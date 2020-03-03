package Core;

import java.util.ArrayList;
import java.util.List;

public class ActorSystem
{
    String className = "ActorSystem ";
    String id;
    private List<Actor> systemMembers = new ArrayList<>();


    public void addActor(Actor actor)
    {
        String methodName = "addActor() ";
        systemMembers.add(actor);

        //System.out.println(className + methodName + this);
    }

    public ActorSystem(String id)
    {
        this.id = id;
    }

    public void setMemberToGeneralStatus(String status)
    {
        String methodName = "setMemberToGeneralStatus) ";
        for (Actor target : systemMembers)
        {
            target.onMonitorSignal(status);
        }
    }

    public boolean areAllMembersStatusOn()
    {
        boolean allActorsStatusOn = true;
        for (Actor toCheck : systemMembers)
        {
            //System.out.println(classname + methodName + toCheck.generalStatus);
            if (!toCheck.generalStatus.equals("on")) {
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
            if (!toCheck.generalStatus.equals("on")) {
                numberOn++;
            }
        }
        return numberOn / systemMembers.size();
    }



    @Override
    public String toString()
    {
        return "ActorSystem{" +
                "id='" + id + '\'' +
                ", systemMembers=" + systemMembers.toString() +
                '}';
    }

    public List<Actor> getSystemMembers()
    {
        return systemMembers;
    }
}
