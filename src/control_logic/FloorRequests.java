package control_logic;

import named_types.CabinNumber;
import named_types.DirectionType;
import named_types.FloorNumber;

import java.util.ArrayList;
import java.util.Random;

public class FloorRequests{
    private ArrayList<CallButtons> buttons = new ArrayList<>();
    private ArrayList<ArrivalSignals> signals = new ArrayList<>();
    // this really depends on how often getfloorrequests is called and what we want the probability to be
    private static final int REQUESTPROB = 25;

    public FloorRequests(){
        buttons.add(new CallButtons(ControlLogicGlobals.MINFLOOR, DirectionType.UP));
        buttons.add(new CallButtons(ControlLogicGlobals.MINFLOOR, DirectionType.DOWN));

        // for all floors
        for(int f = ControlLogicGlobals.MINFLOOR.get() + 1; f <= (ControlLogicGlobals.MAXFLOOR.get() - 1); f += 1){
            buttons.add(new CallButtons(new FloorNumber(f), DirectionType.UP));
            buttons.add(new CallButtons(new FloorNumber(f), DirectionType.DOWN));
        }

        // for all floors
        for(int f = ControlLogicGlobals.MINFLOOR.get(); f < ControlLogicGlobals.MAXFLOOR.get(); f += 1){
            // for all cabins
            for(int c = 1; c < 4; c += 1) {
                signals.add(new ArrivalSignals(new FloorNumber(f),new CabinNumber(c)));
            }
        }
    }

    public void notifyOfArrival(FloorNumber floor, CabinNumber cabin, DirectionType direction){
        for(CallButtons button : buttons){
            if(button.getType() == direction && button.getFloor() == floor){
                button.setButtonPressedState(false);
                break;
            }
        }
        for(ArrivalSignals signal : signals){
            if(signal.getFloor().equals(floor) &&  signal.getCabinNumber().equals(cabin)){
                signal.setState(true);
                break;
            }
        }
    }

    public void notifyOfDeparture(FloorNumber floor, CabinNumber cabin){
        for(ArrivalSignals signal : signals){
            if(signal.getFloor().equals(floor) &&  signal.getCabinNumber().equals(cabin)){
                signal.setState(false);
                break;
            }
        }
    }

    public ArrayList<CallButtons> getButtons() {
        ArrayList<CallButtons> copies = new ArrayList<>();

        for(CallButtons button : buttons){
            copies.add(button.makeCopy());
        }
        return copies;
    }

    public ArrayList<CallButtons> getFloorRequests(){
        Random r = new Random();
        for(CallButtons button : buttons){
            if(r.nextInt(REQUESTPROB)==0 && !button.isPressed()) {
                button.setButtonPressedState(true);
            }
        }
        return this.getButtons();
    }

    public ArrayList<ArrivalSignals> getArrivalSignals(){
        return this.signals;
    }
}