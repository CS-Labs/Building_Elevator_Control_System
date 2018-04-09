package control_logic;

import named_types.*;
import engine.SceneManager;
import engine.LogicEntity;
import javafx.util.Pair;

class DoorControl implements LogicEntity {
    private Door m_LeftDoor;
    private Door m_RightDoor;
    private SceneManager m_Doors = new SceneManager();

    DoorControl()
    {
        m_LeftDoor = new Door(SideTypes.LEFT,400,80,5,100,250);
        m_RightDoor = new Door(SideTypes.RIGHT, 500,80,5,100,250);
        m_Doors.add(m_LeftDoor);
        m_Doors.add(m_RightDoor);
        m_Doors.activateAll();
        //m_LeftDoor.addToWorld();
        //m_RightDoor.addToWorld();
    }

    public void openDoors()
    {
        m_LeftDoor.changeAnimation(DoorAnimationTypes.OPEN);
        m_RightDoor.changeAnimation(DoorAnimationTypes.OPEN);
    }

    public void closeDoors()
    {
        m_LeftDoor.changeAnimation(DoorAnimationTypes.CLOSE);
        m_RightDoor.changeAnimation(DoorAnimationTypes.CLOSE);
    }


    // TODO Implement me
    @Override
    public void process() {
        if(m_LeftDoor.getCurrentDoorMotion() == DoorStatusType.OPENED) closeDoors();
        if(m_LeftDoor.getCurrentDoorMotion() == DoorStatusType.CLOSED) openDoors();
    }
    public void open(FloorNumber floorNumber, CabinNumber cabinNumber) {}
    public void close(FloorNumber floorNumber, CabinNumber cabinNumber) {}
    public DoorStatusType getStatus(FloorNumber floorNumber, Cabin cabinNumber) {return null;}
    public Pair<Double, Double> getLocationOuterDoors(FloorNumber floorNumber, Cabin cabinNumber) {return null;}
    public Pair<Double, Double> getLocationInnerDoors(FloorNumber floorNumber, Cabin cabinNumber) {return null;}


}
