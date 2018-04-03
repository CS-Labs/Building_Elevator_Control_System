package application;

import control_logic.BuildingControl;
import engine.*;

// lol
public class SetUp implements ApplicationEntryPoint {

    private SceneManager _scene = new SceneManager();

    @Override
    public void init() {
        System.out.println("Initialized");
        m_registerSimulationMessages();
        new ControlPanel();
        //Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_LOGIC_ENTITY, new BuildingControl()));
        _scene.add(new BuildingControl());
        _scene.activateAll();
    }

    @Override
    public void shutdown() {

    }


    private void m_registerSimulationMessages()
    {
        Engine.getMessagePump().registerMessage(new Message(SimGlobals.MANUAL_FLOOR_PRESS_ON));
        Engine.getMessagePump().registerMessage(new Message(SimGlobals.MANUAL_FLOOR_PRESS_OFF));
        Engine.getMessagePump().registerMessage(new Message(SimGlobals.ALARM_ON));
        Engine.getMessagePump().registerMessage(new Message(SimGlobals.ALARM_OFF));
        Engine.getMessagePump().registerMessage(new Message(SimGlobals.CLOSE_DOORS_REQUEST_ON));
        Engine.getMessagePump().registerMessage(new Message(SimGlobals.CLOSE_DOORS_REQUEST_OFF));
        Engine.getMessagePump().registerMessage(new Message(SimGlobals.OPEN_DOORS_REQUEST_ON));
        Engine.getMessagePump().registerMessage(new Message(SimGlobals.OPEN_DOORS_REQUEST_OFF));
    }

    public static void main(String[] args) {
        SetUp app = new SetUp();
        EngineLoop loop = new EngineLoop();
        loop.start(app, args);

    }
}
