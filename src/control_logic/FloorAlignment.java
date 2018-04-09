package control_logic;

import named_types.FloorNumber;

public class FloorAlignment
{
    private final int TOP_FLOOR = 10;
    private final int BOTTOM_FLOOR = 1;
    private FloorNumber m_CurrentFloor;
    private FloorSign m_FloorSign;
    FloorAlignment(FloorNumber startingFloor)
    {
        m_CurrentFloor = startingFloor;
        m_updateFloorSign();

    }

    public void updateFloor(FloorNumber newFloor)
    {
        m_CurrentFloor = newFloor;
        m_FloorSign.removeFromWorld();
        m_updateFloorSign();
    }

    public boolean atTopFloor() {return m_CurrentFloor.get() == TOP_FLOOR;}
    public boolean atBottomFloor() {return m_CurrentFloor.get() == BOTTOM_FLOOR;}

    private void m_updateFloorSign()
    {
        m_FloorSign = new FloorSign(m_CurrentFloor,445,20,3,100,40);
        m_FloorSign.addToWorld();
    }


    //TODO: Implement me
    public int alignedIndex(FloorNumber floor, double cabinHeight) {return -1;}



}
