package engine;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The engine is a singleton class as there should never be more than
 * one instance during an application. It is responsible for the startup
 * and shutdown of all subsystems which comprise the application, and from
 * there it drives the system in real time (30-60+ updates per second).
 *
 * Notable functions include:
 *      getMessagePump()
 *      getConsoleVariables()
 *
 * A message pump is used to connect the various parts of the application without
 * having to pass hard references to everyone that needs them. Instead, messages
 * are registered and sent and those who are interested will signal interest in
 * them.
 *
 * On the other hand, console variables provide a way to store global state. This
 * state is made up of a variety of input sources which can include the command
 * line, a config file, and input from various objects during initialization. A
 * combination of all of these is well-supported and even expected.
 *
 * Be aware that this class is meant to be the central point of startup for
 * the process, and as such it has implemented a main method.
 *
 * @author Justin Hall
 */
public class Engine implements PulseEntity, MessageHandler {
    private static Engine _engine; // Self-reference
    private static volatile boolean _isInitialized = false;
    // Package private
    static final String R_RENDER_SCENE = "r_render_screen";
    static final String R_UPDATE_ENTITIES = "r_update_entities";

    private Stage _initialStage;
    private HashSet<PulseEntity> _pulseEntities;
    private ApplicationEntryPoint _application;
    private AtomicReference<MessagePump> _messageSystem = new AtomicReference<>();
    private AtomicReference<ConsoleVariables> _cvarSystem = new AtomicReference<>();
    private AtomicReference<TaskManager> _taskManager = new AtomicReference<>();
    private Window _window;
    private Renderer _renderer;
    private ConcurrentHashMap<TaskManager.Counter, Callback> _taskCallbackMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<LogicEntity, LogicEntityTask> _registeredLogicEntities = new ConcurrentHashMap<>();
    private volatile int _maxFrameRate;
    private final int _maxMessageQueueProcessingRate = 240; // Measures in Hertz, i.e. times per second
    private volatile long _lastMessageQueueFrameTimeMS;
    private volatile long _lastFrameTimeMS;
    private volatile boolean _isRunning = false;
    private volatile boolean _updateEntities = true; // If false, nothing is allowed to move

    // Wrapper around each logic entity
    private class LogicEntityTask implements Task {
        private LogicEntity _entity;
        private Engine _engine;
        private long _startTimeNSec;

        LogicEntityTask(LogicEntity entity, Engine engine) {
            _entity = entity;
            _engine = engine;
            _startTimeNSec = System.nanoTime();
        }

        LogicEntity getLogicEntity() {
            return _entity;
        }

        @Override
        public void execute() {
            long currTimeNSec = System.nanoTime();
            long elapsedNSec = currTimeNSec - _startTimeNSec;
            double deltaSeconds = elapsedNSec / 1000000000.0;
            _startTimeNSec = currTimeNSec;
            try {
                _entity.process(deltaSeconds);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            _engine._notifyOfLogicTaskCompletion(this);
        }
    }

    // Make this package private so that only certain classes can create/initialize the
    // engine
    Engine()
    {
    }

    /**
     * Warning! Do not call the MessagePump's dispatch method!
     *
     * This allows other systems to pass messages/register messages/
     * signal interest in message.
     * @return MessagePump for modification
     */
    public static MessagePump getMessagePump()
    {
        return _engine._messageSystem.get();
    }

    /**
     * Returns the console variable listing for viewing/modification
     */
    public static ConsoleVariables getConsoleVariables()
    {
        return _engine._cvarSystem.get();
    }

    /**
     * WARNING: Do not interface with JavaFX from a task on a logic thread. This is almost
     * guaranteed to cause JavaFX to throw an exception.
     *
     * Specifies a list of tasks to run on the engine's logic threads (strictly logic - no graphics!)
     * @param tasks list of functions to run later
     * @param callback this can be null - function to call when the given task has successfully completed
     *                 (this will always be called on the main application thread to avoid synchronization issues)
     */
    public static void scheduleLogicTasks(ArrayList<Task> tasks, Callback callback) {
        TaskManager.Counter counter = _engine._taskManager.get().submitTasks(tasks);
        if (callback != null) {
            _engine._taskCallbackMap.put(counter, callback);
        }
    }

