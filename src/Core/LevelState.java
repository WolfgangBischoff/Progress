package Core;

import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelState
{
    final private static String CLASSNAME = "LevelState ";

    String levelName;
    int day;
    private Rectangle2D borders;
    private List<Sprite> activeSpritesLayer;
    //private List<Sprite> passiveCollisionRelevantSpritesLayer;
    private List<Sprite> passiveSpritesLayer;
    private List<Sprite> bottomLayer;
    private List<Sprite> middleLayer;
    private List<Sprite> topLayer;
    private StageMonitor stageMonitor;
    private Color shadowColor;
    private Map<String, WorldLoader.SpawnData> spawnPointsMap;

    public LevelState(String levelName, int day, Rectangle2D borders, List<Sprite> activeSpritesLayer, List<Sprite> passiveSpritesLayer, List<Sprite> bottomLayer, List<Sprite> middleLayer, List<Sprite> topLayer, StageMonitor stageMonitor, Color shadowColor, Map<String, WorldLoader.SpawnData> spawnPointsMap)
    {
        this.levelName = levelName;
        this.day = day;
        this.borders = borders;
        this.activeSpritesLayer = activeSpritesLayer;
        this.passiveSpritesLayer = passiveSpritesLayer;
        this.bottomLayer = bottomLayer;
        this.middleLayer = middleLayer;
        this.topLayer = topLayer;
        this.stageMonitor = stageMonitor;
        this.shadowColor = shadowColor;
        this.spawnPointsMap = new HashMap<>(spawnPointsMap);
    }

    @Override
    public String toString()
    {
        return levelName
                + " Day: " + day
                + " SpawnPoints: " + spawnPointsMap.toString()
          //      + " active Sprites: " + activeSpritesLayer.size()
          //      + " passive Sprites: " + passiveSpritesLayer.size()
                ;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public int getDay()
    {
        return day;
    }

    public Rectangle2D getBorders()
    {
        return borders;
    }

    public List<Sprite> getActiveSpritesLayer()
    {
        return activeSpritesLayer;
    }


    public List<Sprite> getPassiveSpritesLayer()
    {
        return passiveSpritesLayer;
    }

    public List<Sprite> getBottomLayer()
    {
        return bottomLayer;
    }

    public List<Sprite> getMiddleLayer()
    {
        return middleLayer;
    }

    public List<Sprite> getTopLayer()
    {
        return topLayer;
    }

    public StageMonitor getStageMonitor()
    {
        return stageMonitor;
    }

    public Color getShadowColor()
    {
        return shadowColor;
    }

    public Map<String, WorldLoader.SpawnData> getSpawnPointsMap()
    {
        return spawnPointsMap;
    }
}
