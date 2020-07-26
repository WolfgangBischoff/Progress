package Core;

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
        if (singleton == null)
            singleton = new GameVariables();
        return singleton;
    }

    public static int getPlayerSuspectedness()
    {
        return playerSuspectedness;
    }

    private static void setPlayerSuspectedness(int playerSuspectedness)
    {
        String methodName = "setPlayerSuspectedness(int) ";
        if (playerSuspectedness > 0)
            GameVariables.playerSuspectedness = playerSuspectedness;
        //System.out.println(CLASS_NAME + methodName + playerSuspectedness);
    }

    public static void addPlayerSuspectedness(int deltaSuspectioness)
    {
        String methodName = "addPlayerSuspectedness(int) ";
        setPlayerSuspectedness(GameVariables.playerSuspectedness + deltaSuspectioness);
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
