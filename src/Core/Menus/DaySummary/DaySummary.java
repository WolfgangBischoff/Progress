package Core.Menus.DaySummary;

import Core.Collectible;
import Core.GameVariables;
import Core.WorldView.WorldView;

import java.util.ArrayList;
import java.util.List;

import static Core.Config.MAM_DAILY_DECREASE;
import static Core.Config.MAM_THRESHOLD_INTERROGATION;

public class DaySummary
{
    static private final String CLASSNAME = "DaySummary ";
    private boolean hasInterrogation = false;
    private boolean isStarving = false;
    List<Collectible> foundStolenCollectibles = new ArrayList<>();

    public DaySummary()
    {
    }

    void init()
    {
        String methodName = "init() ";

        //Check interogation and MaM
        foundStolenCollectibles.clear();
        if (GameVariables.getPlayerMaM_duringDay() >= MAM_THRESHOLD_INTERROGATION)
        {
            hasInterrogation = true;
            //Check if stolen items are in the players inventory
            boolean stolenItemWasFound = false;
            List<Collectible> playerInventory = WorldView.getPlayer().getActor().getInventory().getItemsList();
            for(int i=0; i<playerInventory.size(); i++)
            {
                Collectible checked = playerInventory.get(i);
                if(GameVariables.getStolenCollectibles().contains(checked))
                {
                    stolenItemWasFound = true;
                    foundStolenCollectibles.add(checked);
                }
            }
            WorldView.getPlayer().getActor().getInventory().removeAll(foundStolenCollectibles);//take away found stolen items in the inventory

            //decrease MAM by Interrogation threshold if nothing was found
            if(!stolenItemWasFound)
                GameVariables.addPlayerMAM_duringDay(- MAM_THRESHOLD_INTERROGATION);
        }
        else hasInterrogation = false;

        //Check hunger
        if(GameVariables.getPlayerHunger() < 10)
        {
            System.out.println(CLASSNAME + methodName + "you are starving");
            GameVariables.setHealth(GameVariables.getHealth() - 1);
            isStarving = true;
        }
        else
        {
            isStarving = false;
            GameVariables.setHealth(GameVariables.getHealth() + 1);
        }


    }

    public void endDay()
    {
        String methodName = "endDay() ";
        boolean debug = false;

        //Time decreases MaM, but not below zero
        if (GameVariables.getPlayerMaM_duringDay() > MAM_DAILY_DECREASE)
            GameVariables.addPlayerMAM_duringDay(-MAM_DAILY_DECREASE);
        else if (GameVariables.getPlayerMaM_duringDay() > 0)//to get to 0 if Nam between 0 and daily decrease
            GameVariables.addPlayerMAM_duringDay(- GameVariables.getPlayerMaM_duringDay());

        //lower Hunger bar
        GameVariables.addHunger(-20);

        GameVariables.incrementDay();
    }

    public boolean isHasInterrogation()
    {
        return hasInterrogation;
    }

    public boolean isStarving()
    {
        return isStarving;
    }
}
