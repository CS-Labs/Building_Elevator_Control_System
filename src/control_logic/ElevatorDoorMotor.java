package control_logic;

import named_types.DoorStatusType;
import named_types.Speed;

import java.util.concurrent.atomic.AtomicReference;

public class ElevatorDoorMotor
{
    private double _metersMoved = 0.0;
    private double _sizeMeters = 1.0; // around 36 inches
    private double _openPercentage = 0.0; // 0.0 means totally closed
    private volatile Speed _speedMS = new Speed(0.0);
    private AtomicReference<DoorStatusType> _status = new AtomicReference<>(DoorStatusType.CLOSED);

    public ElevatorDoorMotor() {

    }

    public ElevatorDoorMotor(double sizeMeters) {
        _sizeMeters = sizeMeters;
    }

    public void open(Speed speed) {
        _speedMS = new Speed(Math.abs(speed.get())); // Ensure it is positive
        _status.set(DoorStatusType.OPENING);
    }

    public void close(Speed speed) {
        _speedMS = new Speed(-Math.abs(speed.get())); // Ensure it is negative for easier math
        _status.set(DoorStatusType.CLOSING);
    }

    public void notifyThatPositionSensorTriggered() {
        if (_status.get() == DoorStatusType.CLOSING) _status.set(DoorStatusType.CLOSED);
        else if (_status.get() == DoorStatusType.OPENING) _status.set(DoorStatusType.OPENED);
    }

    public DoorStatusType getStatus() {
        return _status.get();
    }

    public double getOpenPercentage() {
        return _openPercentage;
    }

    public void update(double deltaSeconds) {
        if (_status.get() == DoorStatusType.OPENING) {
            _metersMoved += _speedMS.get() * deltaSeconds;
            _metersMoved = Math.max(0.0, Math.min(_sizeMeters, _metersMoved));
            _openPercentage = _metersMoved / _sizeMeters;
        }
        else if (_status.get() == DoorStatusType.CLOSING) {
            _metersMoved += _speedMS.get() * deltaSeconds;
            _metersMoved = Math.max(0.0, Math.min(_sizeMeters, _metersMoved));
            _openPercentage = _metersMoved / _sizeMeters;
        }
    }
}
