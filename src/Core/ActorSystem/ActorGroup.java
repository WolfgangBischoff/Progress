package Core.ActorSystem;

import Core.Actor;

import java.util.ArrayList;
import java.util.List;

public class ActorGroup
{
    private static final String CLASSNAME = "ActorSystem ";
    String id;
    private List<Actor> systemMembers = new ArrayList<>();


    public void addActor(Actor actor)
    {
        String methodName = "addActor() ";
        systemMembers.add(actor);
    }

    public ActorGroup(String id)
    {
        this.id = id;
    }

    public void setMemberToGeneralStatus(String newStatus)
    {
        String methodName = "setMemberToGeneralStatus(String) ";
        for (Actor target : systemMembers)
        {
            target.onMonitorSignal(newStatus);
        }
    }

    public void setMemberToSensorStatus(String sensorStatus)
    {
        String methodName = "setMemberToSensorStatus(String) ";
        for (Actor target : systemMembers)
        {
            target.setSensorStatus(sensorStatus);
        }
    }

    public void setMemberToGeneralStatus(String ifInStatus, String newStatus)
    {
        String methodName = "setMemberToGeneralStatus(String, String)";
        for (Actor target : systemMembers)
        {
            if (target.getGeneralStatus().toLowerCase().equals(ifInStatus.toLowerCase()))
            {
                System.out.println(CLASSNAME + methodName + " " + ifInStatus + " set to " + newStatus);
                target.onMonitorSignal(newStatus);
            }
        }
    }

    public Boolean areAllMembersStatusOn()
    {
        boolean allActorsStatusOn = true;
        for (Actor toCheck : systemMembers)
        {
            if (!toCheck.getGeneralStatus().equals("on"))
            {
                allActorsStatusOn = false;
                break;
            }
        }
        return allActorsStatusOn;
    }

    public float calcPercentageOn()
    {
        float numberOn = 0f;
        for (Actor toCheck : systemMembers)
        {
            if (!toCheck.getGeneralStatus().equals("on"))
            {
                numberOn++;
            }
        }
        return numberOn / systemMembers.size();
    }


    @Override
    public String toString()
    {
        return id + " member: " + systemMembers.toString();
    }

    public List<Actor> getSystemMembers()
    {
        return systemMembers;
    }
}
