package application;

import engine.Engine;
import engine.Message;

public class UpDownButton extends ElevatorButton
{
  boolean up = false;
  boolean down = false;
  
  UpDownButton(String onPath, String offPath, boolean up, boolean down)
  {
    this.up = up;
    this.down = down;
    super.setGraphic(onPath, offPath);
    this.setOnMousePressed((event) -> super.setGraphic(onImg));
    this.setOnMouseReleased((event) -> super.setGraphic(offImg));
  }

}
