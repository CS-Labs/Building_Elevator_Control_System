package control_logic;

import named_types.Speed;

public class MotorSimulation
{

  private Speed speed;
  private double loc = 370;//top of the cabin when it is on the bottom floor
  //TODO implement me.
  MotorSimulation()
  {
    speed = new Speed(0);
  }

  public double getLocation()
  {
    return loc;
  }

  public void update(double deltaSeconds)
  {
    double step = speed.get() * deltaSeconds;
    //update the location based on the speed and deltaSeconds
    loc += step;
  }

  public void setSpeed(Speed speed)
  {
    this.speed = speed;
  }

  public Speed getSpeed()
  {
    return speed;
  }
}
