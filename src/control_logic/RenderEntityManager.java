package control_logic;


import engine.RenderEntity;
import engine.SceneManager;
import named_types.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

class RenderEntityManager
{
    public DoorPanelRenderer insideDoorLeft;
    public DoorPanelRenderer insideDoorRight;
    public DoorPanelRenderer outsideDoorLeft;
    public DoorPanelRenderer outsideDoorRight;
    public FloorSignRenderer floorSignRenderer;
    public LobbyFloorNumberSignRenderer destinationFloorRenderer;
    public ElevatorButtonPanelRenderer buttonPanelRenderer;
    public ArrivalLightRenderer arrivalLightRenderer;
    public OutsideCabinRenderer cabinOutsideOne;
    public OutsideCabinRenderer cabinOutsideTwo;
    public OutsideCabinRenderer cabinOutsideThree;
    public OutsideCabinRenderer cabinOutsideFour;
    public CabinBackgroundRenderer outsideCabinBackground;
    public CabinBackgroundRenderer insideCabinBackground;
    public ElevatorNumberSignRenderer elevatorNumberSignRenderer;
    private SceneManager m_SystemOverviewMgr = new SceneManager();
    private SceneManager m_ElevatorViewMng = new SceneManager();
    private ArrayList<ArrowButtonRenderer> m_ArrowButtonRenderers = new ArrayList<>();

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


    RenderEntityManager()
    {
        m_ConstructScenes();
    }

    void switchToCabinView()
    {
        m_SystemOverviewMgr.deactivateAll();
        m_ElevatorViewMng.activateAll();
    }
    void switchToSystemOverview()
    {
        m_SystemOverviewMgr.activateAll();
        m_ElevatorViewMng.deactivateAll();
    }

    private void m_ConstructScenes()
    {
        // Add all render entities that go in the single elevator view.
        floorSignRenderer = new FloorSignRenderer(new FloorNumber(1),745,20,3,100,40);
        insideCabinBackground = new CabinBackgroundRenderer(Orientation.INSIDE,600,0,4,400,400);
        outsideCabinBackground = new CabinBackgroundRenderer(Orientation.OUTSIDE, 0,0,4,400,400);
        insideDoorLeft = new DoorPanelRenderer(DoorSideTypes.LEFT,700,80,5,100,250, 639.8, 700,60.2);
        insideDoorRight =  new DoorPanelRenderer(DoorSideTypes.RIGHT,800,80,5,100,250, 800, 856.6, 56.6);
        outsideDoorLeft = new DoorPanelRenderer(DoorSideTypes.LEFT, 100,80,5,100,250, 39.8, 100, 60.2);
        outsideDoorRight =  new DoorPanelRenderer(DoorSideTypes.RIGHT, 200, 80, 5, 100, 250, 200,256.6,56.6);
        buttonPanelRenderer = new ElevatorButtonPanelRenderer(900,107,3,80,150);
        elevatorNumberSignRenderer = new ElevatorNumberSignRenderer(new CabinNumber(1), 600,0,3,100,35);
        destinationFloorRenderer = new LobbyFloorNumberSignRenderer(new FloorNumber(1), 0,0,3,100,35);
        arrivalLightRenderer = (new ArrivalLightRenderer(ArrivalLightStates.NO_ARRIVAL, 175,40,3,50,20));
        m_ElevatorViewMng.add(floorSignRenderer);
        m_ElevatorViewMng.add(insideCabinBackground);
        m_ElevatorViewMng.add(outsideCabinBackground);
        m_ElevatorViewMng.add(insideDoorLeft); // Left inside door.
        m_ElevatorViewMng.add(insideDoorRight); // Right inside door.
        m_ElevatorViewMng.add(outsideDoorLeft); // Left outside door.
        m_ElevatorViewMng.add(outsideDoorRight); // Right outside door.
        m_ElevatorViewMng.add(buttonPanelRenderer);
        m_ElevatorViewMng.add(elevatorNumberSignRenderer);
        m_ElevatorViewMng.add(destinationFloorRenderer);
        m_ElevatorViewMng.add(arrivalLightRenderer);

        // System overview render entities.
        cabinOutsideOne = new OutsideCabinRenderer(120,365,3,40,35);
        cabinOutsideTwo = new OutsideCabinRenderer(260,365,3,40,35);
        cabinOutsideThree = new OutsideCabinRenderer(710,365,3,40,35);
        cabinOutsideFour = new OutsideCabinRenderer(855,365,3,40,35);
        for(int i = 0; i < 10; i++) m_ArrowButtonRenderers.add(new ArrowButtonRenderer(ArrowButtonStates.NOTHING_PRESSED, 950, i*40, 3, 32,38));
        for(ArrowButtonRenderer abRenderer : m_ArrowButtonRenderers) m_SystemOverviewMgr.add(abRenderer);
        m_SystemOverviewMgr.add(new BuildingBackgroundRenderer(0,0,4,1000,400));
        m_SystemOverviewMgr.add(cabinOutsideOne);
        m_SystemOverviewMgr.add(cabinOutsideTwo);
        m_SystemOverviewMgr.add(cabinOutsideThree);
        m_SystemOverviewMgr.add(cabinOutsideFour);

    }

    public void updateCabinLocations(ArrayList<CabinStatus> cabins)
    {
        cabinOutsideOne.updateYLocation(cabins.get(0).getLastFloor());
        cabinOutsideTwo.updateYLocation(cabins.get(1).getLastFloor());
        cabinOutsideThree.updateYLocation(cabins.get(2).getLastFloor());
        cabinOutsideFour.updateYLocation(cabins.get(3).getLastFloor());
    }

    public void updateFloorUpDownPanel(FloorNumber floor, DirectionType direction)
    {
        ArrivalLightStates newLightState;
        //TODO Finish implementing me. Discovered issue; call buttons do not support having both up and down request
    }

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
