package Core;

public enum Status
{
    ON, OFF, DEFAULT, ANIMATION;

    public static Status getStatus(String status)
    {
        switch (status.toLowerCase())
        {
            case "on": return ON;
            case "off": return OFF;
            case "default": return DEFAULT;
            case "animation": return ANIMATION;
            default: throw new RuntimeException("Status in Actor definition unknown: " + status);
        }
    }


}
