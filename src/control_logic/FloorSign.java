package control_logic;

import engine.RenderEntity;

public class FloorSign extends RenderEntity
{

    private FloorNumberTypes m_Floor;
    FloorSign(FloorNumberTypes floor, int x, int y, int d, int w, int h)
    {
        m_Floor = floor;
        setTexture("/resources/img/CCTV_Views/elevator/cabin/floorNumbers/floor" + floor.toDigit() +".png");
        setLocationXYDepth(x, y, d);
        setWidthHeight(w, h);
    }

    @Override
    public void pulse(double deltaSeconds) { setTexture("/resources/img/CCTV_Views/elevator/cabin/floorNumbers/floor" + m_Floor.toDigit() +".png"); }
}
