package control_logic;

import named_types.FloorNumber;

public class FloorAlignment
{

  //private final int _elevatorHeight = 100;
  //will have to change when the GUI is set up to know where the sensors are (their Y location on the GUI)
  //floors{0,1,2...9} since GUI increases going down
  private final int[] sensorLocations = {1000,900,800,700,600,500,400,300,200,100};

  private int lastFloor = 0;

  //TODO: Implement me
  FloorAlignment() {}

  // I think the cabin height should be a constant rather than a parameter passed
  public int alignedIndex(double topCabin, double cabinHeight)
  {
    double botCabin = topCabin + cabinHeight;
    int numSensors = sensorLocations.length;
    for (int i = 0; i < numSensors; i++)
    {
      //might have to change the inequalities
      //I have this way since the Y direction increases as you gon down the GUI
      if (sensorLocations[i] >= topCabin && sensorLocations[i] <= botCabin)
      {
        lastFloor = i + 1;
        return lastFloor;
      }
    }
    return lastFloor;
  }


}
