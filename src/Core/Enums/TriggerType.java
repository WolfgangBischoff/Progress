package Core.Enums;

public enum TriggerType
{
    NOTHING, PERSISTENT, PERSISTENT_TEXT,  TIMED, TIMED_TEXT, TEXTBOX, TEXTBOX_ANALYSIS, COLLECTABLE, MOVE;

    public static TriggerType getStatus(String status)
    {
        switch (status.toLowerCase())
        {
            case "none":
            case "nothing": return NOTHING;
            case "persistent": return PERSISTENT;
            case "persistent_text": return PERSISTENT_TEXT;
            case "timed": return TIMED;
            case "timed_text": return TIMED_TEXT;
            case "textbox": return TEXTBOX;
            case "textbox_analysis": return TEXTBOX_ANALYSIS;
            case "collectable": return COLLECTABLE;
            case "move": return MOVE;
            default: throw new RuntimeException("TriggerType unknown: " + status);
        }
    }

}
