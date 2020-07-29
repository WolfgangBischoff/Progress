package Core;

import java.util.HashMap;
import java.util.Map;

import static Core.Config.MAM_DAILY_DECREASE;

public class GameVariables
{
    private static String CLASSNAME = "GameVariables/";
    private static GameVariables singleton;
    static private int playerMaM_dayStart = 0;//ManagementAttentionMeter
    static private int playerMaM_duringDay = 0;
    static private int day = 0;

    //Game State
    static Sprite player;
    static Map<String, LevelState> levelData = new HashMap<>();

    private GameVariables()
    {

    }

    public static void setPlayer(Sprite player)
    {
        GameVariables.player = player;
    }

    public static void saveLevelState(LevelState levelState)
    {
        String methodName = "saveLevelState() ";
        boolean debug = false;
        levelData.put(levelState.levelName, levelState);

        if (debug)
            System.out.println(CLASSNAME + methodName + "Saved: " + levelState);
    }


    public static void addPlayerManagementAttention(int deltaMAM)
    {
        String methodName = "addPlayerManagementAttention(int) ";
        boolean debug = true;
        if (debug)
            System.out.println(CLASSNAME + methodName + "MAM: " + playerMaM_duringDay + " + " + deltaMAM + " = " + (playerMaM_duringDay + deltaMAM));
        playerMaM_duringDay += deltaMAM;
    }


    public static int getDay()
    {
        return day;
    }

    public static void incrementDay()
    {
        String methodName = "incrementDay()";
        playerMaM_dayStart = playerMaM_duringDay;
        //Time decreases MaM, but not below zero
        if (playerMaM_dayStart > MAM_DAILY_DECREASE)
            playerMaM_dayStart -= Config.MAM_DAILY_DECREASE;
        else if (playerMaM_dayStart > 0)//to get to 0 if Nam between 0 and daily decrease
            playerMaM_dayStart = 0;
        day++;
        playerMaM_duringDay = playerMaM_dayStart;
    }

    public static int getPlayerMaM_dayStart()
    {
        return playerMaM_dayStart;
    }

    public static int getPlayerMaM_duringDay()
    {
        return playerMaM_duringDay;
    }
}
