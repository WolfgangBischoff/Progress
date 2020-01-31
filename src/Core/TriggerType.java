package Core;

public enum TriggerType
{
    NOTHING, PERSISTENT, TIMED;

    public static TriggerType getStatus(String status)
    {
        switch (status.toLowerCase())
        {
            case "nothing": return NOTHING;
            case "persistent": return PERSISTENT;
            case "timed": return TIMED;
            default: throw new RuntimeException("TriggerType unknown: " + status);
        }
    }

}
