package control_logic;

import engine.LogicEntity;
import javafx.util.Pair;
import named_types.CabinNumber;
import named_types.DoorStatusType;
import named_types.FloorNumber;
import named_types.Speed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.rowset.spi.SyncResolver;

/**
 * Manages the simulation of opening/closing the elevator and lobby doors.
 */
class DoorControl implements LogicEntity {
    private final Speed _SPEED_MS = new Speed(0.25);
    private final int _NUM_FLOORS;
    private final int _NUM_CABINS;
    private volatile ElevatorDoorMotor[] _cabinMotors;
    private volatile ElevatorDoorMotor[][] _lobbyMotors;
    private Sensors _sensors;
    private HashSet<ElevatorDoorMotor> _simulatingMotors = new HashSet<>(); // Motors currently simulating movement
    private LinkedList<ElevatorDoorMotor> _completedSimulations = new LinkedList<>();
    //Map cabins to their current status.
    private volatile Map<Integer, DoorStatusType> _statusMap = new HashMap<>();
    private AtomicBoolean interference = new AtomicBoolean(false);

    public DoorControl(BuildingControl buildingControl, int numFloors, int numCabins) {
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
        for(int j = 1; j < 5; j ++)
        {
          _statusMap.put(j, DoorStatusType.CLOSED);
        }
        // Initialize sensors
        _sensors = new Sensors(this);
    }

    DoorStatusType getCurrStatus(int cabinNumber)
    {
      return _statusMap.get(cabinNumber);
    }

    @Override
    public void process(double deltaSeconds) {
        synchronized (this) {
            _completedSimulations.clear();
            int cabinNum = 0;
            for (ElevatorDoorMotor motor : _simulatingMotors) {
                motor.update(deltaSeconds);
                DoorStatusType val = _sensors.checkDoorStatus(motor.getOpenPercentage(),
                        motor.getStatus() == DoorStatusType.OPENING);
                //System.out.println(val);
                // See if the sensor has been triggered
                if (val == DoorStatusType.OPENED || val == DoorStatusType.CLOSED) {
                    _statusMap.replace((cabinNum + 1), val);
                    motor.notifyThatPositionSensorTriggered();
                    _completedSimulations.add(motor);
                }
                cabinNum ++;
            }
            _simulatingMotors.removeAll(_completedSimulations);
        }
    }

    public void open(FloorNumber floorNumber, CabinNumber cabinNumber) {
        synchronized (this) {
            DoorStatusType status = getStatus(floorNumber, cabinNumber);
            if (status == DoorStatusType.OPENED || status == DoorStatusType.OPENING) return; // Already opened/opening
            ElevatorDoorMotor lobby = _lobbyMotors[(floorNumber.get() - 1)][(cabinNumber.get() - 1 )];
            ElevatorDoorMotor cabin = _cabinMotors[(cabinNumber.get() - 1)];
            lobby.open(_SPEED_MS);
            cabin.open(_SPEED_MS);
            _simulatingMotors.add(lobby);
            _simulatingMotors.add(cabin);
            _statusMap.replace(cabinNumber.get(), cabin.getStatus());
        }
    }

    public void close(FloorNumber floorNumber, CabinNumber cabinNumber) {
        synchronized (this) {
            DoorStatusType status = getStatus(floorNumber, cabinNumber);
            if (status == DoorStatusType.CLOSED || status == DoorStatusType.CLOSING) return; // Already closed/closing
            ElevatorDoorMotor lobby = _lobbyMotors[(floorNumber.get() - 1)][(cabinNumber.get() - 1)];
            ElevatorDoorMotor cabin = _cabinMotors[(cabinNumber.get() - 1)];
            lobby.close(_SPEED_MS);
            cabin.close(_SPEED_MS);
            _simulatingMotors.add(lobby);
            _simulatingMotors.add(cabin);
            _statusMap.replace(cabinNumber.get(), cabin.getStatus());
        }
    }

    public DoorStatusType getStatus(FloorNumber floorNumber, CabinNumber cabinNumber) {
      synchronized(this)
      {
        return _lobbyMotors[(floorNumber.get() - 1)][(cabinNumber.get() - 1)].getStatus();
      }
    }

    public Pair<Double, Double> getInnerOuterDoorPercentageOpen(FloorNumber floor, CabinNumber cabin) {
      synchronized(this)
      {
        return new Pair<>(_cabinMotors[(cabin.get() - 1)].getOpenPercentage(),
            _lobbyMotors[(floor.get() - 1)][(cabin.get() - 1)].getOpenPercentage());
      }
    }

    public boolean manualInterferenceDetected() {
        return _sensors.manualInterferenceDetected();
    }

    public boolean interferenceDetected() {return _sensors.checkForInterference();}
}
