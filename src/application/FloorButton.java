package application;

import named_types.FloorNumber;
import engine.Engine;
import engine.Message;

class FloorButton extends ElevatorButton
{
    private FloorNumber m_FloorNumber;
    FloorButton(FloorNumber floorNumber)
    {
        m_FloorNumber = floorNumber;
        String onPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + floorNumber.get() + "ON.png";
        String offPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + floorNumber.get() + "OFF.png";
        super.setGraphic(onPath,offPath);
        // On press quickly toggle the light so the user can actually tell they pressed the button.
        // The light toggle holds no meaning behind it.
        this.setOnMousePressed((event) -> { this.setGraphic(onImg); });
        this.setOnMouseReleased((event) -> {
            this.setGraphic(offImg);
            Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.MANUAL_FLOOR_PRESS, m_FloorNumber));
        });
    }

}
