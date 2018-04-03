package control_logic;

import engine.Engine;
import engine.Message;
import engine.PulseEntity;
import engine.Singleton;

public class BuildingControl implements PulseEntity
{

    private Cabin m_Cabin;
    private DoorControl m_DoorControl;
    public BuildingControl()
    {
        m_Cabin = new Cabin(300,0,4,400,400);
        m_DoorControl = new DoorControl();
        m_Cabin.addToWorld();
        m_DoorControl.openDoors();
        Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_LOGIC_ENTITY, m_DoorControl));
    }

    @Override
    public void pulse(double deltaSeconds) {
    }
}
