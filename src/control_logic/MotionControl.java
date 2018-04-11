package control_logic;

import engine.SceneManager;
import named_types.DirectionType;
import named_types.FloorNumber;
import engine.LogicEntity;
import named_types.Speed;

class MotionControl implements LogicEntity
{

  MotorSimulation motorSimulation = new MotorSimulation();
  FloorAlignment floorAlignment = new FloorAlignment();
  private int lastFloor = floorAlignment.alignedIndex(motorSimulation.getLocation());//should be 1 initially
  private FloorNumber floorToGoTO = null;
  private MotionStatusTypes motionStatus = MotionStatusTypes.STOPPED;
  private DirectionType direction = DirectionType.NONE;



  //TODO I think the position simulation is supposed to take place inside of Motion simulation but we can update that after the demo.
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
    if(floorToGoTO == null) return;
    //Mina's speed profile should go here
    if(lastFloor > floorToGoTO.get())
    {
      direction = DirectionType.DOWN;
      motionStatus = MotionStatusTypes.MOVING;
      motorSimulation.setSpeed(new Speed(5));
    }
    else if (lastFloor < floorToGoTO.get())
    {
      direction = DirectionType.UP;
      motionStatus = MotionStatusTypes.MOVING;
      motorSimulation.setSpeed(new Speed(-5));
    }
    else
    {
      motionStatus = MotionStatusTypes.STOPPED;
      motorSimulation.setSpeed(new Speed(0));
    }
  }

  public FloorNumber getLastFloor()
  {
    //will return the last floor it has passed, if it is currently at a floor then it will return that floor
    return new FloorNumber(lastFloor);
  }

  public MotionStatusTypes getMotionStatus()
  {
    return motionStatus;
  }

  public DirectionType getDirection()
  {
    return direction;
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
