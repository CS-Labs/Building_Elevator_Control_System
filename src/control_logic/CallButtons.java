package control_logic;


import named_types.DirectionType;
import named_types.FloorNumber;

public class CallButtons{
    private DirectionType direction;
    private boolean isPressed;
    private FloorNumber floor;

    public CallButtons(FloorNumber floor, DirectionType direction){
        this.isPressed =  false;
        this.direction = direction;

        // if floor != this.getFloor() you gave an invalid floor
        if(floor.get() >= ControlLogicGlobals.MINFLOOR.get() && floor.get() <= ControlLogicGlobals.MAXFLOOR.get()) this.floor = floor;
        else this.floor = new FloorNumber(1);
    }

    public DirectionType getType(){
        return this.direction;
    }

    public boolean isPressed() {
        return this.isPressed;
    }

    public FloorNumber getFloor(){ return this.floor; }

    // when this is used, if ispressed == false, either an elevator has arrived to this floor
    // or there is a firealarm. otherwise, don't change state.
    public void setButtonPressedState(boolean isPressed){
        this.isPressed = isPressed;
    }

    public CallButtons makeCopy(){
        CallButtons copy = new CallButtons(this.getFloor(),this.getType());
        copy.isPressed = this.isPressed();
        return copy;
    }
}
