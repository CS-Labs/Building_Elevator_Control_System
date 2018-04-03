package application;

import control_logic.FloorNumberTypes;
import engine.Engine;
import engine.Message;

class FloorButton extends ElevatorButton
{
    private FloorNumberTypes m_FloorNumber;
    FloorButton(FloorNumberTypes floorNumber)
    {
        m_FloorNumber = floorNumber;
        String onPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + floorNumber.toDigit() + "ON.png";
        String offPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + floorNumber.toDigit() + "OFF.png";
        super.setGraphic(onPath,offPath);
        this.setOnAction((event) -> {
            if(!m_On) {
                this.setGraphic(onImg);
                Engine.getMessagePump().sendMessage(new Message(SimGlobals.MANUAL_FLOOR_PRESS_ON, m_FloorNumber));
            }
            else  {
                this.setGraphic(offImg);
                Engine.getMessagePump().sendMessage(new Message(SimGlobals.MANUAL_FLOOR_PRESS_OFF, m_FloorNumber));
            }
            m_On = !m_On;

        });
    }

}
