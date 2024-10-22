package control_logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javafx.util.Pair;
import named_types.DirectionType;
import named_types.FloorNumber;

public class ElevatorAlgorithm
{
    private class Queues{
        ArrayList<Integer> up;
        ArrayList<Integer> down;
        int direction = 1;

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
//            System.out.println(up.size() + " " + down.size());
            if(up.size() == 0 && down.size() == 0){
                direction = 1;
                return floor;
            }
            if(up.size() == 0){
                direction = -1;
                return down.get(0);
            }
            if(down.size() == 0){
                direction = 1;
                return up.get(0);
            }
            return floor;

//            if(direction == 1|| direction == 0){
//                if(up.size() == 0) direction = -1;
//                else {
//                    direction = 1;
//                    floor = up.get(0);
//                }
//            }
//            if(direction == -1 || direction == 0){
//                if(down.size() == 0) direction = 0;
//                else {
//                    direction = -1;
//                    floor = down.get(0);
//                }
//            }
//            return floor;
        }
        public void pop(){
            if(direction == 1){
                if(!up.isEmpty()) this.up.remove(0);
            }
            if(direction == -1){
                if(!down.isEmpty()) this.down.remove(0);
            }
        }
        public void clear(){
            up.clear();
            down.clear();
        }
    }

    public void clearRequests(CabinStatus status)
    {
        cabinqueues.get(status.getCabinNumber().get()-1).clear();
    }

    private ArrayList<Queues> cabinqueues;
    ElevatorAlgorithm(ArrayList<Cabin> cabins){
        cabinqueues = new ArrayList<>();

        for(Cabin c : cabins) {
            cabinqueues.add(c.getStat().getCabinNumber().get()-1,new Queues());
        }
    }

    public void pop(CabinStatus cs){
        Queues q = cabinqueues.get(cs.getCabinNumber().get()-1);
        if(!cs.inManagerMode())
        {
            if(q.get() == cs.getLastFloor().get()){
                q.pop();
            }
        }
        else if(cs.inManagerMode())
        {
            if(q.get() == cs.getLastFloorManager().get()){
                q.pop();
            }
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

    public ArrayList<FloorNumber> schedule(ArrayList<CabinStatus> cabinStatuses, ArrayList<Pair<CallButtons,CallButtons>> callbuttonpairs, boolean wasFire, ArrayList<Boolean> managerMode) {
        ArrayList<FloorNumber> nextFloors = new ArrayList<>();
        if(!wasFire) {
            for (Pair<CallButtons, CallButtons> pair : callbuttonpairs) {
                CallButtons b;
                for (int i = 0; i < 2; i++) {
                    boolean break_ = false;

                    if (i == 0) b = pair.getKey();
                    else b = pair.getKey();

                    if (!b.isPressed()) break;

                    int best_cabin = -1;
                    int best_score = -1;

                    // check if already scheduled or already on this floor
                    for (Queues q : cabinqueues) {
                        if (q.isIn(b.getFloor().get())) break_ = true;
//                        for (CabinStatus cs : cabinStatuses) {
//                            if (cs.getLastFloor().get() == b.getFloor().get()) break_ = true;
//                        }
                    }

                    if (break_) break;

                    for (CabinStatus cs : cabinStatuses) {
                        int s = suitability(cs, b);
                        if (s > best_score) {
                            best_score = s;
                            best_cabin = cs.getCabinNumber().get();
                        }
                    }

                    if (best_cabin >= 0) {
                        if (cabinStatuses.get(best_cabin - 1).getLastFloor().get() < b.getFloor().get()) {
                            cabinqueues.get(best_cabin - 1).addSorted(b.getFloor().get(), 1);
                        } else if (cabinStatuses.get(best_cabin - 1).getLastFloor().get() > b.getFloor().get()) {
                            cabinqueues.get(best_cabin - 1).addSorted(b.getFloor().get(), -1);
                        } else if(cabinStatuses.get(best_cabin-1).getLastFloor().get() == b.getFloor().get()){
                            cabinqueues.get(best_cabin-1).addSorted(b.getFloor().get(), cabinqueues.get(cabinStatuses.get(best_cabin-1).getCabinNumber().get()-1).direction);
                        }
                    }
                }
            }

            for (CabinStatus cs : cabinStatuses) {
                int cn = cs.getCabinNumber().get() - 1;
                HashSet<FloorNumber> requests = cs.getAllActiveRequests();

                // check if already scheduled
                Queues q = cabinqueues.get(cn);

                for (FloorNumber fn : requests) {
                    if (q.isIn(fn.get())) {
//                    System.out.println("in in");
                        break;
                    }
//                    if (cs.getLastFloor().get() == fn.get()) {
////                    System.out.println("last floor");
//                        break;
//                    }
//                System.out.println("ADD REQUEST");

                    if (cs.getLastFloor().get() < fn.get()) {
                        cabinqueues.get(cn).addSorted(fn.get(), 1);
                    } else if (cs.getLastFloor().get() > fn.get()) {
                        cabinqueues.get(cn).addSorted(fn.get(), -1);
                    } else if(cs.getLastFloor().get() == fn.get()){
                        cabinqueues.get(cn).addSorted(fn.get(), cabinqueues.get(cs.getCabinNumber().get()-1).direction);
                    }
                }
            }

            for (int i = 0; i < cabinStatuses.size(); i += 1) {
                Queues q = cabinqueues.get(i);
//            CabinStatus cs = cabinStatuses.get(i);
                int floor = q.get();

//            if(floor == -1) floor = cs.getLastFloor().get();
                nextFloors.add(i, new FloorNumber(floor));
            }
        }
        else if (wasFire){
//            System.out.println("wasFire");
            for(int i = 0; i < cabinStatuses.size(); i+= 1){
                if(!managerMode.get(i)){
                    cabinqueues.get(i).clear();
                    cabinqueues.get(i).clear();
                    if (cabinStatuses.get(i).getLastFloor().get() != 1) nextFloors.add(i, new FloorNumber(1));
                    else nextFloors.add(i, new FloorNumber(-1));
                }
                else {
//                    System.out.println("manager mode");
//                    System.out.println(cabinqueues.get(i).up.size() + " " + cabinqueues.get(i).down.size());
                    CabinStatus cs = cabinStatuses.get(i);
                    int cn = cs.getCabinNumber().get() - 1;
                    HashSet<FloorNumber> requests = cs.getAllActiveRequests();

                    // check if already scheduled
                    Queues q = cabinqueues.get(cn);

                    for (FloorNumber fn : requests) {
                        if (q.isIn(fn.get())) {
                            break;
                        }
                        if (cs.getLastFloor().get() == fn.get()) {
                            break;
                        }
//
//                        System.out.println("----------");
//                        for (int j : q.up) {
//                            System.out.print(j + " ");
//                        }
//                        System.out.println();
//                        for (int j : q.down) {
//                            System.out.print(j + " ");
//                        }
//                        System.out.println();
//                        System.out.println("----------");

                        if (cs.getLastFloor().get() < fn.get()) {
                            cabinqueues.get(cn).addSorted(fn.get(), 1);
                        } else if (cs.getLastFloor().get() > fn.get()) {
                            cabinqueues.get(cn).addSorted(fn.get(), -1);
                        }
                        else if(cs.getLastFloor().get() == fn.get()){
                            cabinqueues.get(cn).addSorted(fn.get(), cabinqueues.get(cs.getCabinNumber().get()-1).direction);
                        }
                    }
                    int floor = q.get();

                    nextFloors.add(i, new FloorNumber(floor));
                }
            }
        }
        for(int j = 0; j< cabinStatuses.size(); j+=1){
            if(cabinStatuses.get(j).getLastFloor().get() == 1) cabinqueues.get(j).direction = 1;
            if(cabinStatuses.get(j).getLastFloor().get() == 10) cabinqueues.get(j).direction = -1;
//            System.out.print(cabinqueues.get(j).direction);
        }
//        System.out.println();

        return nextFloors;
    }
}
