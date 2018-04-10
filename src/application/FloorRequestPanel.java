package application;

import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class FloorRequestPanel extends GridPane
{
    FloorRequestPanel(int x, int y)
    {
        this.setStyle("-fx-background-color: #000000");
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setHeight(320);
        this.setWidth(200);
        GridPane requestPanel = m_CreateFloorRequestPanel();
        this.add(requestPanel,1,2);
    }


    private GridPane m_CreateFloorRequestPanel()
    {
        GridPane buttonPanel = new GridPane();
        ImageView image = new ImageView("/resources/img/CCTV_Views/elevator/elevatorFloorPanel/buttonPanel.png");
        BackgroundImage backgroundImg = new BackgroundImage(image.getImage(), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, new BackgroundSize(200, 400, false, false, false, false));
        buttonPanel.setBackground(new Background(backgroundImg));
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        buttonPanel.setHgap(10);
        buttonPanel.setVgap(10);
        for(int i = 0; i < 5; i++) buttonPanel.getColumnConstraints().add(new ColumnConstraints(26));
        for(int i = 0; i < 6; i++) buttonPanel.getRowConstraints().add(new RowConstraints(55));

        //TODO the two buttons below should be up/down arrow buttons (circular button images are in resources folder).
        // TODO also need to add drop down menu to select floor. This panel is for the manager which allows them to
        // TODO select a floor and then make a floor request.
        String upOn = "/resources/img/CCTV_Views/elevator/cabin/directionLights/upON.png";
        String downOn = "/resources/img/CCTV_Views/elevator/cabin/directionLights/downON.png";
        String upOff = "/resources/img/CCTV_Views/elevator/cabin/directionLights/upOFF.png";
        String downOff = "/resources/img/CCTV_Views/elevator/cabin/directionLights/downOFF.png";
        //Add the up and down buttons.
        UpDownButton upButton = new UpDownButton(upOn, upOff, true, false);
        UpDownButton downButton = new UpDownButton(downOn, downOff, false, true);
        buttonPanel.add(upButton, 1, 0,5,1);
        buttonPanel.add(downButton, 1, 1,5,1);

        GridPane.setMargin(upButton, new Insets(10,10,10,30));
        GridPane.setMargin(downButton, new Insets(10,10,10,30));
        return buttonPanel;
    }


}
