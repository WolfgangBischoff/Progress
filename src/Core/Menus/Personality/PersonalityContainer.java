package Core.Menus.Personality;

import java.util.HashMap;
import java.util.Map;

import static Core.Menus.Personality.PersonalityTrait.*;

public class PersonalityContainer
{
    private static final String CLASSNAME = "PersonalityContainer ";
    public MyersBriggsPersonality myersBriggsPersonality;
    private Integer cooperation = 0;
    private Integer numberOfInteractions = 0;
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

    public PersonalityTrait getMotivation()
    {
        if(myersBriggsPersonality.motivation == EXTROVERSION)
            return EXTROVERSION;
        else
            return INTROVERSION;
    }

    public PersonalityTrait getFocus()
    {
        if(myersBriggsPersonality.focus == PersonalityTrait.SENSING)
            return SENSING;
        else
            return INTUITION;
    }

    public PersonalityTrait getDecision()
    {
        if(myersBriggsPersonality.decision == PersonalityTrait.THINKING)
            return THINKING;
        else
            return FEELING;
    }

    public PersonalityTrait getLifestyle()
    {
        if(myersBriggsPersonality.lifestyle == PersonalityTrait.JUDGING)
            return JUDGING;
        else
            return PERCEIVING;
    }

    public void increaseCooperation(Integer addition)
    {
        String methodName = "increaseCooperation() ";
        this.cooperation += addition;
        //System.out.println(CLASSNAME + methodName + "added " + addition + " to " + cooperation);
    }

    public void incrementNumberOfInteraction()
    {
        numberOfInteractions++;
        increaseCooperation(1);
    }

    public Integer getNumberOfInteractions()
    {
        return numberOfInteractions;
    }

    public Integer getCooperation()
    {
        return cooperation;
    }

    public Map<String, Integer> getTraitsThresholds()
    {
        return traitsThresholds;
    }
}
