package control_logic;

import application.ControlPanelGlobals;
import javafx.util.Pair;
import named_types.CabinNumber;
import named_types.DirectionType;
import named_types.FloorNumber;

import java.util.ArrayList;
import java.util.Random;

public class FloorRequests{
    private ArrayList<Pair<CallButtons, CallButtons>> buttons = new ArrayList<>();
    private ArrayList<ArrivalSignals> signals = new ArrayList<>();
    // this really depends on how often getfloorrequests is called and what we want the probability to be
    private static final double probability = .00025;
//    private static final double probability = .0000;


    public FloorRequests(){
        buttons.add(new Pair<>(new CallButtons(ControlLogicGlobals.MINFLOOR, DirectionType.UP), new CallButtons(ControlLogicGlobals.MINFLOOR, DirectionType.DOWN)));
        // for all floors
        for(int f = ControlLogicGlobals.MINFLOOR.get() + 1; f <= (ControlLogicGlobals.MAXFLOOR.get() - 1); f += 1){
            buttons.add(new Pair<>(new CallButtons(new FloorNumber(f), DirectionType.UP),new CallButtons(new FloorNumber(f), DirectionType.DOWN)));
        }
        buttons.add(new Pair<>(new CallButtons(ControlLogicGlobals.MAXFLOOR, DirectionType.UP), new CallButtons(ControlLogicGlobals.MAXFLOOR, DirectionType.DOWN)));

        // for all floors
        for(int f = ControlLogicGlobals.MINFLOOR.get(); f < ControlLogicGlobals.MAXFLOOR.get(); f += 1){
            // for all cabins
            for(int c = 1; c < 4; c += 1) {
                signals.add(new ArrivalSignals(new FloorNumber(f),new CabinNumber(c)));
            }
        }
    }



    public void notifyOfArrival(FloorNumber floor, CabinNumber cabin, DirectionType direction){
        for(Pair<CallButtons, CallButtons> upDownButtons : buttons){
            CallButtons upButton = upDownButtons.getKey();
            CallButtons downButton = upDownButtons.getValue();
            if(upButton.getType() == direction && upButton.getFloor().equals(floor))
            {
                upButton.setButtonPressedState(false);
                break;
            }
            if(downButton.getType() == direction && downButton.getFloor().equals(floor))
            {
                downButton.setButtonPressedState(false);
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


    public ArrayList<Pair<CallButtons,CallButtons>> getFloorRequests(){
        ArrayList<Pair<CallButtons,CallButtons>> updatedRequests = new ArrayList<>();

        Random r = new Random();
        for(Pair<CallButtons, CallButtons> upDownButtons : buttons){
            CallButtons upButton = upDownButtons.getKey();
            CallButtons downButton = upDownButtons.getValue();
            if(upButton.getFloor().get() != 10 && r.nextDouble() < probability) upButton.setButtonPressedState(true);
            if(downButton.getFloor().get() != 1 && r.nextDouble() < probability) downButton.setButtonPressedState(true);
            updatedRequests.add(new Pair<>(upButton.makeCopy(), downButton.makeCopy()));
        }
        return updatedRequests;
    }

    public void clearFloorRequests(){
        for(Pair<CallButtons, CallButtons> upDownButtons : buttons) {
            upDownButtons.getKey().setButtonPressedState(false);
            upDownButtons.getValue().setButtonPressedState(false);
        }
        for(ArrivalSignals arrivalSignal : signals) arrivalSignal.setState(false);
    }

    public ArrayList<ArrivalSignals> getArrivalSignals(){
        return this.signals;
    }
}