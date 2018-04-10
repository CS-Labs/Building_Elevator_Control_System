package control_logic;

import named_types.CabinNumber;
import named_types.FloorNumber;

class Cabin
{
    private CabinStatus cabinstatus;
    private CabinRequests cabinrequests;
    private boolean lock = false;

    // TODO: Implement me.
    public Cabin(CabinNumber cb) {
        this.cabinstatus = new CabinStatus(cb);
        this.cabinrequests = new CabinRequests();
    }

    public CabinStatus getStatus() {
        if(!lock) cabinrequests.getRequests();
        cabinstatus.setRequests(cabinrequests);
        return this.cabinstatus;
    }

    public void lockPanel() {this.lock = true;}
    public void unlockPanel() {this.lock = false;}
    // missing stuff probably
    public void setDestination(FloorNumber floor){this.cabinstatus.setDestination(floor);}
}
