package application;

import javafx.scene.layout.GridPane;

public class BottomPanel extends GridPane
{
    BottomPanel(int x, int y){
        this.setStyle("-fx-background-color: #000000"); // Just so we can see where things are for now.
        this.setLayoutX(x);
        this.setLayoutY(y);
        // Arbitrary for now.
        this.setPrefHeight(300);
        this.setPrefWidth(1000);
    }
}
