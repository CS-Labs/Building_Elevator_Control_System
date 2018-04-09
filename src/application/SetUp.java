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
        _scene.add(new BuildingControl());
        _scene.activateAll();
    }

    @Override
    public void shutdown() {

    }


    private void m_registerSimulationMessages()
    {
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.MANUAL_FLOOR_PRESS));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.ALARM_ON));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.ALARM_OFF));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.CHANGE_VIEW));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.LOCK_PANEL_UPDATE));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.KEY_LOCK_ACTIVATED));
        Engine.getMessagePump().registerMessage(new Message(ControlPanelGlobals.KEY_LOCK_DEACTIVATED));


    }

    public static void main(String[] args) {
        SetUp app = new SetUp();
        EngineLoop loop = new EngineLoop();
        loop.start(app, args);

    }
}
