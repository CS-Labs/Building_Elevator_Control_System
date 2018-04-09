package named_types;

public enum ArrowButtonStates
{
    NOTHING_PRESSED,
    UP_PRESSED,
    DOWN_PRESSED,
    UP_AND_DOWN_PRESSED;

    @Override
    public String toString() {
        switch(this)
        {
            case NOTHING_PRESSED:
                return "noRequest.png";
            case UP_PRESSED:
                return "upRequest.png";
            case DOWN_PRESSED:
                return "downRequest.png";
            case UP_AND_DOWN_PRESSED:
                return "upAndDownRequest.png";
            default:
                    throw new IllegalArgumentException("Missing handling of enum value.");
        }
    }
}
