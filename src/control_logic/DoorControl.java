package control_logic;

import engine.LogicEntity;
import javafx.util.Pair;
import named_types.CabinNumber;
import named_types.DoorStatusType;
import named_types.FloorNumber;

class DoorControl implements LogicEntity {
    // TODO Implement me
    DoorControl() {}
    @Override
    public void process(double deltaSeconds) {}
    public void open(FloorNumber floorNumber, CabinNumber cabinNumber) {}
    public void close(FloorNumber floorNumber, CabinNumber cabinNumber) {}
    public DoorStatusType getStatus(FloorNumber floorNumber, Cabin cabinNumber) {return null;}
    public Pair<Double, Double> getLocationOuterDoors(FloorNumber floorNumber, Cabin cabinNumber) {return null;}
    public Pair<Double, Double> getLocationInnerDoors(FloorNumber floorNumber, Cabin cabinNumber) {return null;}


}
