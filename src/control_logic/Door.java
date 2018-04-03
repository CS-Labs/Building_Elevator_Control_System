package control_logic;

import engine.RenderEntity;

public class Door extends RenderEntity
{

    private double m_MovementSpeed = 0.0;
    private DoorAnimationTypes m_DoorAnimation = DoorAnimationTypes.NONE;
    private DoorStatusType m_DoorStatus = DoorStatusType.CLOSED;
    // Need to be determined, these are dependent on the door being either the right or left door.
    private double m_OpenBound;
    private double m_ClosedBound;
    private SideTypes  m_Side;

    Door(SideTypes door, int x, int y, int d, int w, int h)
    {
        m_Side = door;
        setTexture("/resources/img/CCTV_Views/elevator/cabin/" + ((m_Side == SideTypes.LEFT) ? "left" : "right") + "Door.png");
        setLocationXYDepth(x, y, d);
        setWidthHeight(w, h);
        if(m_Side == SideTypes.LEFT)
        {
            m_OpenBound = 338.871;
            m_ClosedBound = 399.431;
            m_MovementSpeed = -7;
        }
        if(m_Side == SideTypes.RIGHT)
        {
            m_OpenBound = 561.128;
            m_ClosedBound = 500.569;
            m_MovementSpeed = 7;
        }

    }

    public void changeAnimation(DoorAnimationTypes doorAnimation) { m_DoorAnimation = doorAnimation;}

    public DoorStatusType getCurrentDoorMotion() { return m_DoorStatus; }

    @Override
    public void pulse(double deltaSeconds) {
        switch(m_DoorAnimation){
            case OPEN:
                if(m_Side == SideTypes.LEFT && getLocationX() > m_OpenBound) {
                    setSpeedXY(m_MovementSpeed,0);
                    m_DoorStatus = DoorStatusType.OPENING;
                }
                else if(m_Side == SideTypes.RIGHT && getLocationX() < m_OpenBound) {
                    setSpeedXY(m_MovementSpeed,0);
                    m_DoorStatus = DoorStatusType.OPENING;
                }
                else {
                    setSpeedXY(0,0);
                    m_DoorStatus = DoorStatusType.OPENED;
                }
                break; // Open animation..
            case CLOSE:
                if(m_Side == SideTypes.LEFT && getLocationX() < m_ClosedBound) {
                    setSpeedXY((-1)*m_MovementSpeed,0);
                    m_DoorStatus = DoorStatusType.CLOSING;
                }
                else if(m_Side == SideTypes.RIGHT && getLocationX() > m_ClosedBound) {
                    setSpeedXY((-1)*m_MovementSpeed,0);
                    m_DoorStatus = DoorStatusType.CLOSING;
                }
                else {
                    setSpeedXY(0,0);
                    m_DoorStatus = DoorStatusType.CLOSED;
                }
                break; // Close animation..
            case NONE:
            default:
                // No animation.
        }
    }
}
