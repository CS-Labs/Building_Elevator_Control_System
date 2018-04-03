package control_logic;

import engine.RenderEntity;

class Cabin extends RenderEntity
{
    FloorAlignment m_FloorAlignment;
    CabinRequests m_CabinRequests;
    MotionControl m_MotionControl;
    Cabin(int x, int y, int d, int w, int h)
    {
        setTexture("/resources/img/CCTV_Views/elevator/cabin/cabinFrame.png");
        setLocationXYDepth(x, y, d);
        setWidthHeight(w, h);
        m_FloorAlignment = new FloorAlignment(FloorNumberTypes.ONE);
    }

    @Override
    public void pulse(double deltaSeconds) {}
}
