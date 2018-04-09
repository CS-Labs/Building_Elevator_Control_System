package control_logic;

import application.ControlPanel;
import application.ControlPanelSnapShot;
import engine.LogicEntity;
import engine.RenderEntity;
import engine.SceneManager;
import named_types.FloorNumber;

public class BuildingControl implements LogicEntity
{
    private SceneManager m_Scene = new SceneManager();
    private ControlPanelSnapShot m_ControlPanelSnapShot;
    private ControlPanel m_ControlPanel;
    public BuildingControl(ControlPanel controlPanel)
    {
        m_ControlPanel = controlPanel;
        m_Scene.activateAll();

    }

    @Override
    public void process()
    {
        m_ControlPanelSnapShot = m_ControlPanel.getSnapShot(); // Get latest snap-shot.
        // TODO: Do something with the snap-shot.
    }

    // All animations are now rendered at the top level. (Here).

    /*
        Renders the floor sign above the elevator when viewing from inside.
     */
    class FloorSignRenderer extends RenderEntity
    {
        private FloorNumber m_Floor;
        FloorSignRenderer(FloorNumber floor, int x, int y, int d, int w, int h)
        {
            m_Floor = floor;
            setTexture("/resources/img/CCTV_Views/elevator/cabin/floorNumbers/floor" + floor.get() +".png");
            setLocationXYDepth(x, y, d);
            setWidthHeight(w, h);
        }

        public void updateFloorNumber(FloorNumber floorNumber) {m_Floor = floorNumber;}

        @Override
        public void pulse(double deltaSeconds) { setTexture("/resources/img/CCTV_Views/elevator/cabin/floorNumbers/floor" + m_Floor.get() +".png"); }
    }

    /*
         Renders inside of the cabin.
         Note: Pulse should not be implemented, the image never changes.
     */
    class InsideCabinRenderer extends RenderEntity
    {

        InsideCabinRenderer(int x, int y, int d, int w, int h)
        {
            setTexture("/resources/img/CCTV_Views/elevator/cabin/cabinFrame.png");
            setLocationXYDepth(x, y, d);
            setWidthHeight(w, h);
        }

        @Override
        public void pulse(double deltaSeconds) {}
    }

    /*
        Renders one single door panel, use this for inside and outside doors.
     */
    class DoorPanelRenderer extends RenderEntity
    {
        double m_XPos;
        double m_YPos;
        double m_Depth;
        DoorPanelRenderer(int x, int y, int d, int w, int h)
        {
            m_XPos = x;
            m_YPos = y;
            m_Depth = d;
            setTexture("/resources/img/CCTV_Views/elevator/cabin/cabinFrame.png");
            setLocationXYDepth(m_XPos, m_YPos, m_Depth);
            setWidthHeight(w, h);
        }

        public void updateXLocation(int x) {m_XPos = x;}

        @Override
        public void pulse(double deltaSeconds) { setLocationXYDepth(m_XPos, m_YPos, m_Depth); }
    }


    /*
        Renders the elevator button panel (not the managers one).
     */
    class ElevatorButtonPanelRenderer extends RenderEntity
    {

        @Override
        public void pulse(double deltaSeconds) {

        }
    }

    /*
        Renders the arrival light above the elevator door in
        the outside view.
     */
    class ArrivalLightRenderer extends RenderEntity
    {
        @Override
        public void pulse(double deltaSeconds) {

        }
    }

    /*
        Renders the up/down arrows in the outside view.
     */
    class ArrowButtonRenderer extends RenderEntity
    {
        @Override
        public void pulse(double deltaSeconds) {

        }
    }

    /*
        Renders the outside of the cabin body for
        the overview mode.
     */
    class OutsideCabinRenderer extends RenderEntity
    {

        @Override
        public void pulse(double deltaSeconds) {

        }
    }





}
