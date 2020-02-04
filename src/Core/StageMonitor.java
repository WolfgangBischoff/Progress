package Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StageMonitor
{
    String classname = "StageMonitor ";
    Map<String, List<Actor>> actorGroups = new HashMap<>();

    public void addActor(String groupID, Actor actor)
    {
        if (!actorGroups.containsKey(groupID))
            actorGroups.put(groupID, new ArrayList<>());
        List<Actor> actorgroup = actorGroups.get(groupID);
        actorgroup.add(actor);
    }

    public void notify(String groupID, Actor actor)
    {
        String methodName = "notify() ";

        if(groupID.equals("screen"))
            return;

        //TODO Update group
        List<Actor> actorgroup = actorGroups.get(groupID);
        //System.out.println(classname + methodName + actorgroup.toString());
        boolean allon = true;
        for (Actor a : actorgroup)
            if (!a.compoundStatus.equals("on"))
                allon = false;


        if (allon)
            signal();


    }

    public void signal()
    {
        String methodName = "signal() ";

        //TODO Trigger from WorldConfig
        List<Actor> actorgroup2 = actorGroups.get("screen");
        System.out.println(classname + methodName + actorgroup2.toString());
        actorgroup2.get(0).onMonitorSignal();
    }
}
