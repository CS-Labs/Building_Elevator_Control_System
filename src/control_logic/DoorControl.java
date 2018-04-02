package control_logic;

class DoorControl
{
    private Door m_LeftDoor;
    private Door m_RightDoor;
    DoorControl()
    {
        m_LeftDoor = new Door(Side.LEFT,400,80,5,100,250);
        m_RightDoor = new Door(Side.RIGHT, 500,80,5,100,250);
        m_LeftDoor.addToWorld();
        m_RightDoor.addToWorld();
    }

    public void openDoors()
    {
        m_LeftDoor.changeAnimation(DoorAnimation.OPEN);
        m_RightDoor.changeAnimation(DoorAnimation.OPEN);
    }

    public void closeDoors()
    {
        m_LeftDoor.changeAnimation(DoorAnimation.CLOSE);
        m_RightDoor.changeAnimation(DoorAnimation.CLOSE);
    }


}
