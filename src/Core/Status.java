package Core;

public enum Status
{
    ON, OFF, DEFAULT, ANIMATION, NORTH, NORTHMOVING, EAST, EASTMOVING, WEST,WESTMOVING, SOUTH, SOUTHMOVING;

    public static Status getStatus(String status)
    {
        switch (status.toLowerCase())
        {
            case "on": return ON;
            case "off": return OFF;
            case "default": return DEFAULT;
            case "animation": return ANIMATION;
            case "north": return NORTH;
            case "northmoving": return NORTHMOVING;
            case "east": return EAST;
            case "eastmoving": return EASTMOVING;
            case "south": return SOUTH;
            case "southmoving": return SOUTHMOVING;
            case "west": return WEST;
            case "westmoving": return WESTMOVING;
            default: throw new RuntimeException("Status in Actor definition unknown: " + status);
        }
    }


}
