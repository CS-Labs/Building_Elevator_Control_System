package application;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;


class LeftPanel extends GridPane
{
    LeftPanel(int x, int y)
    {
        Label infoLabel = new Label("Elevator Information:");
        Label loadLabel = new Label ("Load (kg):");
        Label speedLabel = new Label("Speed: (m/s):");
        setLabelStyle(infoLabel);
        setLabelStyle(loadLabel);
        setLabelStyle(speedLabel);
        this.setStyle("-fx-background-color: #000000"); // Just so we can see where things are for now.
        this.setLayoutX(x);
        this.setLayoutY(y);
        // Arbitrary
        this.setPrefHeight(700);
        this.setPrefWidth(300);
        this.add(infoLabel,0,0);
        this.add(loadLabel,0,1);
        this.add(speedLabel,0,3);
    }

    private void setLabelStyle(Label l)
    {
        l.setStyle("    -fx-padding: 20;\n" +
                "    -fx-spacing: 10;\n" +
                "    -fx-alignment: center;\n" +
                "    -fx-font-size: 20;");
        l.setTextFill(Color.web("#FFFFFF"));
    }
}
