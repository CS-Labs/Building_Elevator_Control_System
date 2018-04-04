package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import control_logic.FloorNumberTypes;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

class FloorPanel extends GridPane
{
    private ArrayList<FloorButton> m_FloorButtons = new ArrayList<>();
    private ArrayList<SpecialFunctionButton> m_SpecialFunctionButtons = new ArrayList<>();
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
        Queue<Integer> floors = new LinkedList<>(Arrays.asList(2, 1, 4, 3, 6, 5, 8, 7, 10, 9));
        for(int i = 0; i < 4; i++) this.getColumnConstraints().add(new ColumnConstraints(67));
        for(int i = 0; i < 8; i++) this.getRowConstraints().add(new RowConstraints(52));
        for(int r : Arrays.asList(4,3,2,1,0))
        {
            for(int c: Arrays.asList(2,1))
            {
                FloorButton fb = new FloorButton(FloorNumberTypes.values()[floors.poll()]);
                m_FloorButtons.add(fb);
                this.add(fb,c,r);
            }
        }
        // Add special function buttons.
        m_SpecialFunctionButtons.add(new SpecialFunctionButton(SpecialButtonTypes.STOP));
        m_SpecialFunctionButtons.add(new SpecialFunctionButton(SpecialButtonTypes.KEY));
        for(int i = 1; i <= 2; i++) this.add(m_SpecialFunctionButtons.get(i-1),i,5);
    }


}
