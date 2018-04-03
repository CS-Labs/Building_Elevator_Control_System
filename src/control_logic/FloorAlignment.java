package control_logic;

public class FloorAlignment
{
    private final int TOP_FLOOR = 10;
    private final int BOTTOM_FLOOR = 1;
    private FloorNumberTypes m_CurrentFloor;
    private FloorSign m_FloorSign;
    FloorAlignment(FloorNumberTypes startingFloor)
    {
        m_CurrentFloor = startingFloor;
        m_updateFloorSign();

    }

    public void updateFloor(FloorNumberTypes newFloor)
    {
        m_CurrentFloor = newFloor;
        m_FloorSign.removeFromWorld();
        m_updateFloorSign();
    }

    public boolean atTopFloor() {return m_CurrentFloor.toDigit() == TOP_FLOOR;}
    public boolean atBottomFloor() {return m_CurrentFloor.toDigit() == BOTTOM_FLOOR;}

    private void m_updateFloorSign()
    {
        m_FloorSign = new FloorSign(m_CurrentFloor,445,20,3,100,40);
        m_FloorSign.addToWorld();
    }


}
