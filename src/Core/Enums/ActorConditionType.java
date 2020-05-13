package Core.Enums;

public enum ActorConditionType
{
    HAS_ITEM, HAS_MONEY, GLOBAL_VARIABLE, HAS_TAG;

    public static ActorConditionType getConditionFromValue(String value)
    {
        switch (value.toLowerCase())
        {
            case "hasitem": return HAS_ITEM;
            //case "hasmoney": return HAS_MONEY;
            //case "globalvariable": return GLOBAL_VARIABLE;
            default: throw new RuntimeException("ActorConditionType not defined: " + value);
        }
    }


}
