package control_logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import application.ControlPanel;
import application.ControlPanelSnapShot;
import engine.Callback;
import engine.Engine;
import engine.LogicEntity;
import engine.Message;
import engine.SceneManager;
import engine.Singleton;
import javafx.util.Pair;
import named_types.ArrivalLightStates;
import named_types.CabinNumber;
import named_types.DirectionType;
import named_types.DoorStatusType;
import named_types.FloorNumber;
import named_types.ViewTypes;

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
    private ArrayList<Boolean> managerMode = new ArrayList<>(Arrays.asList(false, false, false, false));
    private ArrayList<Boolean> initManager = new ArrayList<>(Arrays.asList(false, false, false, false));
    private ArrayList<Boolean> safeToDepart = new ArrayList<>(Arrays.asList(true, true, true, true));
    private ArrayList<Boolean> doorsOpening = new ArrayList<>(Arrays.asList(false, false, false, false));
    private ConcurrentLinkedQueue<Callback> prepareElevatorForDepartureQueue = new ConcurrentLinkedQueue<>();

    private ArrayList<Double> timers = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0));
    private int numCabins = 4;

    public BuildingControl(ControlPanel controlPanel)
    {
        // add cabins
        for(int i = 0; i < numCabins; i+=1){
            cabins.add(i,new Cabin(new CabinNumber(i+1)));
        }
        ea = new ElevatorAlgorithm(cabins);
        m_ControlPanel = controlPanel;
        _doorControl = new DoorControl(this, 10, numCabins);
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
        boolean keyLocked = m_ControlPanelSnapShot.isKeyLocked;
        if(keyLocked && !(currentView == ViewTypes.OVERVIEW))
        {
          managerMode.set(currentView.toInt() - 1, true);
          initManager.set(currentView.toInt() - 1, true);
        }
        else if(!keyLocked && !(currentView == ViewTypes.OVERVIEW))
        {
          managerMode.set(currentView.toInt() - 1, false);
        }
        // Get the latest cabin snap shots.
        for(int i = 0; i < cabins.size(); i++){
            CabinStatus status = cabins.get(i).getStatus();
            if(initManager.get(i))
            {
              cabins.get(i).clearRequests();
              status.getAllActiveRequests().clear();
              ea.clearRequests(status);
              for (int j = 1; j <= 10; j++) m_RenderEntityManager.buttonPanelRenderer.turnOffFloorButton(new FloorNumber(j));
              initManager.set(i, false);
            }
            if(managerMode.get(i))
            {
              status.setManagerMode(true);
            }
            else if(!managerMode.get(i))
            {
              status.setManagerMode(false);
            }
            m_Statuses.add(status);
            FloorNumber lastFloor = null;
            if(!managerMode.get(i))
            {
              lastFloor = status.getLastFloor();
            }
            else if(managerMode.get(i))
            {
              lastFloor = status.getLastFloorManager();
            }
            CabinNumber cabinNumber = status.getCabinNumber();
            DirectionType direction = status.getDirection();
            ArrivalLightStates light;
            if(direction == DirectionType.UP) light = ArrivalLightStates.ARRIVAL_GOING_UP;
            else if(direction == DirectionType.DOWN) light = ArrivalLightStates.ARRIVAL_GOING_DOWN;
            else light = ArrivalLightStates.NO_ARRIVAL;
            // Check if the current elevator is the one on the screen that needs its visuals updated
            if (currentView == ViewTypes.values()[i]) {
                m_RenderEntityManager.arrivalLightRenderer.setArrivalLightState(light);
                Pair<Double, Double> closedPercentages = _doorControl.getInnerOuterDoorPercentageOpen(lastFloor, cabinNumber);
                //System.out.println(closedPercentages.getKey() + " " + closedPercentages.getValue());
                m_RenderEntityManager.updateDoorLocs(closedPercentages.getKey(), closedPercentages.getValue());
            }
            // Check for interference
            if (currentView != ViewTypes.OVERVIEW && _doorControl.interferenceDetected()) {
                _doorControl.open(lastFloor, cabinNumber);
            }
            // If the cabin has arrived at it's destination notify of arrival.
            if(((!status.inManagerMode() && status.getLastFloor().equals(status.getDestination()) && status.getMotionStatus() == MotionStatusTypes.STOPPED)||
                (status.inManagerMode() && status.getLastFloorManager().equals(status.getDestination()) && status.getMotionStatus() == MotionStatusTypes.STOPPED))
                    && !doorsOpening.get(i)){
                floorrequests.notifyOfArrival(lastFloor, cabinNumber, direction); // Signal arrival
                m_RenderEntityManager.buttonPanelRenderer.turnOffFloorButton(lastFloor); // Turn off button light

                doorsOpening.set(i, true);
                // Make sure we signal that it is not safe to depart since the doors will be opening
                safeToDepart.set(i, false);
                // Create brand new objects for these so we get rid of the possibility
                // of the floor/cabin number changing due to someone having a reference to it somehow
                FloorNumber lastFloorInternal = new FloorNumber(lastFloor.get());
                CabinNumber cabinNumberInternal = new CabinNumber(cabinNumber.get());
                // When the door is finished closing, the following function will be called
                // to prepare the elevator for final departure
                Callback prepareElevatorForDeparture = () ->
                {
                    cabins.get(cabinNumberInternal.get() - 1).removeRequest(lastFloorInternal); // VERY IMPORTANT
                    ea.pop(status);
                    m_RenderEntityManager.arrivalLightRenderer.setArrivalLightState(ArrivalLightStates.NO_ARRIVAL);
                };
                // Dispatch the door open/close logic
                new LogicEntity() {
                    volatile double timeToKeepDoorsOpenSec = 10;
                    volatile double elapsedSec = 0.0;
                    volatile boolean keepUpdating = true;

                    {
                        Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_LOGIC_ENTITY, this));
                        _doorControl.open(lastFloorInternal, cabinNumberInternal);
                    }

                    @Override
                    public void process(double deltaSeconds) {
                        if (!this.keepUpdating) return;
                        DoorStatusType status = _doorControl.getStatus(lastFloorInternal, cabinNumberInternal);
                        switch (status) {
                            case OPENING:
                                // Force this to be 0 while opening
                                this.elapsedSec = 0.0;
                                break;
                            case OPENED:
                                this.elapsedSec += deltaSeconds;
                                // If the time is up, shut the doors
                                if (this.elapsedSec >= this.timeToKeepDoorsOpenSec)
                                    _doorControl.close(lastFloorInternal, cabinNumberInternal);
                                break;
                            case CLOSED:
                                // Notify building control that it is safe to open
                                safeToDepart.set(cabinNumberInternal.get() - 1, true);
                                // Stop updating
                                Engine.getMessagePump().sendMessage(new Message(Singleton.REMOVE_LOGIC_ENTITY, this));
                                // Internal check just for extra safety
                                this.keepUpdating = false;
                                // Make sure to add this to the queue so that building control
                                // knows that the cabin managed by this thread needs to be prepped for departure
                                prepareElevatorForDepartureQueue.add(prepareElevatorForDeparture);
                                break;
                            default:
                                break;
                        }
                    }
                };

                /** OLD STUFF -> uncomment and it should work as before
                if(!cycleChecks.get(i) && _doorControl.getStatus(lastFloor, cabinNumber) == DoorStatusType.CLOSED || _doorControl.getStatus(lastFloor, cabinNumber) == DoorStatusType.OPENING )//&& m_NextFloors.get(i).get() != -1)
                {
                    System.out.println("elevator " + status.getCabinNumber().get() + " doors open");
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
                if(_doorControl.getStatus(lastFloor, cabinNumber) == DoorStatusType.CLOSED && m_NextFloors.get(i).get() != -1)
                {
                    timers.set(i,0.0);
                    m_RenderEntityManager.arrivalLightRenderer.setArrivalLightState(ArrivalLightStates.NO_ARRIVAL);
                    cabins.get(i).removeRequest(lastFloor); // VERY IMPORTANT.
                    ea.pop(status);
                }
                 */
            }
        }

        // If the user is viewing the inside of one of the cabins then render the cabin.
        if(currentView != ViewTypes.OVERVIEW)
        {
            CabinStatus visibleCabin = m_Statuses.get(currentView.toInt()-1);
            if(!visibleCabin.inManagerMode())m_RenderEntityManager.floorSignRenderer.updateFloorNumber(visibleCabin.getLastFloor());
            else if(visibleCabin.inManagerMode())m_RenderEntityManager.floorSignRenderer.updateFloorNumber(visibleCabin.getLastFloorManager());
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
            boolean inManagerMode = inViewCabin.inManagerMode();
            //Filter manual requests.
            if(!inManagerMode)
            {
              inViewCabin.setRequests(requests);
            }
            else if(inManagerMode)
            {
              inViewCabin.setRequests(filteredRequests(m_ControlPanelSnapShot.manualFloorsPresses, inViewCabin.getLastFloorManager().get()));
            }
        }
        ArrayList<Pair<CallButtons,CallButtons>> callButtons = floorrequests.getFloorRequests();
        // TODO: should this be something else?
        ArrayList<Pair<CallButtons,CallButtons>> managerCallButtons = m_ControlPanelSnapShot.upDownEvents;
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

        // If any elevators have been added to the queue to be prepped for departure, handle this now
        while (prepareElevatorForDepartureQueue.size() > 0) {
            prepareElevatorForDepartureQueue.poll().handleCallback();
        }

        if(!alarm.isOn()) m_NextFloors = ea.schedule(m_Statuses,callButtons,alarm.isOn(), managerMode);
        else m_NextFloors = ea.schedule(m_Statuses,managerCallButtons,alarm.isOn(), managerMode);

        // Now update each of the cabins destination floors
        for(int i = 0; i < cabins.size(); i++) {
            //if(_doorControl.getStatus(m_Statuses.get(i).getLastFloor(),m_Statuses.get(i).getCabinNumber()) == DoorStatusType.CLOSED ){//&& m_NextFloors.get(i).get() != -1) {
            if (safeToDepart.get(i)) {
                if(m_NextFloors.get(i).get() != -1) {
                    // Prepare the elevator for final departure
                    FloorNumber nextFloor = m_NextFloors.get(i);
                    // Only notify the cabin to depart if the new next floor is different from
                    // the last destination
                    if (nextFloor.get() == cabins.get(i).getStatus().getDestination().get()) continue;
                    cabins.get(i).setDestination(nextFloor);
                    doorsOpening.set(i, false);
                    //cycleChecks.set(i, false);
                }
                // alarm is on and we reached floor 1, go to manager mode
                if(alarm.isOn() && m_Statuses.get(i).getLastFloor().get() == 1){
                    managerMode.set(i, true);
                }
            }
        }
    }
    
    //Filter requests in manager mode. Can only move one floor at a time.
    private HashSet<FloorNumber> filteredRequests(HashSet<FloorNumber> requests, int currFloor)
    {
      HashSet<FloorNumber> filteredReqs = new HashSet<>();
      requests.forEach((request) -> {
        if((request.get() == currFloor - 1) || (request.get() == currFloor + 1))
        {
          filteredReqs.add(request);
        }
      });
      return filteredReqs;
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
