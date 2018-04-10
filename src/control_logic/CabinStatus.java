package control_logic;

import named_types.CabinNumber;
import named_types.DirectionType;
import named_types.FloorNumber;

import java.util.HashSet;

class CabinStatus
{
    private DirectionType direction;
    private MotionStatusTypes moving;
    private CabinNumber cabinNumber;
    private FloorNumber destination;
    private FloorNumber lastFloor;
    // this is only used to store from cabin and send to building control
    private CabinRequests cabinrequests;

    CabinStatus(CabinNumber cn) {
        this.direction = DirectionType.NONE;
        // if zero, no destination
        this.destination = new FloorNumber(0);
        this.lastFloor = new FloorNumber(1);
        this.cabinNumber = cn;
        this.moving = MotionStatusTypes.STOPPED;
//        this.cabinRequests = new CabinRequests();
    }

    public DirectionType getDirection() {return this.direction;}
    public MotionStatusTypes getMotionStatus() {return this.moving;}
    public CabinNumber getCabinNumber() {return this.cabinNumber;}
    public FloorNumber getDestination() {return this.destination;}
    public FloorNumber getLastFloor() {return this.lastFloor;}

    public CabinStatus getStatus() {
        CabinStatus copy = new CabinStatus(this.getCabinNumber());

        copy.setDestination(this.getDestination());
        copy.setRequests(this.cabinrequests);
        copy.setDirection(this.getDirection());
        copy.setLastFloor(this.getLastFloor());
        copy.setMotionStatus(this.getMotionStatus());
        return copy;
    }
//    public HashSet<CabinNumber> getAllActiveRequests() { return cabinRequests.getRequests(); }

    public void setDirection(DirectionType direction){this.direction = direction;}
    public void setMotionStatus(MotionStatusTypes moving) {this.moving = moving;}
    public void setDestination(FloorNumber fn) {this.destination = fn;}
    public void setLastFloor(FloorNumber fn) {this.lastFloor = fn;}
    public void setRequests(CabinRequests cr){this.cabinrequests = cr;}

}
