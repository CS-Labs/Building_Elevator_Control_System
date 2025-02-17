package application;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import named_types.FloorNumber;

class ElevatorPanel extends GridPane
{
    Key m_Key;
    ElevatorPanel(int x, int y)
    {
        
        this.setStyle("-fx-background-color: #000000");
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setHeight(320);
        this.setWidth(200);
        GridPane buttonPanel = m_CreateButtonPanel();
        this.add(buttonPanel,1,2);
    }

    private GridPane m_CreateButtonPanel() {
        GridPane buttonPanel = new GridPane();
        ImageView image = new ImageView("/resources/img/CCTV_Views/elevator/elevatorFloorPanel/buttonPanel.png");
        BackgroundImage backgroundImg = new BackgroundImage(image.getImage(), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, new BackgroundSize(200, 400, false, false, false, false));
        buttonPanel.setBackground(new Background(backgroundImg));
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        buttonPanel.setHgap(10);
        buttonPanel.setVgap(10);
        Queue<Integer> floors = new LinkedList<>(Arrays.asList(2, 1, 4, 3, 6, 5, 8, 7, 10, 9));
        for(int i = 0; i < 1; i++) buttonPanel.getColumnConstraints().add(new ColumnConstraints(25));
        for(int i = 0; i < 6; i++) buttonPanel.getRowConstraints().add(new RowConstraints(55));
        for(int r : Arrays.asList(4,3,2,1,0))
        {
            for(int c: Arrays.asList(2,1))
            {
                FloorButton fb = new FloorButton(new FloorNumber(floors.poll()));
                buttonPanel.add(fb,c,r);
            }
        }
        // Add the key.
        m_Key = new Key();
        buttonPanel.add(m_Key, 1, 5,5,1);
        GridPane.setMargin(m_Key, new Insets(10,10,10,30));
        return buttonPanel;
    }

    public Key getKey() {return m_Key;}


}

