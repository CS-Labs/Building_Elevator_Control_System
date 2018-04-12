package application;

import engine.Engine;
import engine.Message;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import named_types.FloorNumber;

public class FloorRequestPanel extends GridPane
{
    private FloorNumber m_ActiveFloor;
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
        UpDownButton upButton = new UpDownButton(upOn, upOff);
        UpDownButton downButton = new UpDownButton(downOn, downOff);

        // Send whether the up/down arrow was pressed and what floor the press took place on.
        upButton.setOnAction((event) -> {Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.MANAGER_UP, m_ActiveFloor));});
        downButton.setOnAction((event) -> {Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.MANAGER_DOWN, m_ActiveFloor));});

        buttonPanel.add(upButton, 1, 0,5,1);
        buttonPanel.add(downButton, 1, 1,5,1);

        GridPane.setMargin(upButton, new Insets(10,10,10,30));
        GridPane.setMargin(downButton, new Insets(10,10,0,30));
        ChoiceBox<String> floorSelector = new ChoiceBox<>();
        floorSelector.getItems().addAll("Floor 1", "Floor 2", "Floor 3", "Floor 4", "Floor 5", "Floor 6", "Floor 7",
                "Floor 8", "Floor 9", "Floor 10");
        floorSelector.getSelectionModel().selectedIndexProperty().addListener(new
          ChangeListener<Number>(){
              @Override
              public void changed(ObservableValue<? extends Number> arg0,
                                  Number arg1, Number arg2)
              {
                  m_ActiveFloor = new FloorNumber(arg2.intValue()+1);
              }
          });
        buttonPanel.add(floorSelector, 1, 2, 5, 1);
        floorSelector.getSelectionModel().selectFirst();
        m_ActiveFloor = new FloorNumber(1);
        GridPane.setMargin(floorSelector, new Insets(0,0,0,15));
        return buttonPanel;

    }


}
