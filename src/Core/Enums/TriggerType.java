package Core.Enums;

public enum TriggerType
{
    NOTHING, PERSISTENT, TIMED, TEXTBOX;

    public static TriggerType getStatus(String status)
    {
        switch (status.toLowerCase())
        {
            case "nothing": return NOTHING;
            case "persistent": return PERSISTENT;
            case "timed": return TIMED;
            case "textbox": return TEXTBOX;
            default: throw new RuntimeException("TriggerType unknown: " + status);
        }
    }

}
