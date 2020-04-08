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
        String methodName = "addActor() ";
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

    private void setOnIfBaseActorAllOn(String actorgroup, String targetGroup)
    {
        String methodName = "setOnIfBaseActorAllOn(String, String) ";
        boolean debug = true;

        ActorSystem checkedSystem = actorSystemMap.get(actorgroup);
        ActorSystem dependentSystem = actorSystemMap.get(targetGroup);

        if(debug)
        {
            System.out.println(classname + methodName + "Checked: " + checkedSystem);
            System.out.println(classname + methodName + "Dependent: " + dependentSystem);
        }

        //Set target Actors
        if (checkedSystem.areAllMembersStatusOn())
            dependentSystem.setMemberToGeneralStatus("on");
        else
            dependentSystem.setMemberToGeneralStatus("off");

    }

}
