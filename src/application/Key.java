package application;

import engine.Engine;
import engine.Message;

class Key extends ElevatorButton
{
    Key()
    {
        //TODO: Create an on/off graphics so the user can tell when the key is being used. Right now on/off is the same graphic.
       String graphic = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/keyhole.png";
        super.setGraphic(graphic, graphic);
        this.setOnMousePressed((event) -> { this.setGraphic(onImg); });
        this.setOnMouseReleased((event) -> {
            this.setGraphic(offImg);
            Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.KEY_LOCK_CHANGE));
        });
    }

}
