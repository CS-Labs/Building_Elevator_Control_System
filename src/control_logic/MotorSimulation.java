package control_logic;

import named_types.Speed;

public class MotorSimulation
{

  private Speed speed;
  private double loc = 1000;//bottom floor, might have to change depending how big the GUI is
  //TODO implement me.
  MotorSimulation()
  {
  }

  public double getLocation()
  {
    return loc;
  }

  public void update(double deltaSeconds)
  {
    //update the location based on the speed
    loc += speed.get();
  }

  public void setSpeed(Speed speed)
  {
    this.speed = speed;
  }

  //why do we have Speed(class/type)?
  public Speed getSpeed()
  {
    return speed;
  }
}
