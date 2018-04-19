package control_logic;

import application.ControlPanel;
import application.ControlPanelSnapShot;
import engine.LogicEntity;
import engine.SceneManager;
import javafx.util.Pair;
import named_types.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BuildingControl implements LogicEntity
{
    private ElevatorAlgorithm ea;

    private ControlPanelSnapShot m_ControlPanelSnapShot;
    private ControlPanel m_ControlPanel;
    private ViewTypes m_PreviousView = ViewTypes.OVERVIEW; //TODO: What is our starting view.
    private ArrayList<Cabin> cabins = new ArrayList<>();
    private FloorRequests floorrequests = new FloorRequests();
    private DoorControl _doorControl;
    private SceneManager _alwaysActive;
    private RenderEntityManager m_RenderEntityManager;
//    private AtomicBoolean interference = new AtomicBoolean(false);
    private BuildingFireAlarm alarm;
    private ArrayList<CabinStatus> m_Statuses = new ArrayList<>();
    private ArrayList<FloorNumber> m_NextFloors;
    private boolean fire = false;
    private boolean manualFireAlarmPress = false;



    private int previousDest = -1;

    public BuildingControl(ControlPanel controlPanel)
    {
        // add cabins
        for(int i = 0; i < 4; i+=1){
            cabins.add(i,new Cabin(new CabinNumber(i+1)));
        }
        ea = new ElevatorAlgorithm(cabins);
        m_ControlPanel = controlPanel;
        _doorControl = new DoorControl(this, 10, 4);
        _alwaysActive = new SceneManager();
        _alwaysActive.add(_doorControl);
        _alwaysActive.activateAll();
        alarm = new BuildingFireAlarm();
        m_RenderEntityManager = new RenderEntityManager();
        m_RenderEntityManager.switchToSystemOverview();

    }
    


    @Override
    public void process(double deltaSeconds)
    {
        // Clear previous cabin statuses
        m_Statuses.clear();
        // Get the latest control panel snap shot.
        m_ControlPanelSnapShot = m_ControlPanel.getSnapShot();
        ViewTypes currentView = m_ControlPanelSnapShot.currentView;
        // Get the latest cabin snap shots.
        for(Cabin cabin : cabins){
            CabinStatus status = cabin.getStatus();
            m_Statuses.add(status);
            // If the cabin has arrived at it's destination notify of arrival.
            if(status.getLastFloor() == status.getDestination() && status.getMotionStatus() == MotionStatusTypes.STOPPED){
                // Upon arrival;
                // 0. Remove request from cabin.
                // 1. Signal of arrival.
                // 2. Open doors.
                // 3. Close doors. (WHILE checking if interference has been detected).
                //    a. If interference is detected open doors (go to step 2).
                // 4. Allow Cabin to start moving to service next request.  (signal the algorithm).
                cabin.removeRequest(status.getLastFloor()); // Remove cabin request.
                floorrequests.notifyOfArrival(status.getLastFloor(),status.getCabinNumber(), status.getDirection()); // Signal arrival.
                m_RenderEntityManager.buttonPanelRenderer.turnOffFloorButton(status.getLastFloor()); // Turn off button light.
                ArrivalLightStates light;
                if(status.getDirection() == DirectionType.UP) light = ArrivalLightStates.ARRIVAL_GOING_UP;
                else if(status.getDirection() == DirectionType.DOWN) light = ArrivalLightStates.ARRIVAL_GOING_DOWN;
                else light = ArrivalLightStates.NO_ARRIVAL;
                m_RenderEntityManager.arrivalLightRenderer.setArrivalLightState(light);
                //TODO add opening doors.
                //TODO add closing doors and turning off arrival light visual. (only turn off light when fully closed).
                if(currentView != ViewTypes.OVERVIEW && _doorControl.interferenceDetected())
                {
                    //TODO open doors again.
                }
            }
        }

        // If the user is viewing the inside of one of the cabins then render the cabin.
        if(currentView != ViewTypes.OVERVIEW)
        {
            CabinStatus visibleCabin = m_Statuses.get(currentView.toInt()-1);
            m_RenderEntityManager.floorSignRenderer.updateFloorNumber(visibleCabin.getLastFloor());
            for(FloorNumber fr : visibleCabin.getAllActiveRequests())  m_RenderEntityManager.buttonPanelRenderer.turnOnFloorButton(fr);
        }


        // First check if a random fire has taken place.
        // Next check if the manager has pressed the fire alarm button.
        // Finally perform a time step in the alarm logic.
        alarm.fireCheck();
        alarm.managerPressCheck(m_ControlPanelSnapShot.isAlarmOn);
        alarm.step();

        // Potentially update views.
        m_UpdateViewCheck(m_ControlPanelSnapShot.currentView);
        // Update Cabin locations.
        m_RenderEntityManager.updateCabinLocations(m_Statuses);


        // Merge any requests from the manager with any randomly generated ones.
        ArrayList<CallButtons> callButtons = floorrequests.getFloorRequests();
        if(m_ControlPanelSnapShot.currentView != ViewTypes.OVERVIEW)
        {
            CabinStatus inViewCabin = m_Statuses.get(m_ControlPanelSnapShot.currentView.toInt()-1);
            HashSet<FloorNumber> requests = inViewCabin.getAllActiveRequests();
            requests.addAll(m_ControlPanelSnapShot.manualFloorsPresses);
            inViewCabin.setRequests(requests);
            Pair<DirectionType, FloorNumber> managerRequest = m_ControlPanelSnapShot.upDownEvent;
            if(managerRequest.getKey() != DirectionType.NONE)
            {
                CallButtons managerCallButton = new CallButtons(managerRequest.getValue(), managerRequest.getKey());
                managerCallButton.setButtonPressedState(true);
                callButtons.add(managerCallButton);
            }
        }
        // Wipe requests for locked cabins or all cabins if a fire has occurred before sending it to the algorithm.
        // TODO This does not clear any CallButtons. It might be better to merge call buttons into the requests
        // TODO before we send them to the algorithm.
        ArrayList<Boolean> lockedPanels = m_ControlPanelSnapShot.lockedPanels;
        for(int i = 0; i < lockedPanels.size(); i++)
        {
            if(lockedPanels.get(i)) m_Statuses.get(i).setRequests(new HashSet<>());
        }
        if(alarm.isOn())
        {
            for(CabinStatus cs : m_Statuses) cs.setRequests(new HashSet<>());
        }

        // Now send the data to the Elevator Algorithm.
        // The algorithm will schedule the cabins and return the current
        // destination floor of each cabin.
        m_NextFloors = ea.schedule(m_Statuses,callButtons,alarm.isOn());


        // Now update the cabins destinations.
        for(CabinStatus cs : m_Statuses) {
            if (cs.getDestination().get() > 0) {
                m_RenderEntityManager.destinationFloorRenderer.setFloorNumber(cs.getDestination());
                if (cs.getDestination().get() != previousDest) {
                    previousDest = cs.getDestination().get();
                }
            }
        }
    }

    private void m_UpdateViewCheck(ViewTypes updatedView)
    {
        if(updatedView == m_PreviousView) return;
        if(updatedView != ViewTypes.OVERVIEW) m_RenderEntityManager.elevatorNumberSignRenderer.setCabinNumber(new CabinNumber(updatedView.toInt()));
        if(updatedView == ViewTypes.OVERVIEW && m_PreviousView != ViewTypes.OVERVIEW) m_SwitchToOverView();
        if(updatedView != ViewTypes.OVERVIEW && m_PreviousView == ViewTypes.OVERVIEW) m_SwitchToCabinView();
        m_PreviousView = updatedView;
    }

    private void m_SwitchToCabinView()
    {
        m_RenderEntityManager.switchToCabinView();
        m_ControlPanel.switchToCabinView();
    }
    private void m_SwitchToOverView()
    {
        m_RenderEntityManager.switchToSystemOverview();
        m_ControlPanel.switchToOverView();
    }


}
