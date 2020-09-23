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
    /*
    A System must be initialized with a status
    Actors that are created within the actor system are evaluated against this status. Not the other way round, because we dont know if there are other unloaded actors of this group
    After addition dependent or limiting systems are refreshed
     */

    public void addMember(Actor actor)
    {
        actorGroup.addActor(actor);
        //TODO check if state is valid and correct

        //TODO refresh dependence chain from beginning up
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
