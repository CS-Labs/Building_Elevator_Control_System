package control_logic;

import java.util.ArrayList;
import java.util.Random;

public class FloorRequests{
    private ArrayList<CallButtons> buttons = new ArrayList<>();
    private ArrayList<ArrivalSignals> signals = new ArrayList<>();
    // this really depends on how often getfloorrequests is called and what we want the probability to be
    private static final int REQUESTPROB = 25;

    public FloorRequests(){
        buttons.add(new CallButtons(ControlLogicGlobals.MINFLOOR, Direction.UP));
        buttons.add(new CallButtons(ControlLogicGlobals.MINFLOOR, Direction.DOWN));

        // for all floors
        for(int f = ControlLogicGlobals.MINFLOOR + 1; f <= (ControlLogicGlobals.MAXFLOOR - 1); f += 1){
            buttons.add(new CallButtons(f, Direction.UP));
            buttons.add(new CallButtons(f, Direction.DOWN));
        }

        // for all floors
        for(int f = ControlLogicGlobals.MINFLOOR; f < ControlLogicGlobals.MAXFLOOR; f += 1){
            // for all cabins
            for(int c = 1; c < 4; c += 1) {
                signals.add(new ArrivalSignals(f,c));
            }
        }
    }

    public void notifyOfArrival(int floor, int cabin, Direction direction){
        for(CallButtons button : buttons){
            if(button.getType() == direction && button.getFloor() == floor){
                button.setButtonPressedState(false);
                break;
            }
        }
        for(ArrivalSignals signal : signals){
            if(signal.getFloor() == floor &&  signal.getCabinNumber() == cabin){
                signal.setState(true);
                break;
            }
        }
    }

    public void notifyOfDeparture(int floor, int cabin){
        for(ArrivalSignals signal : signals){
            if(signal.getFloor() == floor &&  signal.getCabinNumber() == cabin){
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