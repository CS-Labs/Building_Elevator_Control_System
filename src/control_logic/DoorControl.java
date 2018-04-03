package control_logic;

import engine.LogicEntity;

class DoorControl implements LogicEntity {
    private Door m_LeftDoor;
    private Door m_RightDoor;
    DoorControl()
    {
        m_LeftDoor = new Door(SideTypes.LEFT,400,80,5,100,250);
        m_RightDoor = new Door(SideTypes.RIGHT, 500,80,5,100,250);
        m_LeftDoor.addToWorld();
        m_RightDoor.addToWorld();
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


    @Override
    public void process() {
        if(m_LeftDoor.getCurrentDoorMotion() == DoorStatusType.OPENED) closeDoors();
        if(m_LeftDoor.getCurrentDoorMotion() == DoorStatusType.CLOSED) openDoors();
    }
}
