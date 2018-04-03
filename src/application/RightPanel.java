package application;

import javafx.scene.layout.GridPane;

class RightPanel extends GridPane
{
    RightPanel(int x, int y)
    {
        this.setStyle("-fx-background-color: #000000"); // Just so we can see where things are for now.
        this.setLayoutX(x);
        this.setLayoutY(y);
        // Arbitrary
        this.setPrefHeight(400);
        this.setPrefWidth(300);

        this.add(new FloorPanel(),1,2);
    }
}
