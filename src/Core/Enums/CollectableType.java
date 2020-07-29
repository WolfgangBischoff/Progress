package Core.Enums;

public enum CollectableType
{
    MONEY, KEY, QUEST, FOOD, NONFUNCTION;

    public static CollectableType getType(String type)
    {
        switch (type.toLowerCase())
        {
            case "money": return MONEY;
            case "key": return KEY;
            case "quest": return QUEST;
            case "food": return FOOD;
            case "nonfunction": return NONFUNCTION;
            default: throw new RuntimeException("ItemType unknown: " + type);
        }
    }
}
