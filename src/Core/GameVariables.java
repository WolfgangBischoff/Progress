package Core;

public class GameVariables
{
    private static String CLASSNAME = "GameVariables/";
    private static GameVariables singleton;
    static private int playerMaM = 0;//ManagementAttentionMeter
    static private int playerMaMNextDay = 0;
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
        if(playerMaM > 0)
            playerMaM -= 2;
        day++;
        playerMaMNextDay = playerMaM;
    }
}
