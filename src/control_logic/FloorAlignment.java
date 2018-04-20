package control_logic;

import named_types.FloorNumber;

public class FloorAlignment
{

  //private final int _elevatorHeight = 100;
  //will have to change when the GUI is set up to know where the sensors are (their Y location on the GUI)
  //floors{0,1,2...9} since GUI increases going draws downward
  //sensors will be at the bottom of the floor
  private final int[] sensorLocations = {400, 360, 320, 280, 240, 200, 160, 120, 80, 40};

  private int lastFloor = 0;
  private double cabinHeight = 30;//actual height of the cabin in pixels
  private boolean sensorDetected = false;

  //TODO: Implement me
  FloorAlignment() {}

  // I think the cabin height should be a constant rather than a parameter passed
  //return the floor a sensor was last triggered. If the cabin is stopped then it will return that floor
  public int alignedIndex(double topCabin)
  {
    double botCabin = topCabin + cabinHeight;
    int numSensors = sensorLocations.length;
    for (int i = 0; i < numSensors; i++)
    {
      //might have to change the inequalities
      //I have it this way since the Y direction increases as you gon down the GUI
      if (sensorLocations[i] >= topCabin && sensorLocations[i] <= botCabin)
      {
        sensorDetected = true;
        lastFloor = i + 1;
        return lastFloor;
      }
    }
    sensorDetected = false;
    return lastFloor;
  }

  public boolean getSensor()
  {
    return sensorDetected;
  }


}
