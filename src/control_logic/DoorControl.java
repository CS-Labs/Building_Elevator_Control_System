package control_logic;

import engine.LogicEntity;
import javafx.util.Pair;
import named_types.CabinNumber;
import named_types.DoorStatusType;
import named_types.FloorNumber;
import named_types.Speed;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the simulation of opening/closing the elevator and lobby doors.
 */
class DoorControl implements LogicEntity {
    private final Speed _SPEED_MS = new Speed(0.25);
    private final int _NUM_FLOORS;
    private final int _NUM_CABINS;
    private ElevatorDoorMotor[] _cabinMotors;
    private ElevatorDoorMotor[][] _lobbyMotors;
    private ElevatorDoorPositionSensor _sensor;
    private HashSet<ElevatorDoorMotor> _simulatingMotors = new HashSet<>(); // Motors currently simulating movement
    private LinkedList<ElevatorDoorMotor> _completedSimulations = new LinkedList<>();

    public DoorControl(int numFloors, int numCabins) {
        _NUM_FLOORS = numFloors;
        _NUM_CABINS = numCabins;
        _cabinMotors = new ElevatorDoorMotor[_NUM_CABINS];
        _lobbyMotors = new ElevatorDoorMotor[_NUM_FLOORS][_NUM_CABINS];
        // Initialize all the motors
        for (int i = 0; i < _NUM_CABINS; ++i) {
            _cabinMotors[i] = new ElevatorDoorMotor();
            for (int j = 0; j < _NUM_FLOORS; ++j) {
                _lobbyMotors[j][i] = new ElevatorDoorMotor();
            }
        }
        // Initialize the sensor
        _sensor = new ElevatorDoorPositionSensor();
    }

    @Override
    public void process(double deltaSeconds) {
        synchronized (this) {
            _completedSimulations.clear();
            for (ElevatorDoorMotor motor : _simulatingMotors) {
                motor.update(deltaSeconds);
                DoorStatusType val = _sensor.checkDoorStatus(motor.getOpenPercentage(),
                        motor.getStatus() == DoorStatusType.OPENING);
                // See if the sensor has been triggered
                if (val == DoorStatusType.OPENED || val == DoorStatusType.CLOSED) {
                    motor.notifyThatPositionSensorTriggered();
                    _completedSimulations.add(motor);
                }
            }
            _simulatingMotors.removeAll(_completedSimulations);
        }
    }

    public void open(FloorNumber floorNumber, CabinNumber cabinNumber) {
        synchronized (this) {
            ElevatorDoorMotor lobby = _lobbyMotors[floorNumber.get()][cabinNumber.get()];
            ElevatorDoorMotor cabin = _cabinMotors[cabinNumber.get()];
            lobby.open(_SPEED_MS);
            cabin.open(_SPEED_MS);
            _simulatingMotors.add(lobby);
            _simulatingMotors.add(cabin);
        }
    }

    public void close(FloorNumber floorNumber, CabinNumber cabinNumber) {
        synchronized (this) {
            ElevatorDoorMotor lobby = _lobbyMotors[floorNumber.get()][cabinNumber.get()];
            ElevatorDoorMotor cabin = _cabinMotors[cabinNumber.get()];
            lobby.close(_SPEED_MS);
            cabin.close(_SPEED_MS);
            _simulatingMotors.add(lobby);
            _simulatingMotors.add(cabin);
        }
    }

    public DoorStatusType getStatus(FloorNumber floorNumber, CabinNumber cabinNumber) {
        return _lobbyMotors[floorNumber.get()][cabinNumber.get()].getStatus();
    }

    public double getOuterDoorsPercentageOpen(FloorNumber floorNumber, CabinNumber cabinNumber) {
        return _lobbyMotors[floorNumber.get()][cabinNumber.get()].getOpenPercentage();
    }

    public double getInnerDoorsPercentageOpen(CabinNumber cabinNumber) {
        return _cabinMotors[cabinNumber.get()].getOpenPercentage();
    }


}
