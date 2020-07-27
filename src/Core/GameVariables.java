package Core;

public class GameVariables
{
    private static String CLASS_NAME = "GameVariables/";
    private static GameVariables singleton;
    static private int playerSuspicion = 0;
    static private int playerSuspicionNextDay = 0;
    static private int day = 0;

    private GameVariables()
    {

    }

    static GameVariables getInstance()
    {
        if (singleton == null)
            singleton = new GameVariables();
        return singleton;
    }

    public static int getPlayerSuspicion()
    {
        return playerSuspicion;
    }

    private static void setPlayerSuspicion(int playerSuspicion)
    {
        String methodName = "setPlayerSuspectedness(int) ";
        if (playerSuspicion > 0)
            GameVariables.playerSuspicion = playerSuspicion;
        //System.out.println(CLASS_NAME + methodName + playerSuspectedness);
    }

    public static void addPlayerSuspectedness(int deltaSuspectioness)
    {
        String methodName = "addPlayerSuspectedness(int) ";
        setPlayerSuspicion(GameVariables.playerSuspicion + deltaSuspectioness);
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
