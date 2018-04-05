package application;

import java.net.URL;
import java.util.ResourceBundle;

import engine.Engine;
import engine.Message;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class BottomPanelController implements Initializable
{
//  @FXML Button systemOverview;
//  @FXML MenuButton cctvMenu;

  @FXML RadioButton lockOne;
  @FXML RadioButton lockTwo;
  @FXML RadioButton lockThree;
  @FXML RadioButton lockFour;
  @FXML RadioButton viewOne;
  @FXML RadioButton viewTwo;
  @FXML RadioButton viewThree;
  @FXML RadioButton viewFour;
  @FXML RadioButton overview;
  @FXML Button fireAlarm;
  boolean m_On = false;

  @Override
  public void initialize(URL location, ResourceBundle resources)
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
    fireAlarm.setOnAction((event) -> {
    if(!m_On) {
      fireAlarm.setGraphic(onImg);
    }
    else  {
      fireAlarm.setGraphic(offImg);
    }
    m_On = !m_On;

  });



    ToggleGroup lockToggleGroup = new ToggleGroup();
    lockOne.setToggleGroup(lockToggleGroup);
    lockTwo.setToggleGroup(lockToggleGroup);
    lockThree.setToggleGroup(lockToggleGroup);
    lockFour.setToggleGroup(lockToggleGroup);
    ToggleGroup viewToggleGroup = new ToggleGroup();
    viewOne.setToggleGroup(viewToggleGroup);
    viewTwo.setToggleGroup(viewToggleGroup);
    viewThree.setToggleGroup(viewToggleGroup);
    viewFour.setToggleGroup(viewToggleGroup);

    lockToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> System.out.println(newVal + " was selected"));
    viewToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> System.out.println(newVal + " was selected"));

  }

}
