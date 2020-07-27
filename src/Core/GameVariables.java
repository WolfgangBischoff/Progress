package Core;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class GameVariables
{
    private static String CLASSNAME = "GameVariables/";
    private static GameVariables singleton;
    static private int playerMaM = 0;//ManagementAttentionMeter
    static private int playerMaMNextDay = 0;
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

        if(debug)
            System.out.println(CLASSNAME + methodName + "Saved: " + levelState);
    }

    public static LevelState loadLevelState(String levelName)
    {
        return levelData.get(levelName);
    }

    public static int getPlayerMaM()
    {
        return playerMaM;
    }

    public static void addPlayerManagementAttention(int deltaMAM)
    {
        String methodName = "addPlayerManagementAttention(int) ";
        playerMaMNextDay += deltaMAM;
    }


    public static int getDay()
    {
        return day;
    }

    public static void incrementDay()
    {
        String methodName = "incrementDay()";
        playerMaM = playerMaMNextDay;
        if (playerMaM > 0)
            playerMaM -= 2;
        day++;
        playerMaMNextDay = playerMaM;
    }
}
