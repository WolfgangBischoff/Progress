package Core;

public enum Direction
{
    NORTH(0), EAST(1), SOUTH(2), WEST(3);

    Integer value;
    Direction(Integer value)
    {
        this.value = value;
    }

    public static Direction getDirectionFromValue(Integer value)
    {
        switch (value)
        {
            case 0: return NORTH;
            case 1: return EAST;
            case 2: return SOUTH;
            case 3: return WEST;
            default:return null;
        }
    }
}
