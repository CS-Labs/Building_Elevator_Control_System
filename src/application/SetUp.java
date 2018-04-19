package application;

import control_logic.BuildingControl;
import engine.ApplicationEntryPoint;
import engine.Engine;
import engine.EngineLoop;
import engine.Message;
import engine.SceneManager;

// lol
public class SetUp implements ApplicationEntryPoint {

    private SceneManager _scene = new SceneManager();

    @Override
    public void init() {
        System.out.println("Initialized");
        m_registerSimulationMessages();
        _scene.add(new BuildingControl(new ControlPanel()));
        _scene.activateAll();
    }

    @Override
    public void shutdown() {

    }


    private void m_registerSimulationMessages()
    {
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.MANUAL_FLOOR_PRESS));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.ALARM_PRESS));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.CHANGE_VIEW));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.LOCK_PANEL_UPDATE));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.KEY_LOCK_CHANGE));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.MANAGER_UP));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.MANAGER_DOWN));
    }

    public static void main(String[] args) {
        SetUp app = new SetUp();
        EngineLoop loop = new EngineLoop();
        loop.start(app, args);

    }
}
