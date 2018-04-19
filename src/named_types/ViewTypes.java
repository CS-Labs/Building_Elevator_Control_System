package named_types;

public enum ViewTypes
{
    ELEVATOR_ONE,
    ELEVATOR_TWO,
    ELEVATOR_THREE,
    ELEVATOR_FOUR,
    OVERVIEW;

    public int toInt()
    {
        switch(this)
        {
            case OVERVIEW:
                return 0;
            case ELEVATOR_ONE:
                return 1;
            case ELEVATOR_TWO:
                return 2;
            case ELEVATOR_THREE:
                return 3;
            case ELEVATOR_FOUR:
                return 4;
            default:
                throw new IllegalArgumentException("Invalid enum value.");
        }
    }

}
