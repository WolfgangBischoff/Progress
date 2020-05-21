package Core.Menus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalityContainer
{
    public MyersBriggsPersonality myersBriggsPersonality;
    public Integer cooperation;
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
}
