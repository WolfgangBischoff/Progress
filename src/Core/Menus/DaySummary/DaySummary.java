package Core.Menus.DaySummary;

import Core.GameVariables;
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
            int interrogation_decrease = GameVariables.getPlayerMaM_duringDay() - MAM_THRESHOLD_INTERROGATION;
            GameVariables.addPlayerManagementAttention(- interrogation_decrease);
        }
    }

    public boolean isHasInterrogation()
    {
        return hasInterrogation;
    }
}
