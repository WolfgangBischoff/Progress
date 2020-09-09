package Core;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameVariables
{
    private static String CLASSNAME = "GameVariables ";
    private static GameVariables singleton;
    static private IntegerProperty playerMoney = new SimpleIntegerProperty(20);
    static IntegerProperty playerMaM_duringDay = new SimpleIntegerProperty();
    static private int playerMaM_dayStart = 0;//ManagementAttentionMeter
    static private int day = 0;
    static private Clock clock;

    //Game State persistent over days
    static Sprite player;

    //Game State persistent on same day
    private static Map<String, LevelState> levelData = new HashMap<>();
    private static List<Collectible> stolenCollectibles = new ArrayList<>();

    public static void init()
    {
        clock = new Clock(GameWindow.getCurrentNanoRenderTimeGameWindow());
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


    public static void addPlayerMAM_duringDay(int deltaMAM)
    {
        String methodName = "addPlayerManagementAttention(int) ";
        boolean debug = true;
        if (debug)
            System.out.println(CLASSNAME + methodName + "MAM: " + playerMaM_duringDay + " + " + deltaMAM + " = " + (playerMaM_duringDay.getValue() + deltaMAM));

        setPlayerMaM_duringDay(playerMaM_duringDay.getValue() + deltaMAM);
        ;
    }

    public static void addStolenCollectible(Collectible collectible)
    {
        stolenCollectibles.add(collectible);
    }

    public static int getDay()
    {
        return day;
    }

    public static void incrementDay()
    {
        String methodName = "incrementDay() ";
        playerMaM_dayStart = playerMaM_duringDay.getValue();
        day++;
        clock.reset();
        System.out.println(CLASSNAME + methodName + "Day: " + day + " MaM Start: " + playerMaM_dayStart);
    }

    public static int getPlayerMaM_dayStart()
    {
        return playerMaM_dayStart;
    }

    public static int getPlayerMaM_duringDay()
    {
        return playerMaM_duringDay.getValue();
    }

    public static IntegerProperty getPlayerMaM_duringDayProperty()
    {
        return playerMaM_duringDay;
    }


    public static Map<String, LevelState> getLevelData()
    {
        return levelData;
    }

    public static void setLevelData(Map<String, LevelState> levelData)
    {
        GameVariables.levelData = levelData;
    }

    public static List<Collectible> getStolenCollectibles()
    {
        return stolenCollectibles;
    }


    public static void setPlayerMaM_dayStart(int playerMaM_dayStart)
    {
        GameVariables.playerMaM_dayStart = playerMaM_dayStart;
    }

    public static void setPlayerMaM_duringDay(int playerMaM_duringDay)
    {
        String methodName = "setPlayerMaM_duringDay() ";
        GameVariables.playerMaM_duringDay.setValue(playerMaM_duringDay);
    }

    public static String getCLASSNAME()
    {
        return CLASSNAME;
    }

    public static GameVariables getSingleton()
    {
        return singleton;
    }

    public static IntegerProperty playerMaM_duringDayProperty()
    {
        return playerMaM_duringDay;
    }

    public static Sprite getPlayer()
    {
        return player;
    }

    public static void setSingleton(GameVariables singleton)
    {
        GameVariables.singleton = singleton;
    }

    public static void setDay(int day)
    {
        GameVariables.day = day;
    }

    public static void setStolenCollectibles(List<Collectible> stolenCollectibles)
    {
        GameVariables.stolenCollectibles = stolenCollectibles;
    }

    public static void addPlayerMoney(int delta)
    {
        String methodName = "addPlayerMoney() ";
        playerMoney.setValue(playerMoney.getValue() + delta);
        //System.out.println(CLASSNAME + methodName + "New balance: " + playerMoney.getValue());
    }

    public static int getPlayerMoney()
    {
        return playerMoney.getValue();
    }

    public static IntegerProperty playerMoneyProperty()
    {
        return playerMoney;
    }

    public static Clock getClock()
    {
        return clock;
    }
}
