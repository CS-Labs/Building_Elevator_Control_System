package application;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

class ElevatorButton extends Button
{
    ImageView onImg;
    ImageView offImg;

    ElevatorButton()
    {
        this.setStyle("-fx-background-radius: 57em; " + "-fx-min-width: 52px; "
                + "-fx-min-height: 52px; " + "-fx-max-width: 52px; "
                + "-fx-max-height: 52px;");
    }

    void setGraphic(String onImgPath, String offImgPath)
    {
        onImg = new ImageView(new Image(getClass().getResourceAsStream(onImgPath)));
        offImg = new ImageView(new Image(getClass().getResourceAsStream(offImgPath)));
        onImg.setFitWidth(50);
        onImg.setFitHeight(50);
        offImg.setFitHeight(50);
        offImg.setFitWidth(50);
        this.setGraphic(offImg);
    }
}
