package Core.Enums;

public enum ActorTag
{
    AUTOMATED_DOOR, AUTOMATED_DOOR_DETECTABLE;

    public static ActorTag getType(String type)
    {
        switch (type.toLowerCase())
        {
            case "automated_door_relevant": return AUTOMATED_DOOR_DETECTABLE;
            case "automated_door": return AUTOMATED_DOOR;
            default: throw new RuntimeException("ActorType unknown: " + type);
        }
    }
}
