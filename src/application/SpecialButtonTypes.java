package application;

public enum SpecialButtonTypes
{
    STOP,
    KEY;

    @Override
    public String toString() {
        switch(this)
        {
            case STOP:
                return "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/stop";
            case KEY:
                return "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/keyhole.png";
        }
        throw new IllegalArgumentException("No matching ENUM value. Something went wrong.");
    }
}
