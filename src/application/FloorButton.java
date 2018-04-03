package application;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

class FloorButton extends Button
{
    private boolean m_Toggle = true;
    FloorButton(int floorNumber, boolean open, boolean close, boolean fire)
    {
        String onPath = "";
        String offPath = "";
        if(floorNumber >= 0)
        {
          onPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + floorNumber + "ON.png";
          offPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + floorNumber + "OFF.png";
        }
        else if(open)
        {
          onPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/openON.png";
          offPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/openOFF.png";
        }
        else if(close)
        {
          onPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/closeON.png";
          offPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/closeOFF.png";
        }
        else if(fire)
        {
          onPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/fireON.png";
          offPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/fireOFF.png";
        }
        ImageView onImg = new ImageView(new Image(getClass().getResourceAsStream(onPath)));
        ImageView offImg = new ImageView(new Image(getClass().getResourceAsStream(offPath)));
        onImg.setFitWidth(50);
        onImg.setFitHeight(50);
        offImg.setFitHeight(50);
        offImg.setFitWidth(50);
        this.setStyle("-fx-background-radius: 57em; " + "-fx-min-width: 52px; "
                + "-fx-min-height: 52px; " + "-fx-max-width: 52px; "
                + "-fx-max-height: 52px;");
        this.setGraphic(offImg);
        this.setOnAction((event) -> {
            if(m_Toggle) this.setGraphic(onImg);
            else  this.setGraphic(offImg);
            m_Toggle = !m_Toggle;
        });
    }

}
