package control_logic;

import application.ControlPanel;
import application.ControlPanelSnapShot;
import application.SystemOverviewPanel;
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
    private ViewTypes m_PreviousView = ViewTypes.OVERVIEW;
    private ArrayList<Cabin> cabins = new ArrayList<>();
    private FloorRequests floorrequests = new FloorRequests();
    private DoorControl _doorControl;
    private SceneManager _alwaysActive;
    private RenderEntityManager m_RenderEntityManager;
    private BuildingFireAlarm alarm;
    private ArrayList<CabinStatus> m_Statuses = new ArrayList<>();
    private ArrayList<FloorNumber> m_NextFloors = new ArrayList<>();
    private ArrayList<Boolean> cycleChecks = new ArrayList<>(Arrays.asList(false, false, false, false));
    private ArrayList<Double> timers = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0));


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
        for(int i = 0; i < cabins.size(); i++){
            CabinStatus status = cabins.get(i).getStatus();
            m_Statuses.add(status);
            // If the cabin has arrived at it's destination notify of arrival.
            if(status.getLastFloor().equals(status.getDestination()) && status.getMotionStatus() == MotionStatusTypes.STOPPED){
                FloorNumber lastFloor = status.getLastFloor();
                CabinNumber cabinNumber = status.getCabinNumber();
                DirectionType direction = status.getDirection();
                floorrequests.notifyOfArrival(lastFloor, cabinNumber, direction); // Signal arrival.
                m_RenderEntityManager.buttonPanelRenderer.turnOffFloorButton(lastFloor); // Turn off button light.
                ArrivalLightStates light;
                if(direction == DirectionType.UP) light = ArrivalLightStates.ARRIVAL_GOING_UP;
                else if(direction == DirectionType.DOWN) light = ArrivalLightStates.ARRIVAL_GOING_DOWN;
                else light = ArrivalLightStates.NO_ARRIVAL;
                m_RenderEntityManager.arrivalLightRenderer.setArrivalLightState(light);
                if(!cycleChecks.get(i) && _doorControl.getStatus(lastFloor, cabinNumber) == DoorStatusType.CLOSED || _doorControl.getStatus(lastFloor, cabinNumber) == DoorStatusType.OPENING)
                {
                    _doorControl.open(lastFloor, cabinNumber);
                    Pair<Double,Double> openPercentages = _doorControl.getInnerOuterDoorPercentageOpen(lastFloor, cabinNumber);
                    System.out.println(openPercentages.getKey() + " " + openPercentages.getValue());
                    m_RenderEntityManager.updateDoorLocs(openPercentages.getKey(), openPercentages.getValue());
                }
                if(_doorControl.getStatus(lastFloor, cabinNumber) == DoorStatusType.OPENED) timers.set(i, timers.get(i) + 0.15);
                if(timers.get(i) > 100 && _doorControl.getStatus(lastFloor, cabinNumber) == DoorStatusType.OPENED || _doorControl.getStatus(lastFloor, cabinNumber) == DoorStatusType.CLOSING)
                {
                    _doorControl.close(lastFloor, cabinNumber);
                    Pair<Double,Double> closedPercentages = _doorControl.getInnerOuterDoorPercentageOpen(lastFloor, cabinNumber);
                    System.out.println(closedPercentages.getKey() + " " + closedPercentages.getValue());
                    m_RenderEntityManager.updateDoorLocs(closedPercentages.getKey(), closedPercentages.getValue());
                    if(currentView != ViewTypes.OVERVIEW && _doorControl.interferenceDetected())
                    {
                        timers.set(i,0.0);
                        _doorControl.open(lastFloor, cabinNumber);
                    }
                    if(closedPercentages.getKey() < 0.1) cycleChecks.set(i,true);
                }
                if(cycleChecks.get(i) && _doorControl.getStatus(lastFloor, cabinNumber) == DoorStatusType.CLOSED)
                {
                    timers.set(i,0.0);
                    m_RenderEntityManager.arrivalLightRenderer.setArrivalLightState(ArrivalLightStates.NO_ARRIVAL);
                    cabins.get(i).removeRequest(lastFloor); // VERY IMPORTANT.
                    //TODO REMOVE REQUEST FROM DATA STRUCTURE IN ALGORITHM (VERY IMPORTANT)
                }


            }
        }


        // If the user is viewing the inside of one of the cabins then render the cabin.
        if(currentView != ViewTypes.OVERVIEW)
        {
            CabinStatus visibleCabin = m_Statuses.get(currentView.toInt()-1);
            m_RenderEntityManager.floorSignRenderer.updateFloorNumber(visibleCabin.getLastFloor());
            if(visibleCabin.getDestination().get() > 0) m_RenderEntityManager.destinationFloorRenderer.setFloorNumber(visibleCabin.getDestination());
            if(m_ControlPanelSnapShot.currentView != m_PreviousView) { // Prevent meaningless re-rendering.
                for (int i = 1; i <= 10; i++) m_RenderEntityManager.buttonPanelRenderer.turnOffFloorButton(new FloorNumber(i));
            }
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
        if(m_ControlPanelSnapShot.currentView != ViewTypes.OVERVIEW) {
            CabinStatus inViewCabin = m_Statuses.get(m_ControlPanelSnapShot.currentView.toInt() - 1);
            HashSet<FloorNumber> requests = inViewCabin.getAllActiveRequests();
            requests.addAll(m_ControlPanelSnapShot.manualFloorsPresses);
            inViewCabin.setRequests(requests);
        }
        ArrayList<Pair<CallButtons,CallButtons>> callButtons = floorrequests.getFloorRequests();
        ArrayList<Pair<CallButtons,CallButtons>> managerCallButtons = floorrequests.getFloorRequests();
        Iterator<Pair<CallButtons,CallButtons>> it1 = callButtons.iterator();
        Iterator<Pair<CallButtons,CallButtons>> it2 = managerCallButtons.iterator();
        while(it1.hasNext() && it2.hasNext()) {
            Pair<CallButtons, CallButtons> buttons = it1.next();
            Pair<CallButtons,CallButtons> managerButtons = it2.next();
            buttons.getKey().setButtonPressedState(buttons.getKey().isPressed() || managerButtons.getKey().isPressed());
            buttons.getValue().setButtonPressedState(buttons.getValue().isPressed() || managerButtons.getValue().isPressed());
        }
        // Wipe requests for locked cabins or all cabins if a fire has occurred before sending it to the algorithm.
        ArrayList<Boolean> lockedPanels = m_ControlPanelSnapShot.lockedPanels;
        for(int i = 0; i < lockedPanels.size(); i++)
        {
            if(lockedPanels.get(i))
            {
                m_Statuses.get(i).setRequests(new HashSet<>());
                cabins.get(i).clearRequests();
            }
        }
        if(alarm.isOn())
        {
            for(CabinStatus cs : m_Statuses) cs.setRequests(new HashSet<>());
            for(Cabin cabin : cabins) cabin.clearRequests();
            callButtons.clear();
            floorrequests.clearFloorRequests();
        }
        // Now send the data to the Elevator Algorithm.
        // The algorithm will schedule the cabins and return the current
        // destination floor of each cabin.
      //  m_NextFloors = ea.schedule(m_Statuses,null,alarm.isOn());

        // Now update each of the cabins destination floors
     //   for(int i = 0; i < cabins.size(); i++) cabins.get(i).setDestination(m_NextFloors.get(i));




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
