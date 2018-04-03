package control_logic;

import application.SimGlobals;
import engine.*;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.Collections;

public class BuildingControl implements LogicEntity
{

    private Cabin m_Cabin;
    private DoorControl m_DoorControl;
    private Helper m_Helper = new Helper();
    private ArrayList<Boolean> m_ManualFloorRequests = new ArrayList<>(Collections.nCopies(10, false));
    private SceneManager m_Scene = new SceneManager();
    public BuildingControl()
    {
        m_Cabin = new Cabin(300,0,4,400,400);
        m_DoorControl = new DoorControl();
        //m_Cabin.addToWorld();
        m_DoorControl.openDoors();
        //Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_LOGIC_ENTITY, m_DoorControl));
        m_Scene.add(m_Cabin);
        m_Scene.add(m_DoorControl);
        m_Scene.activateAll();
        Engine.getMessagePump().signalInterest(SimGlobals.MANUAL_FLOOR_PRESS_ON, m_Helper);
        Engine.getMessagePump().signalInterest(SimGlobals.MANUAL_FLOOR_PRESS_OFF,m_Helper);
        Engine.getMessagePump().signalInterest(SimGlobals.ALARM_ON,m_Helper);
        Engine.getMessagePump().signalInterest(SimGlobals.ALARM_OFF,m_Helper);
        Engine.getMessagePump().signalInterest(SimGlobals.CLOSE_DOORS_REQUEST_ON,m_Helper);
        Engine.getMessagePump().signalInterest(SimGlobals.CLOSE_DOORS_REQUEST_OFF,m_Helper);
        Engine.getMessagePump().signalInterest(SimGlobals.OPEN_DOORS_REQUEST_ON,m_Helper);
        Engine.getMessagePump().signalInterest(SimGlobals.OPEN_DOORS_REQUEST_OFF,m_Helper);

    }

    @Override
    public void process() {
        // Just to demo availability.
        if(m_ManualFloorRequests.contains(true))
        {
            System.out.print("Manually Requested Floors: ");
            for(int i = 0; i < m_ManualFloorRequests.size(); i++)
            {
                if(m_ManualFloorRequests.get(i)) System.out.print(" " + i + " ");
            }
            System.out.println("");
        }
    }

    class Helper implements MessageHandler {
        @Override
        public void handleMessage(Message message) {
            switch (message.getMessageName()) {
                case SimGlobals.MANUAL_FLOOR_PRESS_ON:
                    FloorNumberTypes onFloor = (FloorNumberTypes) message.getMessageData();
                    m_ManualFloorRequests.set(onFloor.toDigit(), true);
                    break;
                case SimGlobals.MANUAL_FLOOR_PRESS_OFF:
                    FloorNumberTypes offFloor = (FloorNumberTypes) message.getMessageData();
                    m_ManualFloorRequests.set(offFloor.toDigit(), false);
                    break;
                default:
                    break;
            }
        }
    }
}
