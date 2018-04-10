package control_logic;

import application.ControlPanel;
import application.ControlPanelSnapShot;
import application.SystemOverviewPanel;
import engine.LogicEntity;
import engine.RenderEntity;
import engine.SceneManager;
import named_types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BuildingControl implements LogicEntity
{
    private ElevatorAlgorithm ea;
    private SceneManager m_SystemOverviewMgr = new SceneManager();
    private SceneManager m_ElevatorViewMng = new SceneManager();
    private ControlPanelSnapShot m_ControlPanelSnapShot;
    private ControlPanel m_ControlPanel;
    private ViewTypes m_PreviousView = ViewTypes.ELEVATOR_ONE; //TODO: What is our starting view.
    private ArrayList<Cabin> cabins = new ArrayList<>();
    private FloorRequests floorrequests = new FloorRequests();
    private boolean fire = false;

    public BuildingControl(ControlPanel controlPanel)
    {
        // add cabins
        for(int i = 0; i < 4; i+=1){
            cabins.add(i,new Cabin(new CabinNumber(i+1)));
        }
        m_ControlPanel = controlPanel;
        m_ConstructScenes();
        // TODO: What scene do we want to start in? What ever we choose update below.
        m_ElevatorViewMng.activateAll();
    }

    @Override
    public void process()
    {
        ArrayList<CabinStatus> statuses = new ArrayList<>();
        ArrayList<FloorNumber> nextFloors = new ArrayList<>();

        for(Cabin cabin : cabins){
            CabinStatus status = cabin.getStatus();

            statuses.add(status);

            if(status.getMotionStatus() == MotionStatusTypes.STOPPED){
                status.getAllActiveRequests().removeRequest(status.getLastFloor());
                floorrequests.notifyOfArrival(status.getLastFloor(),status.getCabinNumber(),status.getDirection());
            }
        }

        nextFloors = ea.schedule(statuses,floorrequests.getFloorRequests(),fire);

        for(int i = 0; i < 4; i+=1){
            cabins.get(i).getStatus().setDestination(nextFloors.get(i));
        }

        m_ControlPanelSnapShot = m_ControlPanel.getSnapShot(); // Get latest snap-shot.
        // Potentially update views.
        m_UpdateViewCheck(m_ControlPanelSnapShot.currentView);
        // TODO: Do something with the snap-shot.

    }

    private void m_UpdateViewCheck(ViewTypes updatedView)
    {
        if(updatedView == m_PreviousView) return;
        if(updatedView == ViewTypes.OVERVIEW && m_PreviousView != ViewTypes.OVERVIEW) m_SwitchToOverView();
        if(updatedView != ViewTypes.OVERVIEW && m_PreviousView == ViewTypes.OVERVIEW) m_SwitchToCabinView();
        m_PreviousView = updatedView;
    }

    private void m_SwitchToCabinView()
    {
        m_SystemOverviewMgr.deactivateAll();
        m_ElevatorViewMng.activateAll();
        m_ControlPanel.switchToCabinView();
    }
    private void m_SwitchToOverView()
    {
        m_SystemOverviewMgr.activateAll();
        m_ElevatorViewMng.deactivateAll();
        m_ControlPanel.switchToOverView();
    }


    private void m_ConstructScenes()
    {
        // Add all render entities that go in the system overview.

        // Add all render entities that go in the single elevator view.
        m_ElevatorViewMng.add(new FloorSignRenderer(new FloorNumber(1),745,20,3,100,40));
        m_ElevatorViewMng.add(new CabinBackgroundRenderer(Orientation.INSIDE,600,0,4,400,400));
        m_ElevatorViewMng.add(new CabinBackgroundRenderer(Orientation.OUTSIDE, 0,0,4,400,400));
        m_ElevatorViewMng.add(new DoorPanelRenderer(DoorSideTypes.LEFT,700,80,5,100,250)); // Left inside door.
        m_ElevatorViewMng.add(new DoorPanelRenderer(DoorSideTypes.RIGHT,800,80,5,100,250)); // Right inside door.
        m_ElevatorViewMng.add(new DoorPanelRenderer(DoorSideTypes.LEFT, 100,80,5,100,250)); // Left outside door.
        m_ElevatorViewMng.add(new DoorPanelRenderer(DoorSideTypes.RIGHT, 200, 80, 5, 100, 250)); // Right outside door.
        m_ElevatorViewMng.add(new ElevatorButtonPanelRenderer(900,107,3,80,150));
        m_ElevatorViewMng.add(new ElevatorNumberSignRenderer(new CabinNumber(1), 600,0,3,100,35));
        m_ElevatorViewMng.add(new LobbyFloorNumberSignRenderer(new FloorNumber(1), 0,0,3,100,35));
        m_ElevatorViewMng.add(new ArrowButtonRenderer(ArrowButtonStates.NOTHING_PRESSED, 50,150,3,35,75));
        m_ElevatorViewMng.add(new ArrivalLightRenderer(ArrivalLightStates.NO_ARRIVAL, 175,40,3,50,20));

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
            setLocationXYDepth(x, y, d);
            setWidthHeight(w, h);
        }

        public void updateFloorNumber(FloorNumber floorNumber) {m_Floor = floorNumber;}

        @Override
        public void pulse(double deltaSeconds) { setTexture("/resources/img/CCTV_Views/elevator/cabin/floorNumbers/floor" + m_Floor.get() +".png"); }
    }

    /*
        Renders a sign in the cabin that display the cabin number 1-4.
     */
    class ElevatorNumberSignRenderer extends RenderEntity
    {
        CabinNumber m_CabinNumber;
        ElevatorNumberSignRenderer(CabinNumber cabinNumber, int x, int y, int d, int w, int h)
        {
            m_CabinNumber = cabinNumber;
            setLocationXYDepth(x, y, d);
            setWidthHeight(w, h);
        }

        public void setCabinNumber(CabinNumber cabinNumber) {m_CabinNumber = cabinNumber;}

        @Override
        public void pulse(double deltaSeconds) {setTexture("/resources/img/CCTV_Views/elevator/cabin/cabinNumbers/cabin" + m_CabinNumber.get() +".png");
        }
    }

    class LobbyFloorNumberSignRenderer extends RenderEntity
    {
        FloorNumber m_FloorNumber;
        LobbyFloorNumberSignRenderer(FloorNumber floorNumber, int x, int y, int d, int w, int h)
        {
            m_FloorNumber = floorNumber;
            setLocationXYDepth(x, y, d);
            setWidthHeight(w, h);
        }

        public void setFloorNumber(FloorNumber floorNumber) {m_FloorNumber = floorNumber;}

        @Override
        public void pulse(double deltaSeconds) {
            setTexture("/resources/img/CCTV_Views/outside/floorLobbyNumbers/floor" + m_FloorNumber.get() + ".png");
        }
    }



    /*
         Renders inside of the cabin.
         Note: Pulse should not be implemented, the image never changes.
     */
    class CabinBackgroundRenderer extends RenderEntity
    {

        CabinBackgroundRenderer(Orientation org, int x, int y, int d, int w, int h)
        {
            String texture = "/resources/img/CCTV_Views/" + ((org == Orientation.INSIDE) ? "elevator/cabin/cabinFrame.png" : "outside/outside.png");
            setTexture(texture);
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
        DoorPanelRenderer(DoorSideTypes doorSide, int x, int y, int d, int w, int h)
        {
            m_XPos = x;
            m_YPos = y;
            m_Depth = d;
            setTexture("/resources/img/CCTV_Views/elevator/cabin/" + ((doorSide == DoorSideTypes.LEFT) ? "left" : "right") + "Door.png");
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
        private ArrayList<ElevatorButtonRenderer> buttonRenderers = new ArrayList<>();

        ElevatorButtonPanelRenderer(int x, int y, int d, int w, int h)
        {
            setTexture("/resources/img/CCTV_Views/elevator/elevatorFloorPanel/elevatorButtonPanel.png");
            // Create the buttons inside the panel.
            Iterator<Integer> xLocs = Arrays.asList(917,942,917,942,917,942,917,942,917,942).iterator();
            Iterator<Integer> yLocs = Arrays.asList(220,220,195,195,170,170,145,145,120,120).iterator();
            int buttonNum = 1;
            while(xLocs.hasNext() && yLocs.hasNext()) {
                ElevatorButtonRenderer elevatorButton = new ElevatorButtonRenderer(buttonNum, xLocs.next(), yLocs.next(), 2, 20 ,20);
                m_ElevatorViewMng.add(elevatorButton);
                buttonRenderers.add(elevatorButton);
                buttonNum++;
            }
            setLocationXYDepth(x, y, d);
            setWidthHeight(w, h);
        }

        public void turnOnFloorButton(FloorNumber floorNumber) {buttonRenderers.get(floorNumber.get()).turnOn();}
        public void turnOffFloorButton(FloorNumber floorNumber) {buttonRenderers.get(floorNumber.get()).turnOff();}

        @Override
        public void pulse(double deltaSeconds) {

        }
    }

    class ElevatorButtonRenderer extends RenderEntity
    {
        private String m_OnTexture;
        private String m_OffTexture;
        private String m_CurrentTexture;
        ElevatorButtonRenderer(int buttonNum, int x, int y, int d, int w, int h)
        {
            m_OnTexture = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + buttonNum + "ON.png";
            m_OffTexture = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + buttonNum + "OFF.png";
            m_CurrentTexture = m_OffTexture;
            setLocationXYDepth(x, y, d);
            setWidthHeight(w, h);
        }

        public void turnOn() {m_CurrentTexture = m_OnTexture;}
        public void turnOff() {m_CurrentTexture = m_OffTexture;}



        @Override
        public void pulse(double deltaSeconds) {setTexture(m_CurrentTexture);}
    }


    /*
        Renders the arrival light above the elevator door in
        the outside view.
     */
    class ArrivalLightRenderer extends RenderEntity
    {
        ArrivalLightStates m_CurrentArrivalLightState;
        ArrivalLightRenderer(ArrivalLightStates startState, int x, int y, int d, int w, int h)
        {
            m_CurrentArrivalLightState = startState;
            setLocationXYDepth(x, y, d);
            setWidthHeight(w, h);
        }

        public void setArrivalLightState(ArrivalLightStates arrivalLightState) {m_CurrentArrivalLightState = arrivalLightState;}

        @Override
        public void pulse(double deltaSeconds) {setTexture("resources/img/CCTV_Views/outside/arrivalLights/" + m_CurrentArrivalLightState.toString());}
    }

    /*
        Renders the up/down arrows in the outside view. There is only one of these per floor so
        regardless of what elevator your viewing the outside of on a floor it should display the same
        up/down arrow.
     */
    class ArrowButtonRenderer extends RenderEntity
    {
        ArrowButtonStates m_CurrentArrowButton;
        ArrowButtonRenderer(ArrowButtonStates startState, int x, int y, int d, int w, int h)
        {
            m_CurrentArrowButton = startState;
            setLocationXYDepth(x, y, d);
            setWidthHeight(w, h);
        }

        public void setArrowButtonState(ArrowButtonStates arrowButtonState) {m_CurrentArrowButton = arrowButtonState;}

        @Override
        public void pulse(double deltaSeconds) {setTexture("resources/img/CCTV_Views/elevator/cabin/directionLights/" + m_CurrentArrowButton.toString());}
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
