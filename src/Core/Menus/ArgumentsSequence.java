package Core.Menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.Menus.BigFiveCharacteristic.*;

public class ArgumentsSequence
{
    /*
    1) Check against stable personality (Big Five)
    2) Check against character specific traits
    3) Check against random emotion

    Overall:
        sequence, abwechseln zwischen argument type gut
        combos; logic + compliment works good

    Modi:
    Angriff auf Meinung des anderen
    Defense, sonst restriction
    Nach außen relevant, unwichtig was der andere denkt, aber die Öffentlichkeit

    Dimenional Traits:
    Myers-Briggs-Typenindikator
    BigFive

    Emotions Random/Skriiptet
     Ärger, Furcht, Traurigkeit, Überraschung, Ekel und Freude Stolz
     */

    private static final String CLASSNAME = "ArgumentsSequence-";
    List<String> argumentsList;
    List<String> opponentTraitsList;
    private Personality opponentPersonality;
    int totalPoints;

    public ArgumentsSequence()
    {
        init();

        opponentPersonality = new Personality();
        opponentPersonality.Agreeablness = 1;
        opponentPersonality.conscientiousness = 1;
        opponentPersonality.Extroversion = 1;
        opponentPersonality.Neuroticism = 1;
        opponentPersonality.Openness = 1;
    }

    private void init()
    {
        argumentsList = new ArrayList<>();
        opponentTraitsList = new ArrayList<>();
        totalPoints = 0;
    }

    public void addArgument(String argument)
    {
        String methodName = "addArgument() ";
        argumentsList.add(argument);
        //totalPoints += evaluateFit_Trait(argument, opponentTraitsList);
        totalPoints += evaluatePersonalityFit(argument, opponentPersonality);
        //System.out.println(CLASSNAME + methodName + totalPoints);
    }

    private int evaluatePersonalityFit(String argument, Personality personalityValues)
    {
        String methodName = "evaluatePersonalityFit() ";
        int evaluationTotal = 0;

        //TODO check if all personality values are 0 => total would be always 0, add 1

        Map<BigFiveCharacteristic, Integer> values = getPersonalityValuesFromArgument(argument);
        System.out.println(CLASSNAME + methodName + argument + " " + values);
        evaluationTotal += personalityValues.Openness * values.get(Openness);
        evaluationTotal += personalityValues.Agreeablness * values.get(Agreeableness);
        evaluationTotal += personalityValues.conscientiousness * values.get(Conscientiousness);
        evaluationTotal += personalityValues.Extroversion * values.get(Extroversion);
        evaluationTotal += personalityValues.Neuroticism * values.get(Neuroticism);
        System.out.println(CLASSNAME + methodName + "Added Value from " + argument + " " + evaluationTotal);
        return evaluationTotal;
    }

    private Map<BigFiveCharacteristic, Integer> getPersonalityValuesFromArgument(String argument)
    {
        Map<BigFiveCharacteristic, Integer> argumentToValues = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> dimensionToArgument : DiscussionController.argumentsTraitsMatrix.entrySet())
        {
            BigFiveCharacteristic dimension = BigFiveCharacteristic.getType(dimensionToArgument.getKey());
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

        //Map<String, Integer> argumentsValues = DiscussionController.argumentsTraitsMatrix.get(argument);
        for (String trait : evaluationTraits)
        {
            //Integer value = argumentsValues.get(trait);
            //evaluationTotal += value;
        }
        return evaluationTotal;
    }

}
