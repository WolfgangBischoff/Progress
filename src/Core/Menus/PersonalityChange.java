package Core.Menus;

import Core.Actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.Menus.PersonalityTrait.*;

public class PersonalityChange
{
    private static final String CLASSNAME = "PersonalityChange-";
    List<String> topicList;
    List<String> opponentTraitsList;
    private MyersBriggsPersonality opponentPersonality;
    int totalPoints;
    Actor opponent;


    public PersonalityChange(MyersBriggsPersonality personality)
    {
        init();
        //opponent = null; //TODO from controller
        this.opponentPersonality = personality;
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

        Map<PersonalityTrait, Integer> values = getPersonalityValuesFromArgument(argument);
        //System.out.println(CLASSNAME + methodName + argument + " " + values);

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

    private Map<PersonalityTrait, Integer> getPersonalityValuesFromArgument(String argument)
    {
        Map<PersonalityTrait, Integer> argumentToValues = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> dimensionToArgument : PersonalityScreenController.argumentsTraitsMatrix.entrySet())
        {
            PersonalityTrait dimension = PersonalityTrait.getType(dimensionToArgument.getKey());
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
