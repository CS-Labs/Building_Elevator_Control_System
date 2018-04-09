package control_logic;

import named_types.CabinNumber;
import named_types.FloorNumber;

public class ArrivalSignals{
    private boolean active;
    private FloorNumber floor;
    private CabinNumber cabinNumber;

    public ArrivalSignals(FloorNumber floor, CabinNumber cabinNumber){
        this.active = false;
        this.floor = floor;
        this.cabinNumber = cabinNumber;
    }

    public boolean on(){
        return this.active;
    }

    public void setState(boolean state) {
        this.active = state;
    }

    public FloorNumber getFloor(){
        return this.floor;
    }

    public CabinNumber getCabinNumber(){
        return this.cabinNumber;
    }

    public ArrivalSignals getCopy(){
        ArrivalSignals copy = new ArrivalSignals(this.floor,this.cabinNumber);
        copy.setState(this.active);

        return copy;
    }
}