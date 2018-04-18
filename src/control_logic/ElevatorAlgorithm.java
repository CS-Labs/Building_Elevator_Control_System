package control_logic;

import named_types.CabinNumber;
import named_types.DirectionType;
import named_types.FloorNumber;
import java.util.Collections;

import java.util.ArrayList;
import java.util.HashSet;

public class ElevatorAlgorithm
{
    private class Queues{
        ArrayList<Integer> up;
        ArrayList<Integer> down;
        int direction = 0;

        Queues(){
            up = new ArrayList<>();
            down = new ArrayList<>();
        }
        public boolean isIn(int floor){
            return up.contains(floor) || down.contains(floor);
        }
        public void addSorted(int floor, int direction){
            if(direction == 1){
                up.add(floor);
                Collections.sort(up);
            }
            if(direction == -1){
                down.add(floor);
                Collections.sort(down);
                Collections.reverse(down);
            }
        }
        public Integer get(){
            int floor = -1;
            // so the logic works out
//            if(direction == DirectionType.NONE) direction = DirectionType.UP;
            System.out.println(up.size() + " " + down.size());
            if(direction == 1|| direction == 0){
                if(up.size() == 0) direction = -1;
                else {
                    direction = 1;
                    floor = up.get(0);
                }
            }
            if(direction == -1 || direction == 0){
                if(down.size() == 0) direction = 0;
                else {
                    direction = -1;
                    floor = down.get(0);
                }
            }
            return floor;
        }
        public void pop(){
            if(direction == 1){
                if(!up.isEmpty()) this.up.remove(0);
            }
            if(direction == -1){
                if(!down.isEmpty()) this.down.remove(0);
            }
        }
    }

    private ArrayList<Queues> cabinqueues;
    //TODO implement me.
    ElevatorAlgorithm(ArrayList<Cabin> cabins){
        cabinqueues = new ArrayList<>();

        for(Cabin c : cabins) {
            cabinqueues.add(c.getStat().getCabinNumber().get()-1,new Queues());
        }
    }

    public void pop(CabinStatus cs){
        Queues q = cabinqueues.get(cs.getCabinNumber().get());
        if(q.get() == cs.getLastFloor().get()){
            q.pop();
        }
    }

    private int suitability(CabinStatus status, CallButtons button){
        DirectionType personDir = button.getType();
        DirectionType requestDir;

        if(status.getLastFloor().get() > button.getFloor().get()) requestDir = DirectionType.UP;
        else requestDir = DirectionType.DOWN;

        int d = Math.abs(status.getLastFloor().get() - button.getFloor().get());
        int N = 9;

        if((personDir == status.getDirection() &&  requestDir == status.getDirection()) || status.getDirection() == DirectionType.NONE) return N+2-d;
        else if((personDir != status.getDirection() &&  requestDir == status.getDirection())) return N+1-d;
        else return 1;
    }

    public ArrayList<FloorNumber> schedule(ArrayList<CabinStatus> cabinStatuses, ArrayList<CallButtons> callButtons, boolean wasFire) {
        ArrayList<FloorNumber> nextFloors = new ArrayList<>();

        for(CallButtons b : callButtons){
            int best_cabin = -1;
            int best_score = -1;

            // check if already scheduled or already on this floor
            for(Queues q : cabinqueues){
                if(q.isIn(b.getFloor().get())) break;
                for(CabinStatus cs : cabinStatuses){
                    if(cs.getLastFloor().get() == b.getFloor().get()) break;
                }
            }

            for(CabinStatus cs : cabinStatuses){
                int s = suitability(cs,b);
                if(s > best_score){
                    best_score = s;
                    best_cabin = cs.getCabinNumber().get();
                }
            }

            if(best_cabin >= 0) {
                if (cabinStatuses.get(best_cabin - 1).getLastFloor().get() < b.getFloor().get()) {
                    cabinqueues.get(best_cabin).addSorted(b.getFloor().get(), 1);
                } else if (cabinStatuses.get(best_cabin - 1).getLastFloor().get() > b.getFloor().get()) {
                    cabinqueues.get(best_cabin).addSorted(b.getFloor().get(), -1);
                }
            }
        }

        for(CabinStatus cs : cabinStatuses){
            int cn = cs.getCabinNumber().get()-1;
            CabinRequests cr = cs.getAllActiveRequests();
            HashSet<FloorNumber> requests = cr.getRequests();

            // check if already scheduled
            Queues q = cabinqueues.get(cn);

            for(FloorNumber fn : requests) {
                if (q.isIn(fn.get())) break;
                if (cs.getLastFloor().get() == fn.get()) break;

                if (cs.getLastFloor().get() < fn.get()) {
                    cabinqueues.get(cn).addSorted(fn.get(), 1);
                } else if (cs.getLastFloor().get() > fn.get()) {
                    cabinqueues.get(cn).addSorted(fn.get(), -1);
                }
            }
        }

        for(int i = 0; i < 4; i += 1){
            Queues q = cabinqueues.get(i);
            CabinStatus cs = cabinStatuses.get(i);
            int floor = q.get();

            if(floor == -1) floor = cs.getLastFloor().get();
            nextFloors.add(i,new FloorNumber(floor));
        }
        return nextFloors;
    }
}
