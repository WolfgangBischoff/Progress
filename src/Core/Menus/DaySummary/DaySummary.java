package Core.Menus.DaySummary;

import Core.GameVariables;
import Core.WorldView;

import static Core.Config.MAM_DAILY_DECREASE;
import static Core.Config.MAM_THRESHOLD_INTERROGATION;

public class DaySummary
{
    static private final String CLASSNAME = "DaySummary ";
    private boolean hasInterrogation = false;

    public DaySummary()
    {
        init();
    }

    private void init()
    {
        if (GameVariables.getPlayerMaM_duringDay() >= MAM_THRESHOLD_INTERROGATION)
        {
            hasInterrogation = true;
        }
    }

    public void endDay()
    {
        String methodName = "endDay() ";
        boolean debug = false;

        if (hasInterrogation)
        {
            //decrease MAM by Interrogation threshold
            if(debug)
                System.out.println(CLASSNAME + methodName + "Interrogation");
            GameVariables.addPlayerMAM_duringDay(- MAM_THRESHOLD_INTERROGATION);
            if(GameVariables.getPlayerMaM_duringDay() >= MAM_THRESHOLD_INTERROGATION)
            {
                if(debug)
                    System.out.println(CLASSNAME + methodName + "removed additional: " + (GameVariables.getPlayerMaM_duringDay() - MAM_THRESHOLD_INTERROGATION));
                GameVariables.addPlayerMAM_duringDay(- (GameVariables.getPlayerMaM_duringDay() - MAM_THRESHOLD_INTERROGATION));
            }

            //take away found stolen items
            WorldView.getPlayer().getActor().getInventory().removeAll(GameVariables.getStolenCollectibles());
            GameVariables.getStolenCollectibles().clear();
        }

        //Time decreases MaM, but not below zero
        if (GameVariables.getPlayerMaM_duringDay() > MAM_DAILY_DECREASE)
            GameVariables.addPlayerMAM_duringDay(-MAM_DAILY_DECREASE);
        else if (GameVariables.getPlayerMaM_duringDay() > 0)//to get to 0 if Nam between 0 and daily decrease
            GameVariables.addPlayerMAM_duringDay(- GameVariables.getPlayerMaM_duringDay());

        GameVariables.incrementDay();
    }

    public boolean isHasInterrogation()
    {
        return hasInterrogation;
    }
}
