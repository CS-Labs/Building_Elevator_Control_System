package control_logic;

import named_types.FloorNumber;
import engine.LogicEntity;
import named_types.Speed;

class MotionControl implements LogicEntity
{

  MotorSimulation motorSimulation = new MotorSimulation();
  FloorAlignment floorAlignment = new FloorAlignment();
  private FloorNumber floorToGoTO;
  private int lastFloor = floorAlignment.alignedIndex(motorSimulation.getLocation());//should be 1 initially

  //TODO Implement me
  @Override
  public void process(double deltaSeconds)
  {
    motorSimulation.update(deltaSeconds);
    lastFloor = floorAlignment.alignedIndex(motorSimulation.getLocation());

    //update the speed
    speedUpdate();
  }

  private void speedUpdate()
  {
    //Mina's speed profile should go here
    if(lastFloor > floorToGoTO.get())
    {
      motorSimulation.setSpeed(new Speed(0.5));
    }
    else if (lastFloor < floorToGoTO.get())
    {
      motorSimulation.setSpeed(new Speed(-0.5));
    }
    else
    {
      motorSimulation.setSpeed(new Speed(0));
    }
  }

  public FloorNumber getLastFloor()
  {
    //will return the last floor it has passed, if it is currently at a floor then it will return that floor
    return new FloorNumber(lastFloor);
  }

  // TODO: 4/9/2018
  public void setDestination(FloorNumber floor)
  {
    floorToGoTO = floor;
  }

  public boolean arrived()
  {
    return motorSimulation.getSpeed().get() == 0;
  }

}
