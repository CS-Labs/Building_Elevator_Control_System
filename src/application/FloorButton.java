package application;

class FloorButton extends ElevatorButton
{
    private boolean m_Toggle = true;
    FloorButton(int floorNumber)
    {
        String onPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + floorNumber + "ON.png";
        String offPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + floorNumber + "OFF.png";
        super.setGraphic(onPath,offPath);
        this.setOnAction((event) -> {
            if(m_Toggle) this.setGraphic(onImg);
            else  this.setGraphic(offImg);
            m_Toggle = !m_Toggle;
        });
    }

}
