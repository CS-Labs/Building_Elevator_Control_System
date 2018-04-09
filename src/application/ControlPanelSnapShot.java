package application;

import java.util.ArrayList;

// POD class.
class ControlPanelSnapShot {
    // TODO: Once CallButtons is added this should return an array list of call buttons.
    public ArrayList<Integer> manualFloorsPresses;
    public boolean isAlarmOn;
    // Index + 1 corresponds to the elevator panel numbers, boolean value true if locked, false if not locked.
    public ArrayList<Boolean> lockedPanels;
    public ViewTypes currentView;
    public boolean isKeyLocked;
    ControlPanelSnapShot(ArrayList<Integer> manualFloorsPresses, boolean isAlarmOn,
                         ArrayList<Boolean> lockedPanels, ViewTypes currentView, boolean isKeyLocked) {
        this.manualFloorsPresses = manualFloorsPresses;
        this.isAlarmOn = isAlarmOn;
        this.lockedPanels = lockedPanels;
        this.currentView = currentView;
        this.isKeyLocked = isKeyLocked;
    }

}
