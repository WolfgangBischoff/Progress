package Core.Menus;

public enum MyersBriggsCharacteristic
{
    //self  	;social		;detail	  ;holistic	;logic	;emotion;conservative progressive ;
    INTROVERSION,EXTROVERSION,SENSING,INTUITION,THINKING,FEELING,JUDGING,    PERCEIVING;

    public static MyersBriggsCharacteristic getType(String type)
    {
        switch (type.toLowerCase())
        {
            case "introversion": return INTROVERSION;
            case "extroversion": return EXTROVERSION;
            case "sensing": return SENSING;
            case "intuition": return INTUITION;
            case "thinking": return THINKING;
            case "feeling": return FEELING;
            case "judging": return JUDGING;
            case "perceiving": return PERCEIVING;
            default: throw new RuntimeException("MyersBriggsCharacteristic unknown: " + type);
        }
    }

}
