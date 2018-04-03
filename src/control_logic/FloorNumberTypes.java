package control_logic;

public enum FloorNumberTypes
{
    NONE(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10);

    private final int value;
    FloorNumberTypes(int value) { this.value = value; }
    public int toDigit() { return value; }

}
