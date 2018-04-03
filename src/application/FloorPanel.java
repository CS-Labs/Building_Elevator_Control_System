package application;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

class FloorPanel extends GridPane
{
    FloorPanel()
    {
        //this.setStyle("-fx-background-color: #b7babf");
        ImageView image = new ImageView("/resources/img/CCTV_Views/elevator/elevatorFloorPanel/buttonPanel.png");
        image.setFitHeight(400);
        image.setFitWidth(300);
        image.setPreserveRatio(true);
        BackgroundImage backgroundImg = new BackgroundImage(image.getImage(), null, null, null, null);
        this.setBackground(new Background(backgroundImg));
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setHgap(10);
        this.setVgap(10);
        Queue<Integer> floors = new LinkedList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));
        this.getColumnConstraints().add(new ColumnConstraints(50));
        this.getColumnConstraints().add(new ColumnConstraints(50));
        this.getColumnConstraints().add(new ColumnConstraints(50));
        this.getColumnConstraints().add(new ColumnConstraints(50));
        this.getColumnConstraints().add(new ColumnConstraints(50));
        this.getColumnConstraints().add(new ColumnConstraints(50));
        this.getRowConstraints().add(new RowConstraints(50));
        this.getRowConstraints().add(new RowConstraints(50));
        this.getRowConstraints().add(new RowConstraints(50));
        this.getRowConstraints().add(new RowConstraints(50));
        this.getRowConstraints().add(new RowConstraints(50));
        this.getRowConstraints().add(new RowConstraints(50));
        for(int r : Arrays.asList(4,3,2,1,0))
        {
            for(int c: Arrays.asList(3,1))
            {
                this.add(new FloorButton(floors.poll(),false,false,false),c,r);
            }
        }
        this.add(new FloorButton(-1, true, false, false), 1, 5);
        this.add(new FloorButton(-1, false, true, false), 2, 5);
        this.add(new FloorButton(-1, false, false, true), 3, 5);
    }
}
