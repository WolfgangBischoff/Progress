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

    Map<String, ActorSystem> actorSystemMap = new HashMap<>();

    public void addActor(String groupID, Actor actor)
    {
        //TODO remove old logic
        String methodName = "addActor() ";

        /*
        if (!actorGroups.containsKey(groupID))
            actorGroups.put(groupID, new ArrayList<>());
        List<Actor> actorgroup = actorGroups.get(groupID);
        actorgroup.add(actor);
        */
        if (!actorSystemMap.containsKey(groupID))
            actorSystemMap.put(groupID, new ActorSystem(groupID));
        ActorSystem actorSystem = actorSystemMap.get(groupID);
        actorSystem.addActor(actor);

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
            switch (logicCode)
            {
                case "none":
                    break;
                case "setOnIfBaseActorAllOn":
                    setOnIfBaseActorAllOn(groupID, targetGroupID);
                    break;
                default:
                    throw new RuntimeException(classname + methodName + "logicCode not found: " + logicCode);
            }
        }
    }

    //TODO move to ActorSystem
    /*
    private void setOnIfBaseActorAllOn(List<Actor> actorgroup, List<Actor> targetGroup)
    {
        String methodName = "setOnIfBaseActorAllOn() ";
        //Check Condition
        boolean allActorsStatusOn = true;
        for (Actor toCheck : actorgroup)
        {
            if (!toCheck.generalStatus.equals("on")) {
                allActorsStatusOn = false;
                break;
            }
        }

        //Set target Actors
        if (allActorsStatusOn)
            for (Actor target : targetGroup)
                target.onMonitorSignal("on");
        else
            for (Actor target : targetGroup)
                target.onMonitorSignal("off");

    }*/

    private void setOnIfBaseActorAllOn(String actorgroup, String targetGroup)
    {
        String methodName = "setOnIfBaseActorAllOn(String, String) ";
        ActorSystem checkedSystem = actorSystemMap.get(actorgroup);
        ActorSystem dependentSystem = actorSystemMap.get(targetGroup);

        //Set target Actors
        if (checkedSystem.areAllMembersStatusOn())
            dependentSystem.setMemberToGeneralStatus("on");
        else
            dependentSystem.setMemberToGeneralStatus("off");

    }
}
