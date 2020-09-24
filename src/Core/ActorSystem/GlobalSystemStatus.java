package Core.ActorSystem;

import Core.Actor;

public class GlobalSystemStatus
{
    ActorGroup actorGroup;
    SystemStatus systemStatus;
    GlobalSystemStatus dependentSystem;
    GlobalSystemStatus limitingSystem;

    public GlobalSystemStatus(String systemId, SystemStatus systemStatus)
    {
        actorGroup = new ActorGroup(systemId);
        this.systemStatus = systemStatus;
    }

    public static void init()
    {
        //TODO load all Systems
    }
    /*
    We load all system at the beginning and keep them in memory
    Level can get the actors by id => globalactor; actorId
    if a actor is updateChecked() the system is updated from base
    the updates are unchecked() to avoid loops
     */

    private void addMember(Actor actor)
    {
        actorGroup.addActor(actor);

    }

    public void updateSystemChecked()
    {
        //TODO check if new status is valid

        //once status is valid update downstream
        updateDownstream();
    }

    private void updateSystemUnchecked()
    {
        //TODO update own status from members according to logic
    }

    private void updateDownstream()
    {
        updateSystemUnchecked();
        if(dependentSystem != null)
            dependentSystem.updateDownstream();
    }

    private void updateFromBase()
    {
        //Find base system and update
        if(limitingSystem != null)
        {
            limitingSystem.updateFromBase();
        }
        //once updated update yourself
        updateSystemUnchecked();
    }


    @Override
    public String toString()
    {
        return "GlobalSystemStatus{" +
                "actorGroup=" + actorGroup +
                ", systemStatus=" + systemStatus +
                '}';
    }
}
