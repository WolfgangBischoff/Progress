package Core.ActorSystem;

public enum SystemStatus
{
    ON, OFF;

    public static SystemStatus getOff(String value)
    {
        switch (value.toLowerCase())
        {
            case "on": return ON;
            case "off": return OFF;
            default:throw new RuntimeException("SystemStatus unknown: " + value);
        }
    }
}
