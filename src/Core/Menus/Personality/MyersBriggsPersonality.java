package Core.Menus.Personality;

import static Core.Menus.Personality.PersonalityTrait.*;

//https://www.16personalities.com/de/personlichkeitstypen
public enum MyersBriggsPersonality
{
    //INTROVERSION,EXTROVERSION,SENSING,INTUITION,THINKING,FEELING,JUDGING ,PERCEIVING;
    //Analysts
    ARCHITECT(INTROVERSION, INTUITION, THINKING, JUDGING)
    ,LOGICIAN(INTROVERSION, INTUITION, THINKING, PERCEIVING)
    ,COMMANDER(EXTROVERSION, INTUITION, THINKING, JUDGING)
    ,DEBATER(EXTROVERSION, INTUITION, THINKING, PERCEIVING)
    //Diplomats
    ,ADVOCATE(INTROVERSION, INTUITION, FEELING, JUDGING)
    ,MEDIATOR(INTROVERSION, INTUITION, FEELING, PERCEIVING)
    ,PROTAGONIST(EXTROVERSION, INTUITION, FEELING, JUDGING)
    ,CAMPAIGNER(EXTROVERSION, INTUITION, FEELING, PERCEIVING)
    //Sentinels
    ,LOGISTICIAN(INTROVERSION, SENSING, THINKING, JUDGING)
    ,DEFENDER(INTROVERSION, SENSING, FEELING, JUDGING)
    ,EXECUTIVE(EXTROVERSION, SENSING, THINKING, JUDGING)
    ,CONSUL(EXTROVERSION, SENSING, FEELING, JUDGING)
    //Explorers
    ,VIRTUOSO(INTROVERSION, SENSING, THINKING, PERCEIVING)
    ,ADVENTURER(INTROVERSION, SENSING, FEELING, PERCEIVING)
    ,ENTREPRENEUR(EXTROVERSION, SENSING, THINKING, PERCEIVING)
    ,ENTERTAINER(EXTROVERSION, SENSING, FEELING, PERCEIVING);

    PersonalityTrait motivation; //self/social - Introversion/Extroversion
    PersonalityTrait focus; //  detail/holistic - Sensing/iNtuition
    PersonalityTrait decision; //  logic/emotion - Thinking/Feeling
    PersonalityTrait lifestyle; // conservative/progressive - Judging/Perceiving

    MyersBriggsPersonality(PersonalityTrait motivation, PersonalityTrait focus, PersonalityTrait decision, PersonalityTrait lifestyle)
    {
        this.motivation = motivation;
        this.focus = focus;
        this.decision = decision;
        this.lifestyle = lifestyle;
    }

    public static MyersBriggsPersonality getPersonality(String name)
    {
        switch (name.toLowerCase())
        {
            case "architect": return ARCHITECT;
            case "logician": return LOGICIAN;
            case "commander": return COMMANDER;
            case "debater": return DEBATER;
            case "advocate": return ADVOCATE;
            case "mediator": return MEDIATOR;
            case "protagonist": return PROTAGONIST;
            case "campaigner": return CAMPAIGNER;
            case "logistician": return LOGISTICIAN;
            case "defender": return DEFENDER;
            case "executive": return EXECUTIVE;
            case "consul": return CONSUL;
            case "virtuoso": return VIRTUOSO;
            case "adventurer": return ADVENTURER;
            case "entrepreneur": return ENTREPRENEUR;
            case "entertainer": return ENTERTAINER;
            default: throw new RuntimeException("Personality unknown: " + name);
        }
    }
}
