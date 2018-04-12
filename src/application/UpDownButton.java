package application;

public class UpDownButton extends ElevatorButton
{

  UpDownButton(String onPath, String offPath)
  {
    super.setGraphic(onPath, offPath);
    this.setOnMousePressed((event) -> super.setGraphic(onImg));
    this.setOnMouseReleased((event) -> super.setGraphic(offImg));
  }

}
