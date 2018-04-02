package application;

import javafx.scene.layout.GridPane;

public class LeftPanel extends GridPane
{
    LeftPanel(int x, int y)
    {
        this.setStyle("-fx-background-color: #000000"); // Just so we can see where things are for now.
        this.setLayoutX(x);
        this.setLayoutY(y);
        // Arbitrary
        this.setPrefHeight(700);
        this.setPrefWidth(300);
    }
}