    public void start(ApplicationEntryPoint application) {
        synchronized(this) {
            if (_isRunning) return; // Already running
            // Initialize the engine
            _application = application;
            _preInit();
            _init();
            // Initialize the game loop
            Runnable frame = new Runnable() {
                @Override
                public void run() {
                    if (!_isRunning) {
                        shutdown(); //System.exit(0); // Need to shut the system down
                        return;
                    }
                    // Schedule the next frame
                    Platform.runLater(this);
                    long currentTimeMS = System.currentTimeMillis();
                    double deltaSeconds = (currentTimeMS - _lastFrameTimeMS) / 1000.0;
                    // Don't pulse faster than the maximum refresh rate
                    if (deltaSeconds >= (1.0 / _maxFrameRate)) {
                        pulse(deltaSeconds);
                        _lastFrameTimeMS = currentTimeMS;
                    }
                    // Message processing happens at a very fast rate, i.e. 240 times per second
                    // to ensure high degree of responsiveness
                    deltaSeconds = (currentTimeMS - _lastMessageQueueFrameTimeMS) / 1000.0;
                    if (deltaSeconds >= (1.0 / _maxMessageQueueProcessingRate)) {
                        _processMessages();
                        _processCompletedTasks();
                        _lastMessageQueueFrameTimeMS = currentTimeMS;
                    }
                }
            };
            // Schedule the first frame and then it will schedule itself from then on
            Platform.runLater(frame);
            /*
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (!_isRunning) shutdown(); //System.exit(0); // Need to shut the system down
                    long currentTimeMS = System.currentTimeMillis();
                    double deltaSeconds = (currentTimeMS - _lastFrameTimeMS) / 1000.0;
                    // Don't pulse faster than the maximum frame rate
                    //if (deltaSeconds <= (1.0 / 60)) return;
                    //System.out.println(deltaSeconds);
                    pulse(deltaSeconds);
                    _lastFrameTimeMS = currentTimeMS;
                }
            }.start();
            */
        }
    }

    private void _processMessages() {
        // Check if any console variables changed and send messages for any that have
        ArrayList<ConsoleVariable> changedVars = getConsoleVariables().getVariableChangesSinceLastCall();
        for (ConsoleVariable cvar : changedVars)
        {
            _messageSystem.get().sendMessage(new Message(Singleton.CONSOLE_VARIABLE_CHANGED, cvar));
        }
        // Make sure we keep the messages flowing
        getMessagePump().dispatchMessages();
    }

    private void _processCompletedTasks() {
        // See if any tasks have finished on the logic threads and notify the caller if so
        LinkedList<TaskManager.Counter> _completedCounters = new LinkedList<>();
        // numCounters takes a snapshot of the task callback map so that we are guaranteed to
        // only process a finite number of them during a given frame
        int numCounters = _taskCallbackMap.size();
        for (Map.Entry<TaskManager.Counter, Callback> entry : _taskCallbackMap.entrySet()) {
            if (numCounters == 0) break;
            if (entry.getKey().isComplete()) {
                // Notify of task completion
                entry.getValue().handleCallback();
                _completedCounters.add(entry.getKey());
            }
            --numCounters;
        }
        // Remove any completed counters
        for (TaskManager.Counter counter : _completedCounters) {
            _taskCallbackMap.remove(counter);
        }
    }

    /**
     * Represents the main game/simulation loop
     */
    @Override
    public void pulse(double deltaSeconds) {
        if (_updateEntities) getMessagePump().sendMessage(new Message(Engine.R_UPDATE_ENTITIES, deltaSeconds));
        getMessagePump().sendMessage(new Message(Engine.R_RENDER_SCENE, deltaSeconds));
        for (PulseEntity entity : _pulseEntities)
        {
            entity.pulse(deltaSeconds);
        }
    }

