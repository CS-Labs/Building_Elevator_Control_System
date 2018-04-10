package control_logic;

import named_types.FloorNumber;
import engine.LogicEntity;

class MotionControl implements LogicEntity
{

  private double cabinHeight = 100;
  MotorSimulation motorSimulation = new MotorSimulation();
  FloorAlignment floorAlignment = new FloorAlignment();
  private int lastFloor = floorAlignment.alignedIndex(motorSimulation.getLocation(), cabinHeight);//should be 1 initially

  //TODO Implement me
  @Override
  public void process(double deltaSeconds)
  {
    //don't know how to get delta seconds
    //call motorSimulation.update(delta seconds);
    //call lastFloor = floorAlignment.alignedIndex(motorSimulation.getLocation(), cabinHeight);
  }

  //why do we have FloorNumber?
  public FloorNumber getLastFloor()
  {
    //will return the last floor it has passed, if it is currently at a floor then it will return that floor
    return new FloorNumber(lastFloor);
  }

  // TODO: 4/9/2018
  public void setDestination(FloorNumber floor)
  {

  }

  public boolean arrived()
  {
    return motorSimulation.getSpeed().get() == 0;
  }

}
