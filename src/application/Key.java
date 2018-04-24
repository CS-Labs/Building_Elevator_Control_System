package application;

import engine.Engine;
import engine.Message;

class Key extends ElevatorButton
{
    boolean off = true;
    Key()
    {
        //TODO: Create an on/off graphics so the user can tell when the key is being used. Right now on/off is the same graphic.
       String offGraphic = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/keyhole.png";
       String onGraphic = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/keyhole_on.png";
        super.setGraphic(onGraphic, offGraphic);
        this.setOnMousePressed((event) -> { 
          if(off) 
          {
            off = false;
            this.setGraphic(onImg);
            Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.KEY_LOCK_CHANGE));
          }
          else if(!off) 
          {
            off = true; 
            this.setGraphic(offImg);
            Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.KEY_LOCK_CHANGE));
          }
        });
    }

}
