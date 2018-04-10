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
    private CabinRequests cabinRequests = new CabinRequests();
    // this needs to be initialized by cabin and sent to cabinStatus I believe

    CabinStatus(CabinNumber cn) {
        this.direction = DirectionType.NONE;
        // if zero, no destination
        this.destination = new FloorNumber(0);
        this.lastFloor = new FloorNumber(1);
        this.cabinNumber = cn;
        this.moving = MotionStatusTypes.STOPPED;
    }

    public DirectionType getDirection() {return this.direction;}
    public MotionStatusTypes getMotionStatus() {return this.moving;}
    public CabinNumber getCabinNumber() {return this.cabinNumber;}
    public FloorNumber getDestination() {return this.destination;}
    public FloorNumber getLastFloor() {return this.lastFloor;}
    public HashSet<CabinNumber> getAllActiveRequests() { return cabinRequests.getRequests(); }

    public void setDirection(DirectionType direction){this.direction = direction;}
    public void getMotionStatus(MotionStatusTypes moving) {this.moving = moving;}
    public void getDestination(FloorNumber fn) {this.destination = fn;}
    public void getLastFloor(FloorNumber fn) {this.lastFloor = fn;}
}
