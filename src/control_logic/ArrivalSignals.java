package control_logic;

public class ArrivalSignals{
    private boolean active;
    private int floor;
    private int cabinNumber;

    public ArrivalSignals(int floor, int cabinNumber){
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

    public int getFloor(){
        return this.floor;
    }

    public int getCabinNumber(){
        return this.cabinNumber;
    }

    public ArrivalSignals getCopy(){
        ArrivalSignals copy = new ArrivalSignals(this.floor,this.cabinNumber);
        copy.setState(this.active);

        return copy;
    }
}