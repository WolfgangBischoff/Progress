package Core.Menus;

import Core.Actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.Menus.MyersBriggsCharacteristic.*;

public class PersonalityChange
{
    /*
    Person has a cooperation value, that rises by small talk and other events.
    At the beginning you get known by small talk with a generic action set evaluated by personality. (and maybe emotions)
    The small talk set can be extended over time.
    With increasing cooperation value you find trais of the person, some traits are difficult to get, or just achievable by external world.
    The "Traits" menu show special actions like "Join party", Sabatoage, Give info

     */

    private static final String CLASSNAME = "PersonalityChange-";
    List<String> topicList;
    List<String> opponentTraitsList;
    private MyersBriggsPersonality opponentPersonality;
    int totalPoints;
    Actor opponent;


    public PersonalityChange()
    {
        init();
        opponent = null; //TODO from controller
        this.opponentPersonality = MyersBriggsPersonality.ARCHITECT;
        totalPoints = 0;
        opponentTraitsList = new ArrayList<>();
    }

    private void init()
    {
        topicList = new ArrayList<>();
    }

    public void addArgument(String argument)
    {
        String methodName = "addArgument() ";
        topicList.add(argument);
        totalPoints += evaluatePersonalityFit(argument, opponentPersonality);
    }

    private int evaluatePersonalityFit(String argument, MyersBriggsPersonality personalityValues)
    {
        String methodName = "evaluatePersonalityFit() ";
        int evaluationTotal = 0;

        Map<MyersBriggsCharacteristic, Integer> values = getPersonalityValuesFromArgument(argument);
        System.out.println(CLASSNAME + methodName + argument + " " + values);

        //INTROVERSION,EXTROVERSION,SENSING,INTUITION,THINKING,FEELING,JUDGING ,PERCEIVING;
        if(personalityValues.motivation == INTROVERSION)
            evaluationTotal += values.get(INTROVERSION);
        else
            evaluationTotal += values.get(EXTROVERSION);

        if(personalityValues.focus == SENSING)
            evaluationTotal += values.get(SENSING);
        else
            evaluationTotal += values.get(INTUITION);

        if(personalityValues.decision == THINKING)
            evaluationTotal += values.get(THINKING);
        else
            evaluationTotal += values.get(FEELING);

        if(personalityValues.lifestyle == JUDGING)
            evaluationTotal += values.get(JUDGING);
        else
            evaluationTotal += values.get(PERCEIVING);

        System.out.println(CLASSNAME + methodName + "Added Value from " + argument + " " + evaluationTotal);
        return evaluationTotal;
    }

    private Map<MyersBriggsCharacteristic, Integer> getPersonalityValuesFromArgument(String argument)
    {
        Map<MyersBriggsCharacteristic, Integer> argumentToValues = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> dimensionToArgument : PersonalityScreenController.argumentsTraitsMatrix.entrySet())
        {
            MyersBriggsCharacteristic dimension = MyersBriggsCharacteristic.getType(dimensionToArgument.getKey());
            Map<String, Integer> dimensionValues = dimensionToArgument.getValue();
            if (!dimensionValues.containsKey(argument))
                System.out.println(CLASSNAME + "Argument not known: " + argument);
            argumentToValues.put(dimension, dimensionValues.get(argument));
        }
        return argumentToValues;
    }

    private int evaluateFit_Trait(String argument, List<String> evaluationTraits)
    {
        String methodName = "evaluateFit_Trait() ";
        boolean debug = true;
        int evaluationTotal = 0;

        //Map<String, Integer> argumentsValues = PersonalityScreenController.argumentsTraitsMatrix.get(argument);
        for (String trait : evaluationTraits)
        {
            //Integer value = argumentsValues.get(trait);
            //evaluationTotal += value;
        }
        return evaluationTotal;
    }

}
