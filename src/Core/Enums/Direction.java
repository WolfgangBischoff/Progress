package Core.Enums;

public enum Direction
{
    NORTH(0), EAST(1), SOUTH(2), WEST(3), UNDEFINED(4);

    Integer value;
    Direction(Integer value)
    {
        this.value = value;
    }

    public static Direction getDirectionFromValue(String value)
    {
        switch (value.toLowerCase())
        {
            case "north": return NORTH;
            case "east": return EAST;
            case "south": return SOUTH;
            case "west": return WEST;
            case "undefined": return UNDEFINED;
            default: throw new RuntimeException("Direction not defined: " + value);
        }
    }
}
