package Core;

import Core.Enums.ActorConditionType;
import Core.Enums.CollectableType;

import java.util.ArrayList;
import java.util.List;

public class ActorCondition
{
    private static final String CLASSNAME = "ActorCondition-";
    String spriteStatusCondition, sensorStatusCondition, trueSpriteStatus, trueSensorStatus, falseSpriteStatus, falseSensorStatus;
    List<String> params = new ArrayList<>();
    ActorConditionType actorConditionType;

    @Override
    public String toString()
    {
        return "\nprecondition: " + spriteStatusCondition + " and " + sensorStatusCondition
                + "\nif " + actorConditionType
                + " params= " + params
                + "\n\tthen " + trueSpriteStatus + " " + trueSensorStatus
                + "\n\telse " + falseSpriteStatus + " " + falseSensorStatus
                ;
    }

    public boolean evaluate(Actor activeActor, Actor passiveActor)
    {
        switch (actorConditionType)
        {
            case HAS_ITEM:
                return hasItem(activeActor);
            default:
                throw new RuntimeException("ActorConditionType not defined: " + actorConditionType);
        }
    }

    private boolean hasItem(Actor actor)
    {
        String methodName = "hasItem() ";
        boolean debug = false;
        //Params must be in pairs of name - type
        //If at least on item fits return true
        for (int i = 0; i < params.size(); i = i + 2)
        {
            Collectible toCheck = new Collectible(params.get(i), CollectableType.getType(params.get(i + 1)), "checkitem hasItem()", 0);
            if (actor.inventory.contains(toCheck))
            {
                if (debug)
                    System.out.println(CLASSNAME + methodName + toCheck + " found in " + actor.inventory);
                return true;
            }
            else if (debug)
                System.out.println(CLASSNAME + methodName + toCheck + " not found in " + actor.inventory);
        }
        return false;
    }

    public enum WorldViewStatus
    {
        WORLD_VIEW, INVENTORY_OPEN, INVENTORY_EXCHANGE, INVENTORY_SHOP
    }
}
