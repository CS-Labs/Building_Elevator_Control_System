package control_logic;

import application.SimGlobals;

public class CallButtons{
    private Direction direction;
    private boolean isPressed;
    private int floor;

    public CallButtons(int floor, Direction direction){
        this.isPressed =  false;
        this.direction = direction;

        // if floor != this.getFloor() you gave an invalid floor
        if(floor >= SimGlobals.MINFLOOR && floor <= SimGlobals.MAXFLOOR) this.floor = floor;
        else this.floor = 1;
    }

    public Direction getType(){
        return this.direction;
    }

    public boolean isPressed() {
        return this.isPressed;
    }

    public int getFloor(){
        return this.getFloor();
    }

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
