package Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StageMonitor
{
    String classname = "StageMonitor ";
    Map<String, List<Actor>> actorGroups = new HashMap<>();
    Map<String, String> groupsTologicCodeMap = new HashMap<>(); //TODO Use ENUM
    Map<String, String> groupsToTargetGroupsMap = new HashMap<>();

    public void addActor(String groupID, Actor actor)
    {
        String methodName = "addActor() ";
        if (!actorGroups.containsKey(groupID))
            actorGroups.put(groupID, new ArrayList<>());
        List<Actor> actorgroup = actorGroups.get(groupID);
        actorgroup.add(actor);
    }

    public void notify(List<String> groupIDList)
    {
        String methodName = "notify() ";
        //Notify all groups of the notifying Actor
        for (int i = 0; i < groupIDList.size(); i++)
        {
            String groupID = groupIDList.get(i);
            String targetGroupID = groupsToTargetGroupsMap.get(groupID);
            String logicCode = groupsTologicCodeMap.get(groupID);
            List<Actor> actorgroup = actorGroups.get(groupID);
            List<Actor> targetActorGroup = actorGroups.get(targetGroupID);
            switch (logicCode)
            {
                case "none":
                    break;
                case "setOnIfBaseActorAllOn":
                    setOnIfBaseActorAllOn(actorgroup, targetActorGroup);
                    break;
                default:
                    throw new RuntimeException(classname + methodName + "logicCode not found: " + logicCode);
            }

        }


    }

    private void setOnIfBaseActorAllOn(List<Actor> actorgroup, List<Actor> targetGroup)
    {
        String methodName = "setOnIfBaseActorAllOn() ";
        //Check Condition
        boolean allActorsStatusOn = true;
        for (Actor toCheck : actorgroup)
        {
            //System.out.println(classname + methodName + toCheck.generalStatus);
            if (!toCheck.generalStatus.equals("on"))
                allActorsStatusOn = false;
        }

        //Set target Actors
        if (allActorsStatusOn)
            for (Actor target : targetGroup)
                target.onMonitorSignal("on");
        else
            for (Actor target : targetGroup)
                target.onMonitorSignal("off");

    }
}
