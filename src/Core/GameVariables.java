package Core;

import java.awt.*;

public class GameVariables
{
    private static String CLASS_NAME = "GameVariables/";
    private static GameVariables singleton;
    static private int playerSuspectedness = 0;
    static private int day = 0;

    private GameVariables()
    {

    }

    static GameVariables getInstance()
    {
        if(singleton == null)
            singleton = new GameVariables();
        return singleton;
    }

    public static int getPlayerSuspectedness()
    {
        return playerSuspectedness;
    }

    public static void setPlayerSuspectedness(int playerSuspectedness)
    {
        String methodName = "setPlayerSuspectedness(int) ";
        GameVariables.playerSuspectedness = playerSuspectedness;
        System.out.println(CLASS_NAME + methodName + playerSuspectedness);
    }

    public static int getDay()
    {
        return day;
    }

    public static void incrementDay()
    {
        day++;
    }
}
