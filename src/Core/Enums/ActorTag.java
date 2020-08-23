package Core.Enums;

public enum ActorTag
{
    AUTOMATED_DOOR, AUTOMATED_DOOR_DETECTABLE,
    BECOME_TRANSPARENT, DETECTS_PLAYER, PLAYER,
    PERSISTENT //is loaded on the next day from LevelState
    ;

    public static ActorTag getType(String type)
    {
        switch (type.toLowerCase())
        {
            case "automated_door_relevant": return AUTOMATED_DOOR_DETECTABLE;
            case "automated_door": return AUTOMATED_DOOR;
            case "become_transparent": return BECOME_TRANSPARENT;
            case "detects_player": return DETECTS_PLAYER;
            case "player": return PLAYER;
            case "persistent": return PERSISTENT;
            default: throw new RuntimeException("ActorType unknown: " + type);
        }
    }
}
