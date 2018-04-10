package control_logic;

import named_types.CabinNumber;

import java.util.HashSet;
import java.util.Random;

class CabinRequests
{
    private HashSet<CabinNumber> requests = new HashSet<>();
    // need to change these
    private static final double probability = .01;

    CabinRequests() {
    }

    public HashSet<CabinNumber> getRequests() {
        Random r = new Random();
        for(int i = ControlLogicGlobals.MINFLOOR.get(); i <= ControlLogicGlobals.MAXFLOOR.get(); i+=1) {
            if (r.nextDouble() < probability) requests.add(new CabinNumber(i));
        }
        return requests;
    }

    public void removeRequest(CabinNumber cabin){
        if(requests.contains(cabin)) requests.remove(cabin);
    }
    public void clearRequests(){
        requests.clear();
    }
}
