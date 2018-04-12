package control_logic;

import named_types.DoorStatusType;

public class ElevatorDoorPositionSensor
{
    ElevatorDoorPositionSensor() {

    }

    // openPercent should be on the range [0.0, 1.0]
    public DoorStatusType checkDoorStatus(double openPercent, boolean isOpening) {
        if (openPercent == 0.0) return DoorStatusType.CLOSED;
        else if (openPercent == 1.0) return DoorStatusType.OPENED;
        else if (isOpening) return DoorStatusType.OPENING;
        return DoorStatusType.CLOSING;
    }
}
