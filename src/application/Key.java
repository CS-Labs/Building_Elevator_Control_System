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
        this.setOnAction((event) -> {
            if(!m_On) Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.KEY_LOCK_ACTIVATED));
            else Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.KEY_LOCK_DEACTIVATED));
            m_On = !m_On;
        });
    }

}
