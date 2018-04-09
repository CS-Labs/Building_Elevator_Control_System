package control_logic;

import named_types.FloorNumber;
import engine.RenderEntity;

public class FloorSign extends RenderEntity
{

    private FloorNumber m_Floor;
    FloorSign(FloorNumber floor, int x, int y, int d, int w, int h)
    {
        m_Floor = floor;
        setTexture("/resources/img/CCTV_Views/elevator/cabin/floorNumbers/floor" + floor.get() +".png");
        setLocationXYDepth(x, y, d);
        setWidthHeight(w, h);
    }

    @Override
    public void pulse(double deltaSeconds) { setTexture("/resources/img/CCTV_Views/elevator/cabin/floorNumbers/floor" + m_Floor.get() +".png"); }
}
