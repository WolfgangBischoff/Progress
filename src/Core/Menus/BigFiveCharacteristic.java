package Core.Menus;

public enum BigFiveCharacteristic
{
    Conscientiousness,Openness, Agreeableness, Neuroticism,Extroversion;

    public static BigFiveCharacteristic getType(String type)
    {
        switch (type.toLowerCase())
        {
            case "conscientiousness": return Conscientiousness;
            case "openness": return Openness;
            case "agreeableness": return Agreeableness;
            case "neuroticism": return Neuroticism;
            case "extroversion": return Extroversion;
            default: throw new RuntimeException("BigFiveCharacteristic unknown: " + type);
        }
    }

}
