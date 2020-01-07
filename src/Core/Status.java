package Core;

public enum Status
{
    ON, OFF, DEFAULT, ANIMATION, NORTH, EAST, WEST, SOUTH;

    public static Status getStatus(String status)
    {
        switch (status.toLowerCase())
        {
            case "on": return ON;
            case "off": return OFF;
            case "default": return DEFAULT;
            case "animation": return ANIMATION;
            case "north": return NORTH;
            case "east": return EAST;
            case "south": return SOUTH;
            case "west": return WEST;
            default: throw new RuntimeException("Status in Actor definition unknown: " + status);
        }
    }


}
