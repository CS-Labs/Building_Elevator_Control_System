package control_logic;

import named_types.DoorStatusType;

public class Sensors
{
    ElevatorDoorPositionSensor _doorPosSensor;
    OpticalInterferenceDetector _opticalDetector;
    FloorDoorPositionSensor _floorDoorSensor;
    DoorControl _doorControl;
    
    Sensors(DoorControl doorControl) 
    {
      _doorControl = doorControl;
      _doorPosSensor = new ElevatorDoorPositionSensor();
      _opticalDetector = new OpticalInterferenceDetector(this);
      _floorDoorSensor = new FloorDoorPositionSensor();
    }
    
    public DoorStatusType checkDoorStatus(double openPercent, boolean isOpening)
    {
      return _doorPosSensor.checkDoorStatus(openPercent, isOpening);
    }
    
    DoorControl getDoorControl()
    {
      return _doorControl;
    }
}
