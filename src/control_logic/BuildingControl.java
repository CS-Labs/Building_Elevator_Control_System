package control_logic;

import application.ControlPanel;
import engine.LogicEntity;
import engine.RenderEntity;
import engine.SceneManager;
import application.ControlPanelSnapShot;
public class BuildingControl implements LogicEntity
{

    private Cabin m_Cabin;
    private DoorControl m_DoorControl;
    private SceneManager m_Scene = new SceneManager();
    private ControlPanelSnapShot m_ControlPanelSnapShot;
    private ControlPanel m_ControlPanel;
    public BuildingControl(ControlPanel controlPanel)
    {
        m_ControlPanel = controlPanel;
        m_Cabin = new Cabin(300,0,4,400,400);
        m_DoorControl = new DoorControl();
        m_DoorControl.openDoors();
        m_Scene.add(m_Cabin);
        m_Scene.add(m_DoorControl);
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
        @Override
        public void pulse(double deltaSeconds) {

        }
    }

    /*
         Renders inside of the cabin.
         Note: Pulse should not be implemented, the image never changes.
     */
    class InsideCabinRenderer extends RenderEntity
    {

        @Override
        public void pulse(double deltaSeconds) {}
    }

    /*
        Renders one of the inside door panels.
     */
    class InsideDoorPanelRenderer extends RenderEntity
    {

        @Override
        public void pulse(double deltaSeconds) {

        }
    }

    /*
        Renders one of the outside door panels.
     */
    class OutsideDoorPanelRenderer extends RenderEntity
    {

        @Override
        public void pulse(double deltaSeconds) {

        }
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
