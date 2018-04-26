package application;

import engine.Engine;
import engine.Message;

class Key extends ElevatorButton
{
    boolean off = true;
    Key()
    {
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
    public void turnOff()
    {
        System.out.println("Turning off");
        off = true;
        this.setGraphic(offImg);
    }

    public void turnOn()
    {
        System.out.println("Turning on");
        off = false;
        this.setGraphic(onImg);
    }

}
