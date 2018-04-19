package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import engine.Engine;
import engine.Message;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import named_types.ViewTypes;

public class SystemOverviewPanel implements Initializable
{

  @FXML RadioButton viewOne;
  @FXML RadioButton viewTwo;
  @FXML RadioButton viewThree;
  @FXML RadioButton viewFour;
  @FXML RadioButton overview;
  @FXML 
  Pane lockPane;
  @FXML Button fireAlarm;
  // List of boolean values where each index correspond to the index+1 elevator panel and the boolean value
  // corresponds to whether said elevator panel is locked or not.
  ArrayList<Boolean> m_LockedPanels = new ArrayList<>(Arrays.asList(false, false, false, false));
  boolean m_On = false;
  int m_floorSelected = 0;


  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
    m_SetupViewButtons();
    m_SetupFireAlarmPanel();
    m_SetupLockButtons();
  }


  private void m_SetupFireAlarmPanel()
  {
    fireAlarm.setText("");
    fireAlarm.setStyle("-fx-background-radius: 57em; " + "-fx-min-width: 52px; "
            + "-fx-min-height: 52px; " + "-fx-max-width: 52px; "
            + "-fx-max-height: 52px;");
    ImageView onImg = new ImageView(new Image(getClass().getResourceAsStream("/resources/img/Building_Overview/Building_Fire_Alarm/fireON.png")));
    ImageView offImg = new ImageView(new Image(getClass().getResourceAsStream("/resources/img/Building_Overview/Building_Fire_Alarm/fireOFF.png")));
    onImg.setFitWidth(50);
    onImg.setFitHeight(50);
    offImg.setFitHeight(50);
    offImg.setFitWidth(50);
    fireAlarm.setGraphic(offImg);
    fireAlarm.setOnMousePressed((event) -> { fireAlarm.setGraphic(onImg); });
    fireAlarm.setOnMouseReleased((event) -> {
      fireAlarm.setGraphic(offImg);
      Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.ALARM_PRESS));
    });
  }

  private void m_SetupViewButtons()
  {
    ToggleGroup viewToggleGroup = new ToggleGroup();
    viewOne.setToggleGroup(viewToggleGroup);
    viewTwo.setToggleGroup(viewToggleGroup);
    viewThree.setToggleGroup(viewToggleGroup);
    viewFour.setToggleGroup(viewToggleGroup);
    overview.setToggleGroup(viewToggleGroup);
    viewToggleGroup.selectedToggleProperty().addListener((ov, oldToggle, newToggle) -> {
        if (viewToggleGroup.getSelectedToggle() != null)
        {
          String stringView = viewToggleGroup.getSelectedToggle().getUserData().toString();
          Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.CHANGE_VIEW, m_MapToView(stringView)));
        }
      });
    overview.setSelected(true);

  }

  /*
     Unfortunately this cannot be done in fxml, basically because we need toggle buttons with a
     radio button look. We need toggle buttons over radio buttons because multiple panels can be locked
     at a time.
   */
  private void m_SetupLockButtons()
  {
    m_SetupLockButton(2,52,"Elevator One", 0);
    m_SetupLockButton(2,80,"Elevator Two", 1);
    m_SetupLockButton(2,106,"Elevator Three", 2);
    m_SetupLockButton(2,135,"Elevator Four", 3);

  }
  private void m_SetupLockButton(int x, int y, String text, int index)
  {
    ToggleButton lockButton = new RadioButton(text);
    lockButton.selectedProperty().addListener(((observable, oldValue, newValue) -> {
      m_LockedPanels.set(index,newValue);
      Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.LOCK_PANEL_UPDATE, m_LockedPanels));
    }));
    lockButton.setLayoutX(x);
    lockButton.setLayoutY(y);
    lockButton.setStyle("-fx-font-size: 9pt; -fx-text-fill: white;");
    lockPane.getChildren().add(lockButton);
  }


  private ViewTypes m_MapToView(String buttonData)
  {
    switch(buttonData)
    {
      case "viewOne":
        return ViewTypes.ELEVATOR_ONE;
      case "viewTwo":
        return ViewTypes.ELEVATOR_TWO;
      case "viewThree":
        return ViewTypes.ELEVATOR_THREE;
      case "viewFour":
        return ViewTypes.ELEVATOR_FOUR;
      case "overview":
        return ViewTypes.OVERVIEW;
        default:
          throw new IllegalArgumentException("Invalid button data type.");
    }
  }


}