    @Override
    public void handleMessage(Message message) {
        switch(message.getMessageName())
        {
            case Singleton.ADD_PULSE_ENTITY:
                _registerPulseEntity((PulseEntity)message.getMessageData());
                break;
            case Singleton.REMOVE_PULSE_ENTITY:
                _deregisterPulseEntity((PulseEntity)message.getMessageData());
                break;
            case Singleton.REMOVE_ALL_PULSE_ENTITIES:
                _pulseEntities.clear();
                break;
            case Singleton.CONSOLE_VARIABLE_CHANGED:
            {
                ConsoleVariable cvar = (ConsoleVariable)message.getMessageData();
                if (cvar.getcvarName().equals(Singleton.CALCULATE_MOVEMENT))
                {
                    _updateEntities = Boolean.parseBoolean(cvar.getcvarValue());
                }
                break;
            }
            case Singleton.PERFORM_SOFT_RESET:
                System.err.println("Engine: performing an in-place soft reset");
                _softRestart();
                break;
            case Singleton.ADD_LOGIC_ENTITY:
            {
                LogicEntity entity = (LogicEntity)message.getMessageData();
                LogicEntityTask task = new LogicEntityTask(entity, this);
                _registeredLogicEntities.putIfAbsent(entity, task);
                _notifyOfLogicTaskCompletion(task); // This will schedule it on the logic threads
                break;
            }
            case Singleton.REMOVE_LOGIC_ENTITY:
            {
                LogicEntity entity = (LogicEntity)message.getMessageData();
                _registeredLogicEntities.remove(entity);
                break;
            }
        }
    }

    public void shutdown()
    {
        synchronized(this) {
            if (!_isRunning) return; // Not currently running
            _isRunning = false;
            _registeredLogicEntities.clear();
            _application.shutdown();
            _taskManager.get().stop();
            _isInitialized = false;
        }
    }

    // Performs memory allocation of core submodules so that
    // the _init function can safely initialize everything
    private void _preInit()
    {
        synchronized(this) {
            if (_isInitialized) return; // Already initialized
            _isInitialized = true;
            _engine = this; // This is a static variable
            _cvarSystem.set(new ConsoleVariables());
            _messageSystem.set(new MessagePump());
            _pulseEntities = new HashSet<>();
            //_taskManager = new TaskManager();
            _window = new Window();
            _renderer = new Renderer();
            _isRunning = true;
        }
    }

    private void _notifyOfLogicTaskCompletion(LogicEntityTask task) {
        if (!_isRunning) return; // Engine is no longer active
        LogicEntity entity = task.getLogicEntity();
        if (_registeredLogicEntities.containsKey(entity)) {
            ArrayList<Task> tasks = new ArrayList<>();
            tasks.add(task);
            Engine.scheduleLogicTasks(tasks, null);
        }
    }

    // Package private
    boolean _isEngineRunning() {
        return _isRunning;
    }

    // Performs minimal allocations but initializes all submodules in the
    // correct order
    private void _init()
    {
        synchronized(this) {
            getConsoleVariables().loadConfigFile("src/resources/engine.cfg");
            _registerDefaultCVars();
            _maxFrameRate = Math.abs(Engine.getConsoleVariables().find(Singleton.ENG_LIMIT_FPS).getcvarAsInt());
            _registeredLogicEntities.clear();
            boolean headless = true;
            if (!Engine.getConsoleVariables().find(Singleton.HEADLESS).getcvarAsBool()) {
                headless = false;
                if (_initialStage == null) {
                    _initialStage = new Stage();
                    _initialStage.show();
                    _initialStage.setOnCloseRequest((value) -> shutdown());
                }
            }
            _updateEntities = Boolean.parseBoolean(getConsoleVariables().find(Singleton.CALCULATE_MOVEMENT).getcvarValue());
            // Make sure we register all of the message types
            _registerMessageTypes();
            // Signal interest in the things the simulation.engine needs to know about
            getMessagePump().signalInterest(Singleton.ADD_PULSE_ENTITY, this);
            getMessagePump().signalInterest(Singleton.REMOVE_PULSE_ENTITY, this);
            getMessagePump().signalInterest(Singleton.CONSOLE_VARIABLE_CHANGED, this);
            getMessagePump().signalInterest(Singleton.REMOVE_ALL_PULSE_ENTITIES, this);
            getMessagePump().signalInterest(Singleton.PERFORM_SOFT_RESET, this);
            getMessagePump().signalInterest(Singleton.ADD_LOGIC_ENTITY, this);
            getMessagePump().signalInterest(Singleton.REMOVE_LOGIC_ENTITY, this);
            if (_taskManager.get() != null) {
                _taskManager.get().stop();
            }
            _taskManager.set(new TaskManager(getConsoleVariables().find(Singleton.NUM_LOGIC_THREADS).getcvarAsInt()));
            _taskManager.get().start();
            _pulseEntities = new HashSet<>();
            _lastFrameTimeMS = System.currentTimeMillis();
            _lastMessageQueueFrameTimeMS = System.currentTimeMillis();
            // Only initialize the renderer if there is a graphics context
            if (!headless) {
                GraphicsContext gc = _window.init(_initialStage);
                _renderer.init(gc);
            }
            _application.init();
            _maxFrameRate = getConsoleVariables().find(Singleton.ENG_MAX_FPS).getcvarAsInt();
        }
    }

