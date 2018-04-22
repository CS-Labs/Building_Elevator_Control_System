package application;

import java.util.ArrayList;
import java.util.HashSet;

import control_logic.CallButtons;
import javafx.util.Pair;
import named_types.FloorNumber;
import named_types.ViewTypes;

// POD class.
public class ControlPanelSnapShot {
    public HashSet<FloorNumber> manualFloorsPresses;
    public boolean isAlarmOn;
    // Index + 1 corresponds to the elevator panel numbers, boolean value true if locked, false if not locked.
    public ArrayList<Boolean> lockedPanels;
    public ViewTypes currentView;
    public boolean isKeyLocked;
    public ArrayList<Pair<CallButtons,CallButtons>> upDownEvents;
    ControlPanelSnapShot(HashSet<FloorNumber> manualFloorsPresses, boolean isAlarmOn,
                         ArrayList<Boolean> lockedPanels, ViewTypes currentView, boolean isKeyLocked,
                         ArrayList<Pair<CallButtons,CallButtons>> upDownEvent) {
        this.manualFloorsPresses = manualFloorsPresses;
        this.isAlarmOn = isAlarmOn;
        this.lockedPanels = lockedPanels;
        this.currentView = currentView;
        this.isKeyLocked = isKeyLocked;
        this.upDownEvents = upDownEvent;
    }
}
