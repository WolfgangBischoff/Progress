package Core.Menus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalityContainer
{
    public MyersBriggsPersonality myersBriggsPersonality;
    public Integer cooperation;
    public Integer numberOfInteractions = 0;
    public Map<String, Integer> traitsThresholds = new HashMap<>();

    @Override
    public String toString()
    {
        return "PersonalityContainer{" +
                "myersBriggsPersonality=" + myersBriggsPersonality +
                ", cooperation=" + cooperation +
                ", traitsThresholds=" + traitsThresholds +
                '}';
    }

    public String getMotivation()
    {
        if(myersBriggsPersonality.motivation == MyersBriggsCharacteristic.EXTROVERSION)
            return "extroverted";
        else
            return "introverted";
    }

    public String getFocus()
    {
        if(myersBriggsPersonality.focus == MyersBriggsCharacteristic.SENSING)
            return "Big Picture";
        else
            return "Components";
    }

    public String getDecision()
    {
        if(myersBriggsPersonality.decision == MyersBriggsCharacteristic.THINKING)
            return "logic";
        else
            return "feelings";
    }

    public String getLifestyle()
    {
        if(myersBriggsPersonality.lifestyle == MyersBriggsCharacteristic.JUDGING)
            return "protecting";
        else
            return "changing";
    }
}
