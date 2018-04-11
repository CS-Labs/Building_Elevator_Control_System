package application;

import javafx.util.Pair;
import named_types.DirectionType;
import named_types.FloorNumber;
import named_types.ViewTypes;

import java.util.ArrayList;

// POD class.
public class ControlPanelSnapShot {
    public ArrayList<Integer> manualFloorsPresses;
    public boolean isAlarmOn;
    // Index + 1 corresponds to the elevator panel numbers, boolean value true if locked, false if not locked.
    public ArrayList<Boolean> lockedPanels;
    public ViewTypes currentView;
    public boolean isKeyLocked;
    public Pair<DirectionType, FloorNumber> upDownEvent;
    ControlPanelSnapShot(ArrayList<Integer> manualFloorsPresses, boolean isAlarmOn,
                         ArrayList<Boolean> lockedPanels, ViewTypes currentView, boolean isKeyLocked, Pair<DirectionType, FloorNumber> event) {
        this.manualFloorsPresses = manualFloorsPresses;
        this.isAlarmOn = isAlarmOn;
        this.lockedPanels = lockedPanels;
        this.currentView = currentView;
        this.isKeyLocked = isKeyLocked;
        this.upDownEvent = event;
    }

}
