package application;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

class FloorPanel extends GridPane
{
    FloorPanel()
    {
        this.setStyle("-fx-background-color: #b7babf");
        // this.setStyle("-fx-background-image: url('/resources/img/CCTV_Views/elevator/elevatorFloorPanel/buttonPanel.png')"); size is off..
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setHgap(10);
        this.setVgap(10);
        Queue<Integer> floors = new LinkedList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));
        for(int r : IntStream.range(0,5).toArray())
        {
            for(int c: IntStream.range(0,2).toArray())
            {
                this.add(new FloorButton(floors.poll()),c,r);
            }
        }



    }
}
