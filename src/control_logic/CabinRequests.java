package control_logic;

import named_types.FloorNumber;

import java.util.HashSet;
import java.util.Random;

class CabinRequests
{
    private HashSet<FloorNumber> requests = new HashSet<>();
    // need to change these
    private static final double probability = .00025;
//    private static final double probability = .000;

    public HashSet<FloorNumber> getRequests() {
        Random r = new Random();
        for(int i = ControlLogicGlobals.MINFLOOR.get(); i <= ControlLogicGlobals.MAXFLOOR.get(); i+=1) {
            if (r.nextDouble() < probability) {
                requests.add(new FloorNumber(i));
            }
        }
        return requests;
    }

    public HashSet<FloorNumber> getCurrentRequests(){
        return this.requests;
    }

    public void removeRequest(FloorNumber f){
        if(requests.contains(f)) requests.remove(f);
    }
    public void clearRequests(){
        requests.clear();
    }
}
