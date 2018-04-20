package control_logic;

import engine.SceneManager;
import named_types.DirectionType;
import named_types.FloorNumber;
import engine.LogicEntity;
import named_types.Speed;

import java.util.ArrayList;

class MotionControl implements LogicEntity
{

  MotorSimulation motorSimulation = new MotorSimulation();
  FloorAlignment floorAlignment = new FloorAlignment();
  private int lastFloor = floorAlignment.alignedIndex(motorSimulation.getLocation());//should be 1 initially
  private FloorNumber floorToGoTO = null;
  private MotionStatusTypes motionStatus = MotionStatusTypes.STOPPED;
  private DirectionType direction = DirectionType.NONE;
  private boolean sensorDetected = false;

  //constants for speed profile
  private final double topSpeed = 25;
  private final double beforeStopSpeed = 0.1;
  private final double increaseRate = 0.005;
  private final double decreaseRate = 0.005;



  //TODO I think the position simulation is supposed to take place inside of Motion simulation but we can update that after the demo.
  @Override
  public void process(double deltaSeconds)
  {
    motorSimulation.update(deltaSeconds);
    lastFloor = floorAlignment.alignedIndex(motorSimulation.getLocation());
    sensorDetected = floorAlignment.getSensor();

    //update the speed
    speedUpdate();
  }

  double increaseByRate(double targetSpeed)
  {
    if(motorSimulation.getSpeed().get() + increaseRate >= targetSpeed)
      return targetSpeed;

    return (motorSimulation.getSpeed().get() + increaseRate);
  }

  double decreaseByRate (double targetSpeed)
  {
    if(motorSimulation.getSpeed().get() - decreaseRate <= targetSpeed)
      return targetSpeed;

    return (motorSimulation.getSpeed().get() - decreaseRate);
  }

  private void speedUpdate()
  {
    if(floorToGoTO == null) return;
    //Mina's speed profile should go here
    //experimental speed profile
    //negative speed is UP. positive is down.
    ArrayList<Integer> speedProfile = new ArrayList<>();
    speedProfile.add(0);
    speedProfile.add(1);
    speedProfile.add(0);

    //Updated speed by Mina and Javier
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /*if(lastFloor > floorToGoTO.get())
    {
      direction = DirectionType.DOWN;
      motionStatus = MotionStatusTypes.MOVING;

      if(lastFloor - floorToGoTO.get() == 1)
      {
        motorSimulation.setSpeed(new Speed(decreaseByRate(beforeStopSpeed)));
      }
      else
        motorSimulation.setSpeed(new Speed(increaseByRate(topSpeed)));
    }
    else if(lastFloor < floorToGoTO.get())
    {
      direction = DirectionType.UP;
      motionStatus = MotionStatusTypes.MOVING;

      if(floorToGoTO.get() - lastFloor == 1)
      {
        motorSimulation.setSpeed(new Speed(increaseByRate(-beforeStopSpeed)));
      }
      else
        motorSimulation.setSpeed(new Speed(decreaseByRate(-topSpeed)));
    }
    else
    {
      if(direction == DirectionType.DOWN)
      {
        if(sensorDetected)
        {
          motorSimulation.setSpeed(new Speed(0));
          motionStatus = MotionStatusTypes.STOPPED;
        }
      }

      else if(direction == DirectionType.UP)
      {
        if(!sensorDetected)
        {
          motorSimulation.setSpeed(new Speed(0));
          motionStatus = MotionStatusTypes.STOPPED;
        }
      }
    }
    *///////////////////////////////////////////////////////////////////////////////////////////////////
    //end of commented part




    if(lastFloor > floorToGoTO.get())
    {
//      if (motorSimulation.getSpeed().equals(speedProfile.get(speedProfile.size() / 2)))
//      {
//        motorSimulation.setSpeed(new Speed(speedProfile.get(speedProfile.size() / 2)));
//      }
//      else
//      {
//        increaseByRate(1);
//      }
      direction = DirectionType.DOWN;
      motionStatus = MotionStatusTypes.MOVING;
      motorSimulation.setSpeed(new Speed(25));
    }
    else if (lastFloor < floorToGoTO.get())
    {
      direction = DirectionType.UP;
      motionStatus = MotionStatusTypes.MOVING;
      motorSimulation.setSpeed(new Speed(-25));
    }
    else
    {
//      direction = DirectionType.NONE;
      motionStatus = MotionStatusTypes.STOPPED;
      motorSimulation.setSpeed(new Speed(0.0));
      //if we are approaching the desired floor, we should decrease speed.
     // motorSimulation.setSpeed(new Speed(decreaseByRate(speedProfile.get(speedProfile.size()-1))));
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

  public void setDestination(FloorNumber floor)
  {
    floorToGoTO = floor;
  }

  public boolean arrived()
  {
    return motorSimulation.getSpeed().get() == 0;
  }

}

/*
  private void speedUpdate()
  {
    //Mina's speed profile should go here
    //experimental speed profile
    //negative speed is UP. positive is down.
    ArrayList<Integer> speedProfile = new ArrayList<>();
    speedProfile.add(0);
    speedProfile.add(-1);
    speedProfile.add(0);

    int floorToGoInt = floorToGoTO.get();

    if (floorToGoInt - lastFloor >= 1)
    {
      if (motorSimulation.getSpeed().equals(speedProfile.get(speedProfile.size() / 2)))
      {
        motorSimulation.setSpeed(new Speed(speedProfile.get(speedProfile.size() / 2)));
      }
      else
      {
        increaseByRate(1);
      }
    }
    else
    {
      //if we are approaching the desired floor, we should decrease speed.
      motorSimulation.setSpeed(new Speed(decreaseByRate(speedProfile.get(speedProfile.size()-1))));
    }

  */
