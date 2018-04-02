package control_logic;

import engine.RenderEntity;

public class Door extends RenderEntity
{

    private double m_XSpeed = 0.0;
    private DoorAnimation m_DoorAnimation = DoorAnimation.NONE;
    // Need to be determined, these are dependent on the door being either the right or left door.
    private double m_OpenBound;
    private double m_ClosedBound;

    Door(Side door, int x, int y, int d, int w, int h)
    {
        setTexture("/resources/img/CCTV_Views/elevator/cabin/" + ((door == Side.LEFT) ? "left" : "right") + "Door.png");
        setLocationXYDepth(x, y, d);
        setSpeedXY(m_XSpeed, 0);
        setWidthHeight(w, h);
    }

    public void changeAnimation(DoorAnimation doorAnimation) { m_DoorAnimation = doorAnimation;}


    @Override
    public void pulse(double deltaSeconds) {
        switch(m_DoorAnimation){
            case OPEN:
                break; // Open animation..
            case CLOSE:
                break; // Close animation..
            case NONE:
            default:
                // No animation.
        }
    }
}
