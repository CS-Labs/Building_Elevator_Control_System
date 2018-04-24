package control_logic;

import named_types.DoorStatusType;

class Sensors
{
    ElevatorDoorPositionSensor _doorPosSensor;
    OpticalInterferenceDetector _opticalDetector;
    FloorDoorPositionSensor _floorDoorSensor;
    DoorControl _doorControl;
    
    Sensors(DoorControl doorControl) 
    {
      _doorControl = doorControl;
      _doorPosSensor = new ElevatorDoorPositionSensor();
      _opticalDetector = new OpticalInterferenceDetector();
      _floorDoorSensor = new FloorDoorPositionSensor();
    }
    
    DoorStatusType checkDoorStatus(double openPercent, boolean isOpening) {
      return _doorPosSensor.checkDoorStatus(openPercent, isOpening);
    }

    boolean manualInterferenceDetected() {
        return _opticalDetector.manualInterferenceDetected();
    }

    boolean checkForInterference() {return _opticalDetector.interferenceDetected();}
}
