package control_logic;

import application.ControlPanel;
import application.ControlPanelSnapShot;
import engine.LogicEntity;
import engine.RenderEntity;
import engine.SceneManager;
import named_types.*;

import java.util.*;

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
    private DoorControl _doorControl;
    private SceneManager _alwaysActive;
    private DoorPanelRenderer m_InsideDoorLeft;
    private DoorPanelRenderer m_InsideDoorRight;
    private DoorPanelRenderer m_OutsideDoorLeft;
    private DoorPanelRenderer m_OutsideDoorRight;
    private FloorSignRenderer m_FloorSignRenderer;
    private LobbyFloorNumberSignRenderer m_DestinationFloorRenderer;
    private ElevatorButtonPanelRenderer m_ButtonPanelRenderer;
    private ArrivalLightRenderer m_ArrivalLightRenderer;
    private HashSet<FloorNumber> _floorRequests = new HashSet<>(); // TODO: Remove this for real algorithm
    private LinkedList<FloorNumber> _acceptedRequests = new LinkedList<>();
    private OutsideCabinRenderer m_CabinOutsideOne;
    private OutsideCabinRenderer m_CabinOutsideTwo;
    private OutsideCabinRenderer m_CabinOutsideThree;
    private OutsideCabinRenderer m_CabinOutsideFour;

    HashMap<FloorNumber, Double> floorsToYLoc = new HashMap<FloorNumber, Double>() {{
        put(new FloorNumber(1), 360.0);
        put(new FloorNumber(2), 320.0);
        put(new FloorNumber(3), 280.0);
        put(new FloorNumber(4), 240.0);
        put(new FloorNumber(5), 200.0);
        put(new FloorNumber(6), 160.0);
        put(new FloorNumber(7), 120.0);
        put(new FloorNumber(8), 80.0);
        put(new FloorNumber(9), 40.0);
        put(new FloorNumber(10), 0.0);
    }};

    // TODO Implement this better? This is just for the demo.
    private double doorCloseTime = 5.0;
    private boolean wasOpened = false;
    private int previousDest = -1;

    public BuildingControl(ControlPanel controlPanel)
    {
        // add cabins
        //TODO setback to four cabins after the demo.
        for(int i = 0; i < 1; i+=1){
            cabins.add(i,new Cabin(new CabinNumber(i+1)));
        }
        m_ControlPanel = controlPanel;
        m_ConstructScenes();
        // TODO: What scene do we want to start in? What ever we choose update below.
        m_ElevatorViewMng.activateAll();
        // TODO maybe remove below for something more permanent?
        _doorControl = new DoorControl(10, 4);
        _alwaysActive = new SceneManager();
        _alwaysActive.add(_doorControl);
        _alwaysActive.activateAll();
    }


    @Override
    public void process(double deltaSeconds)
    {
        ArrayList<CabinStatus> statuses = new ArrayList<>();
        ArrayList<FloorNumber> nextFloors = new ArrayList<>();

        for(Cabin cabin : cabins){
            CabinStatus status = cabin.getStatus();

            statuses.add(status);

            if(status.getLastFloor() == status.getDestination() && status.getMotionStatus() == MotionStatusTypes.STOPPED){
                status.getAllActiveRequests().removeRequest(status.getLastFloor());
                floorrequests.notifyOfArrival(status.getLastFloor(),status.getCabinNumber(),status.getDirection());
            }
        }

        /* TODO implement algorithm
        nextFloors = ea.schedule(statuses,floorrequests.getFloorRequests(),fire);

        for(int i = 0; i < 4; i+=1){
            cabins.get(i).getStatus().setDestination(nextFloors.get(i));
        }
        */


        m_ControlPanelSnapShot = m_ControlPanel.getSnapShot(); // Get latest snap-shot.
        // Potentially update views.
        m_UpdateViewCheck(m_ControlPanelSnapShot.currentView);

        if(m_ControlPanelSnapShot.upDownEvent.getKey() != DirectionType.NONE)
        {
            cabins.get(0).setDestination(m_ControlPanelSnapShot.upDownEvent.getValue());
        }

        // dumb algorithm just for demo, only works with one cabin and a single request at a time.
        CabinNumber cabin = new CabinNumber(1);
        FloorNumber floor = new FloorNumber(1);
        if(m_ControlPanelSnapShot.manualFloorsPresses.size() != 0)
        {
            // Try to insert the floor request if it has not already been requested
            FloorNumber floorReq = new FloorNumber(m_ControlPanelSnapShot.manualFloorsPresses.get(0));
            if (!_floorRequests.contains(floorReq)) {
                _floorRequests.add(floorReq);
                _acceptedRequests.add(floorReq);
            }
            //cabins.get(0).setDestination(new FloorNumber(m_ControlPanelSnapShot.manualFloorsPresses.get(0)));
        }
        CabinStatus cs = cabins.get(0).getStatus();
        m_FloorSignRenderer.updateFloorNumber(cs.getLastFloor());
        m_CabinOutsideOne.updateYLocation(cs.getLastFloor());
        if(cs.getDestination().get() > 0) {
            m_DestinationFloorRenderer.setFloorNumber(cs.getDestination());
            if (cs.getDestination().get() != previousDest)
            {
                previousDest = cs.getDestination().get();
                wasOpened = false;
            }
        }
        if(cs.getDestination().equals(cs.getLastFloor()) && cs.getMotionStatus() == MotionStatusTypes.STOPPED) // WE MADE IT
        {
            m_ButtonPanelRenderer.turnOffFloorButton(cs.getDestination());
            double innerPercentage = _doorControl.getInnerDoorsPercentageOpen(cabin);
            double outerPercentage = _doorControl.getOuterDoorsPercentageOpen(floor, cabin);
            DoorStatusType status = _doorControl.getStatus(floor, cabin);
            if(!wasOpened)
            {
                _doorControl.open(floor, cabin);
                if(cs.getDirection() == DirectionType.UP) m_ArrivalLightRenderer.setArrivalLightState(ArrivalLightStates.ARRIVAL_GOING_UP);
                if(cs.getDirection() == DirectionType.DOWN) m_ArrivalLightRenderer.setArrivalLightState(ArrivalLightStates.ARRIVAL_GOING_DOWN);
            }
            else
            {
                _doorControl.close(floor,cabin);
                m_ArrivalLightRenderer.setArrivalLightState(ArrivalLightStates.NO_ARRIVAL);
            }
            m_InsideDoorLeft.update(innerPercentage, status);
            m_InsideDoorRight.update(innerPercentage, status);
            m_OutsideDoorLeft.update(outerPercentage, status);
            m_OutsideDoorRight.update(outerPercentage, status);
            doorCloseTime += deltaSeconds;
            //if(doorCloseTime > 15)
            if (doorCloseTime > 15 && _doorControl.getStatus(floor, cabin) == DoorStatusType.OPENED)
            {
                wasOpened = true;
                doorCloseTime = 0.0;
            }

        }

        for(FloorNumber i : _acceptedRequests) m_ButtonPanelRenderer.turnOnFloorButton(i);
        // If this is true, the cabin is both stopped and the doors are closed so it is safe to
        // assign it a new destination if there is one
        if(cs.getMotionStatus() == MotionStatusTypes.STOPPED &&
                _doorControl.getStatus(floor, cabin) == DoorStatusType.CLOSED) {
            if (_acceptedRequests.size() > 0) {
                FloorNumber floorReq = _acceptedRequests.poll();
                _floorRequests.remove(floorReq);
                cabins.get(0).setDestination(floorReq);
                System.out.println("Remaining requests (in queue): " + _acceptedRequests);

            }
        }



        // End dumb algorithm
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
        m_FloorSignRenderer = new FloorSignRenderer(new FloorNumber(1),745,20,3,100,40);
        m_ElevatorViewMng.add(m_FloorSignRenderer);
        m_ElevatorViewMng.add(new CabinBackgroundRenderer(Orientation.INSIDE,600,0,4,400,400));
        m_ElevatorViewMng.add(new CabinBackgroundRenderer(Orientation.OUTSIDE, 0,0,4,400,400));

        // Mappings of door open/close percentages to x positions. The indexes in the array correspond to
        m_InsideDoorLeft = new DoorPanelRenderer(DoorSideTypes.LEFT,700,80,5,100,250, 639.8, 700,60.2);
        m_InsideDoorRight =  new DoorPanelRenderer(DoorSideTypes.RIGHT,800,80,5,100,250, 800, 856.6, 56.6);
        m_OutsideDoorLeft = new DoorPanelRenderer(DoorSideTypes.LEFT, 100,80,5,100,250, 39.8, 100, 60.2);
        m_OutsideDoorRight =  new DoorPanelRenderer(DoorSideTypes.RIGHT, 200, 80, 5, 100, 250, 200,256.6,56.6);

        m_ElevatorViewMng.add(m_InsideDoorLeft); // Left inside door.
        m_ElevatorViewMng.add(m_InsideDoorRight); // Right inside door.
        m_ElevatorViewMng.add(m_OutsideDoorLeft); // Left outside door.
        m_ElevatorViewMng.add(m_OutsideDoorRight); // Right outside door.

        m_ButtonPanelRenderer = new ElevatorButtonPanelRenderer(900,107,3,80,150);
        m_ElevatorViewMng.add(m_ButtonPanelRenderer);
        m_ElevatorViewMng.add(new ElevatorNumberSignRenderer(new CabinNumber(1), 600,0,3,100,35));
        m_DestinationFloorRenderer = new LobbyFloorNumberSignRenderer(new FloorNumber(1), 0,0,3,100,35);
        m_ElevatorViewMng.add(m_DestinationFloorRenderer);
        m_ArrivalLightRenderer = (new ArrivalLightRenderer(ArrivalLightStates.NO_ARRIVAL, 175,40,3,50,20));
        m_ElevatorViewMng.add(m_ArrivalLightRenderer);

        m_SystemOverviewMgr.add(new BuildingBackgroundRenderer(0,0,4,1000,400));
        for(int i = 0; i < 10; i++) m_SystemOverviewMgr.add(new ArrowButtonRenderer(ArrowButtonStates.NOTHING_PRESSED, 950, i*40, 3, 32,38));

        m_CabinOutsideOne = new OutsideCabinRenderer(120,365,3,40,35);
        m_CabinOutsideTwo = new OutsideCabinRenderer(260,365,3,40,35);
        m_CabinOutsideThree = new OutsideCabinRenderer(710,365,3,40,35);
        m_CabinOutsideFour = new OutsideCabinRenderer(855,365,3,40,35);

        m_SystemOverviewMgr.add(m_CabinOutsideOne);
        m_SystemOverviewMgr.add(m_CabinOutsideTwo);
        m_SystemOverviewMgr.add(m_CabinOutsideThree);
        m_SystemOverviewMgr.add(m_CabinOutsideFour);

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
        double m_XInit;
        double m_XPos;
        double m_YPos;
        double m_Depth;
        double m_LBound;
        double m_HBound;
        double m_Width;
        DoorSideTypes m_Side;
        DoorPanelRenderer(DoorSideTypes doorSide, int x, int y, int d, int w, int h, double lBound, double hBound, double width)
        {
            m_XPos = x;
            m_XInit = x;
            m_YPos = y;
            m_Depth = d;
            m_Side = doorSide;
            m_LBound = lBound;
            m_HBound = hBound;
            m_Width = width;
            setTexture("/resources/img/CCTV_Views/elevator/cabin/" + ((doorSide == DoorSideTypes.LEFT) ? "left" : "right") + "Door.png");
            setLocationXYDepth(m_XPos, m_YPos, m_Depth);
            setWidthHeight(w, h);
        }


        public void update(double percentage, DoorStatusType status)
        {
            /**
            if(status == DoorStatusType.OPENING && m_Side == DoorSideTypes.LEFT) m_XPos = m_HBound - (percentage * m_Width);
            if(status == DoorStatusType.OPENING && m_Side == DoorSideTypes.RIGHT) m_XPos = m_LBound + (percentage * m_Width);
            if(status == DoorStatusType.CLOSING && m_Side == DoorSideTypes.LEFT) m_XPos = m_LBound + (percentage * m_Width);
            if(status == DoorStatusType.CLOSING && m_Side == DoorSideTypes.RIGHT) m_XPos = m_HBound - (percentage * m_Width);
             */
            if (m_Side == DoorSideTypes.LEFT) m_XPos = m_HBound - (percentage * m_Width);
            if (m_Side == DoorSideTypes.RIGHT) m_XPos = m_LBound + (percentage * m_Width);
        }

        @Override
        public void pulse(double deltaSeconds) {
            setLocationXYDepth(m_XPos, m_YPos, m_Depth);
        }
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

        public void turnOnFloorButton(FloorNumber floorNumber) {buttonRenderers.get(floorNumber.get()-1).turnOn();}
        public void turnOffFloorButton(FloorNumber floorNumber) {buttonRenderers.get(floorNumber.get()-1).turnOff();}

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
        double m_XPos;
        double m_YPos;
        double m_Depth;

        OutsideCabinRenderer(int x, int y, int d, int w, int h)
        {
            m_XPos = x;
            m_YPos = y;
            m_Depth = d;
            setTexture("resources/img/Building_Overview/cabinOutside.png");
            setLocationXYDepth(m_XPos, m_YPos, m_Depth);
            setWidthHeight(w, h);
        }

        public void updateYLocation(FloorNumber n) {m_YPos = floorsToYLoc.get(n);}

        @Override
        public void pulse(double deltaSeconds) {setLocationXYDepth(m_XPos, m_YPos , m_Depth);}
    }


    class BuildingBackgroundRenderer extends RenderEntity
    {

        BuildingBackgroundRenderer(int x, int y, int d, int w, int h)
        {
            setTexture("resources/img/background3.png");
            setLocationXYDepth(x, y, d);
            setWidthHeight(w, h);
        }
        @Override
        public void pulse(double deltaSeconds) {
        }
    }




}
