package application;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

class FloorButton extends Button
{
    private boolean m_Toggle = true;
    FloorButton(int floorNumber)
    {
        String onPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + floorNumber + "ON.png";
        String offPath = "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/" + floorNumber + "OFF.png";
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