    /**
     * This allows a partial restart to take place. The
     * minimum number of memory allocations to perform this will take place
     * and all submodules (including the ApplicationEntryPoint) will be re-initialized.
     *
     * This will ensure the removal of all render entities, all pulse entities,
     * and all GUI elements. Along with this it will reset the console variables
     * and the entire message pump.
     */
    private void _softRestart()
    {
        synchronized(this) {
            getMessagePump().sendMessage(new Message(Singleton.REMOVE_ALL_RENDER_ENTITIES));
            getMessagePump().sendMessage(new Message(Singleton.REMOVE_ALL_PULSE_ENTITIES));
            getMessagePump().sendMessage(new Message(Singleton.REMOVE_ALL_UI_ELEMENTS));
            // Dispatch the messages immediately
            getMessagePump().dispatchMessages();
            // Reallocate these only
            _cvarSystem.set(new ConsoleVariables());
            _messageSystem.set(new MessagePump());
            _init();
        }
    }

    private void _registerDefaultCVars()
    {
        getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.ENG_MAX_FPS, "60", "60"));
        getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.ENG_LIMIT_FPS, "true", "true"));
        getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.WORLD_START_X, "0", "0"));
        getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.WORLD_START_Y, "0", "0"));
        getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.WORLD_WIDTH, "1000", "0"));
        getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.WORLD_HEIGHT, "1000", "0"));
        getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.CALCULATE_MOVEMENT, "true", "true"));
        getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.NUM_LOGIC_THREADS, "2", "2"));
        getConsoleVariables().registerVariable(new ConsoleVariable(Singleton.HEADLESS, "false", "false"));
    }

    private void _registerMessageTypes()
    {
        getMessagePump().registerMessage(new Message(Singleton.ADD_PULSE_ENTITY));
        getMessagePump().registerMessage(new Message(Singleton.REMOVE_PULSE_ENTITY));
        getMessagePump().registerMessage(new Message(Singleton.ADD_UI_ELEMENT));
        getMessagePump().registerMessage(new Message(Singleton.REMOVE_UI_ELEMENT));
        getMessagePump().registerMessage(new Message(Singleton.SET_FULLSCREEN));
        getMessagePump().registerMessage(new Message(Singleton.SET_SCR_HEIGHT));
        getMessagePump().registerMessage(new Message(Singleton.SET_SCR_WIDTH));
        getMessagePump().registerMessage(new Message(Singleton.ADD_RENDER_ENTITY));
        getMessagePump().registerMessage(new Message(Singleton.REMOVE_RENDER_ENTITY));
        getMessagePump().registerMessage(new Message(Singleton.REGISTER_TEXTURE));
        getMessagePump().registerMessage(new Message(Singleton.SET_MAIN_CAMERA));
        getMessagePump().registerMessage(new Message(Singleton.CONSOLE_VARIABLE_CHANGED));
        getMessagePump().registerMessage(new Message(R_RENDER_SCENE));
        getMessagePump().registerMessage(new Message(R_UPDATE_ENTITIES));
        getMessagePump().registerMessage(new Message(Singleton.REMOVE_ALL_UI_ELEMENTS));
        getMessagePump().registerMessage(new Message(Singleton.REMOVE_ALL_PULSE_ENTITIES));
        getMessagePump().registerMessage(new Message(Singleton.REMOVE_ALL_RENDER_ENTITIES));
        getMessagePump().registerMessage(new Message(Singleton.PERFORM_SOFT_RESET));
        getMessagePump().registerMessage(new Message(Singleton.ADD_LOGIC_ENTITY));
        getMessagePump().registerMessage(new Message(Singleton.REMOVE_LOGIC_ENTITY));
    }

    /**
     * Registers a pulse entity, which is an entity which must be updated once
     * per simulation.engine/simulation frame.
     * @param entity entity to update every frame
     */
    private void _registerPulseEntity(PulseEntity entity)
    {
        _pulseEntities.add(entity);
    }

    private void _deregisterPulseEntity(PulseEntity entity)
    {
        _pulseEntities.remove(entity);
    }
}
