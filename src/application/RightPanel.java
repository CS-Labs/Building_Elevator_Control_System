package application;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

class RightPanel extends GridPane
{
    RightPanel(int x, int y)
    {
        this.setStyle("-fx-background-color: #000000"); // Just so we can see where things are for now.
        this.setLayoutX(x);
        this.setLayoutY(y);
        // Arbitrary
        this.setPrefHeight(700);
        this.setPrefWidth(300);

        this.add(new FloorPanel(),1,2);
    }
}
