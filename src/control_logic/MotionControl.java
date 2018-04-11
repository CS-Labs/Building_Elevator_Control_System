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



  //TODO I think the position simulation is supposed to take place inside of Motion simulation but we can update that after the demo.
  @Override
  public void process(double deltaSeconds)
  {
    motorSimulation.update(deltaSeconds);
    lastFloor = floorAlignment.alignedIndex(motorSimulation.getLocation());

    //update the speed
    speedUpdate();
  }

  double increaseByRate(int targetSpeed)
  {
    double rate = 0.005;

    return (targetSpeed + rate);
  }

  double decreaseByRate (int targetSpeed)
  {
    double rate = 0.005;

    return (targetSpeed - rate);
  }

  private void speedUpdate()
  {
    if(floorToGoTO == null) return;
    //Mina's speed profile should go here
    //experimental speed profile
    //negative speed is UP. positive is down.
    ArrayList<Integer> speedProfile = new ArrayList<>();
    speedProfile.add(0);
    speedProfile.add(-1);
    speedProfile.add(0);
    System.out.println(motorSimulation.getSpeed());
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


    //Javi's
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

  */