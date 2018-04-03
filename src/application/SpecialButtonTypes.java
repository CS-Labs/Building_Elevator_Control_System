package application;

public enum SpecialButtonTypes
{
    OPEN_DOORS,
    CLOSE_DOORS,
    SOUND_FIRE_ALARM;

    @Override
    public String toString() {
        switch(this)
        {
            case OPEN_DOORS:
                return "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/open";
            case CLOSE_DOORS:
                return "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/close";
            case SOUND_FIRE_ALARM:
                return "/resources/img/CCTV_Views/elevator/elevatorFloorPanel/fire";
        }
        throw new IllegalArgumentException("No matching ENUM value. Something went wrong.");
    }
}
