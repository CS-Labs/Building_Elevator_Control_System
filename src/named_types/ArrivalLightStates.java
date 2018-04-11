package named_types;

public enum ArrivalLightStates
{
    NO_ARRIVAL,
    ARRIVAL_GOING_UP,
    ARRIVAL_GOING_DOWN;

    @Override
    public String toString() {
        switch(this)
        {
            case NO_ARRIVAL:
                return "noArrival.png";
            case ARRIVAL_GOING_UP:
                return "arrivalGoingUp.png";
            case ARRIVAL_GOING_DOWN:
                return "arrivalGoingDown.png";
            default:
                throw new IllegalArgumentException("Unhandled enum type.");
        }
    }
}
