package Core;

import java.util.ArrayList;
import java.util.List;

public class ActorSystem
{
    private static final String CLASSNAME = "ActorSystem/";
    String id;
    private List<Actor> systemMembers = new ArrayList<>();


    public void addActor(Actor actor)
    {
        String methodName = "addActor() ";
        systemMembers.add(actor);
    }

    public ActorSystem(String id)
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

    public void setMemberToGeneralStatus(String ifInStatus, String newStatus)
    {
        String methodName = "setMemberToGeneralStatus(String, String)";
        for (Actor target : systemMembers)
        {
            if (target.generalStatus.toLowerCase().equals(ifInStatus.toLowerCase()))
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
            if (!toCheck.generalStatus.equals("on"))
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
            //System.out.println(classname + methodName + toCheck.generalStatus);
            if (!toCheck.generalStatus.equals("on"))
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
