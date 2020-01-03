package Core;

public enum Status
{
    ON, OFF;

    public static Status getStatus(String status)
    {
        switch (status.toLowerCase())
        {
            case "on": return ON;
            case "off": return OFF;
            default: return ON;
        }
    }


}
