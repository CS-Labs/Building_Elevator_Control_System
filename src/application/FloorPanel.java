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
        for(int i = 0; i < 5; i++) this.getColumnConstraints().add(new ColumnConstraints(50));
        for(int i = 0; i < 8; i++) this.getRowConstraints().add(new RowConstraints(50));
        for(int r : Arrays.asList(4,3,2,1,0))
        {
            for(int c: Arrays.asList(3,1))
            {
                this.add(new FloorButton(floors.poll()),c,r);
            }
        }
        // Add special function buttons.
        this.add(new SpecialFunctionButton(SpecialButtonTypes.CLOSE_DOORS),1,5);
        this.add(new SpecialFunctionButton(SpecialButtonTypes.SOUND_FIRE_ALARM), 2, 5);
        this.add(new SpecialFunctionButton(SpecialButtonTypes.OPEN_DOORS), 3,5);
    }
}
