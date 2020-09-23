package Core;

import Core.ActorSystem.ActorGroup;
import Core.WorldView.WorldView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StageMonitor
{
    private final static String CLASSNAME = "StageMonitor/";
    Map<String, String> groupToLogicMap = new HashMap<>(); //TODO Use ENUM
    Map<String, String> groupIdToInfluencedGroupIdMap = new HashMap<>();
    Map<String, ActorGroup> groupIdToActorGroupMap = new HashMap<>();

    public void addActorToActorSystem(String actorSystemId, Actor actor)
    {
        boolean debug = false;
        String methodName = "addActorToActorSystem() ";
        if (!groupIdToActorGroupMap.containsKey(actorSystemId))
            groupIdToActorGroupMap.put(actorSystemId, new ActorGroup(actorSystemId));
        ActorGroup actorGroup = groupIdToActorGroupMap.get(actorSystemId);
        actorGroup.addActor(actor);

        if (debug)
            System.out.println(CLASSNAME + methodName + " added " + actor.actorInGameName + " to " + actorGroup);
    }

    public String isDependentOnGroup(List<String> checkedGroupId)
    {
        for (Map.Entry<String, String> entry : groupIdToInfluencedGroupIdMap.entrySet())
            if (checkedGroupId.contains(entry.getValue()))//actor can be in multiple groups but just be influenced by one
                return entry.getKey();
        return null;
    }


    public void sendSignalFrom(List<String> notifyingGroupsList)
    {
        String methodName = "sendSignalFrom(List<String>)";
        //Notify all groups of the notifying Actor
        for (int i = 0; i < notifyingGroupsList.size(); i++)
        {
            sendSignalFrom(notifyingGroupsList.get(i));
        }
    }

    public void sendSignalFrom(String notifyingGroup)
    {
        String methodName = "sendSignalFrom(String)";
        boolean debug = false;
        String targetGroupID = groupIdToInfluencedGroupIdMap.get(notifyingGroup);
        String logicCode = groupToLogicMap.get(notifyingGroup);

        if (debug)
            System.out.println(CLASSNAME + methodName + " " + notifyingGroup + " used " + logicCode + " on " + targetGroupID);
        switch (logicCode)
        {
            case "none":
                break;
            case "isBaseSystem":
                apply_baseSystemLogic(notifyingGroup, targetGroupID);
                break;
            case "allOn_default/locked":
                allOn_setSensorStatus(notifyingGroup, targetGroupID, "default", "locked");
                break;
            case "always_sensorDefault_spriteOn":
                always_sensorStatus(notifyingGroup, targetGroupID, "default");
                always_spriteStatus(notifyingGroup, targetGroupID, "on");
                break;
            case "levelchange":
                changeLevel(notifyingGroup, targetGroupID);
                break;
            case "transitionOnChange":
                transitionOnChange(notifyingGroup, targetGroupID);
                break;
            default:
                throw new RuntimeException(CLASSNAME + methodName + "logicCode not found: " + logicCode);
        }
    }

    private void transitionOnChange(String notifyingGroup, String targetGroupID)
    {
        String methodName = "triggerOnChange()";
        ActorGroup notifier = groupIdToActorGroupMap.get(notifyingGroup);
        ActorGroup signaled = groupIdToActorGroupMap.get(targetGroupID);

        signaled.setMemberToGeneralStatus("transition");
    }

    private void changeLevel(String filename_level, String spawnId)
    {
        String methodName = "changeLevel(String)";
        System.out.println(CLASSNAME + methodName + "loaded: " + filename_level + " spawn at " + spawnId);
        WorldView.getSingleton().saveStage();
        WorldView.getSingleton().loadStage(filename_level, spawnId);
    }

    private void always_sensorStatus(String notifyingGroup, String targetGroupID, String sensorStatus)
    {
        String methodName = "always_sensorStatus(String, String, String) ";
        boolean debug = false;

        ActorGroup notifier = groupIdToActorGroupMap.get(notifyingGroup);
        ActorGroup signaled = groupIdToActorGroupMap.get(targetGroupID);

        if (debug)
            System.out.println(CLASSNAME + methodName + notifier.areAllMembersStatusOn() + notifier);
        signaled.setMemberToSensorStatus(sensorStatus);
    }

    private void always_spriteStatus(String notifyingGroup, String targetGroupID, String spriteStatus)
    {
        String methodName = "always_sensorStatus(String, String, String) ";
        boolean debug = false;

        ActorGroup notifier = groupIdToActorGroupMap.get(notifyingGroup);
        ActorGroup signaled = groupIdToActorGroupMap.get(targetGroupID);

        signaled.setMemberToGeneralStatus(spriteStatus);
    }

    private void allOn_setSensorStatus(String notifyingGroup, String targetGroupID, String trueStatus, String falseStatus)
    {
        String methodName = "allOn_setSensorStatus(String, String, String, String) ";
        boolean debug = false;

        ActorGroup notifier = groupIdToActorGroupMap.get(notifyingGroup);
        ActorGroup signaled = groupIdToActorGroupMap.get(targetGroupID);

        if (notifier.areAllMembersStatusOn())
            signaled.setMemberToSensorStatus(trueStatus);
        else
            signaled.setMemberToSensorStatus(falseStatus);

    }

    private void apply_baseSystemLogic(String checkedGroup, String dependentGroup)
    {
        String methodName = "set_baseOff_IfBaseOffline() ";

        ActorGroup checkedSystem = groupIdToActorGroupMap.get(checkedGroup);
        ActorGroup dependentSystem = groupIdToActorGroupMap.get(dependentGroup);
        String influencingSystemStatus = checkedSystem.areAllMembersStatusOn().toString();

        for (Actor influenced : dependentSystem.getSystemMembers())
        {
            String status = influenced.generalStatus;
            String statusConsideringLogic = statusTransition_baseSystemLogic(influencingSystemStatus, status);
            influenced.onMonitorSignal(statusConsideringLogic);
        }
    }

    private String statusTransition_baseSystemLogic(String influencingGroupStatus, String influencedActorStatus)
    {
        String baseSystemOfflineString = "basesystemoffline";
        switch (influencingGroupStatus.toLowerCase())
        {
            case "true":
                if (influencedActorStatus.equals(baseSystemOfflineString))
                    return "on";
                else
                    return influencedActorStatus;

            case "false":
                if (influencedActorStatus.equals("on"))
                    return baseSystemOfflineString;
                else
                    return influencedActorStatus;

            default:
                throw new RuntimeException("statusTransition not defined of status: " + influencingGroupStatus.toLowerCase());
        }
    }

    //for Actors to double check their status changes
    public String checkIfStatusIsValid(String influencedStatus, String influencingSystemId)
    {
        String methodName = "checkIfStatusIsValid(String, String)";
        ActorGroup influencingSystem = groupIdToActorGroupMap.get(influencingSystemId);
        String logic = groupToLogicMap.get(influencingSystemId);

        if (logic.equals("isBaseSystem"))
        {
            String influencingSystemStatus = influencingSystem.areAllMembersStatusOn().toString();
            return statusTransition_baseSystemLogic(influencingSystemStatus, influencedStatus);
        }

        //System.out.println(CLASSNAME + methodName + " Logic not found for " + influencedStatus + " influenced of " + influencingSystemId);
        return influencedStatus;
    }

}
