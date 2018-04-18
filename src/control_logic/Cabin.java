package control_logic;

import engine.Engine;
import engine.Message;
import engine.SceneManager;
import engine.Singleton;
import named_types.CabinNumber;
import named_types.FloorNumber;

import static engine.Engine.*;

class Cabin
{
    private CabinStatus cabinstatus;
    private CabinRequests cabinrequests;
    private MotionControl motioncontrol;
    private boolean lock = false;

    // TODO: Implement me.
    public Cabin(CabinNumber cb) {
        this.cabinstatus = new CabinStatus(cb);
        this.cabinrequests = new CabinRequests();
        this.motioncontrol = new MotionControl();
        Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_LOGIC_ENTITY, motioncontrol));
    }

    // this updates requests, call this first then call status.getAllActiveRequests so that it doesnt update
    public CabinStatus getStatus() {
        if(!lock) cabinrequests.getRequests();
        cabinstatus.setRequests(cabinrequests);
        cabinstatus.setLastFloor(motioncontrol.getLastFloor());
        cabinstatus.setMotionStatus(motioncontrol.getMotionStatus());
        cabinstatus.setDirection(motioncontrol.getDirection());
        return cabinstatus.getStatus();
    }

    public CabinStatus getStat(){
        return cabinstatus;
    }

    public void lockPanel() {this.lock = true;}
    public void unlockPanel() {this.lock = false;}
    // missing stuff probably
    public void setDestination(FloorNumber floor){
        this.cabinstatus.setDestination(floor);
        motioncontrol.setDestination(floor);
    }
}
