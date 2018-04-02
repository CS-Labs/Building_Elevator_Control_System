package control_logic;

import engine.RenderEntity;

class Cabin extends RenderEntity
{
    Cabin(int x, int y, int d, int w, int h)
    {
        setTexture("/resources/img/CCTV_Views/elevator/cabin/cabinFrame.png");
        setLocationXYDepth(x, y, d);
        setWidthHeight(w, h);
    }

    @Override
    public void pulse(double deltaSeconds) {}
}
