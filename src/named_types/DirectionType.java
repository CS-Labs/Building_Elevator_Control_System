package named_types;
/*
Use this to know what direction a button is for. Could probably be used for other stuff.
 */
public enum DirectionType {
    DOWN,
    UP,
    NONE;
    public static DirectionType switchDirection(DirectionType direction){
        if(direction == DirectionType.DOWN) return DirectionType.UP;
        if(direction == DirectionType.UP) return DirectionType.DOWN;
        return DirectionType.NONE;
    }
}

