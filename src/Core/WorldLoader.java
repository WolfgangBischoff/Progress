package Core;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.*;

public class WorldLoader
{
    final int tileCodeIdx = 0;
    final int spriteNameIdx = 1;
    final int blockingIdx = 2;
    final int layerIdx = 3;
    final int fpsIdx = 4;
    final int totalFramesIdx = 5;
    final int colsIdx = 6;
    final int rowsIdx = 7;
    final int frameWidthIdx = 8;
    final int frameHeightIdx = 9;
    final int velocityIdx = 10;
    final int directionIdx = 11;
    final int hitboxOffsetXIdx = 12;
    final int hitboxOffsetYIdx = 13;
    final int hitboxWidthIdx = 14;
    final int hitboxHeightIdx = 15;


    private Rectangle2D borders;
    List<Sprite> passivLayer = new ArrayList<>();
    List<Sprite> bttmLayer = new ArrayList<>();
    List<Sprite> mediumLayer = new ArrayList<>();
    List<Sprite> upperLayer = new ArrayList<>();
    String levelName;
    GraphicsContext gc;
    List<String[]> leveldata = new ArrayList<>();
    List<String[]> actordata = new ArrayList<>();
    Sprite player;
    Map<String, TileData> tileDataMap = new HashMap<>();
    Map<String, ActorData> actorDataMap = new HashMap<>();
    int maxVerticalTile = 0;
    int currentVerticalTile = 0;
    int maxHorizontalTile = 0;
    String readMode;

    private final String KEYWORD_NEW_LAYER = "layer:";
    private final String KEYWORD_PASSIV_LAYER = "passivlayer:";
    private final String KEYWORD_ACTORS = "actors:";
    private final String KEYWORD_TILEDEF = "tiledefinition:";

    public WorldLoader(String stageName)
    {
        levelName = stageName;
    }

    public void load()
    {
        leveldata = Utilities.readAllLineFromTxt("src/res/level/" + levelName + ".csv");
        //actordata = Utilities.readAllLineFromTxt("src/res/actorData/" + "actorData" + ".csv");
        //readActorData();
        //System.out.print("WorldLoader: " + actorDataMap.toString());

        readMode = null;

        for (int i = 0; i < leveldata.size(); i++)
        {
            String[] lineData = leveldata.get(i);

            //Check for keyword
            Set<String> keywords = new HashSet<>();
            keywords.add(KEYWORD_NEW_LAYER);
            keywords.add(KEYWORD_ACTORS);
            keywords.add(KEYWORD_TILEDEF);
            keywords.add(KEYWORD_PASSIV_LAYER);
            if (keywords.contains(lineData[0]))
            {
                readMode = lineData[0];
                currentVerticalTile = 0;
                continue;
            }

            //process line according to keyword
            switch (readMode)
            {
                case KEYWORD_TILEDEF:
                    tileDefinition(lineData);
                    continue;
                case KEYWORD_NEW_LAYER:
                    readTile(lineData, false);
                    continue;
                case KEYWORD_PASSIV_LAYER:
                    readTile(lineData, true);
                case KEYWORD_ACTORS:
                    readActors(lineData);
                    continue;

            }
        }
        borders = new Rectangle2D(0, 0, (maxHorizontalTile + 1) * 64 - player.basewidth, (maxVerticalTile) * 64 - player.baseheight);
    }

    private void tileDefinition(String[] lineData)
    {
        //System.out.println("WorldLoader/tileDefinition: " + lineData[0]);
        Boolean blocking = Boolean.parseBoolean(lineData[blockingIdx]);
        Integer priority = Integer.parseInt(lineData[layerIdx]);
        Integer fps = Integer.parseInt(lineData[fpsIdx]);
        Integer totalFrames = Integer.parseInt(lineData[totalFramesIdx]);
        Integer cols = Integer.parseInt(lineData[colsIdx]);
        Integer rows = Integer.parseInt(lineData[rowsIdx]);
        Integer frameWidth = Integer.parseInt(lineData[frameWidthIdx]);
        Integer frameHeight = Integer.parseInt(lineData[frameHeightIdx]);
        Integer velocity = Integer.parseInt(lineData[velocityIdx]);
        Integer direction = Integer.parseInt(lineData[directionIdx]);
        Integer hitboxOffsetX = Integer.parseInt(lineData[hitboxOffsetXIdx]);
        Integer hitboxOffsetY = Integer.parseInt(lineData[hitboxOffsetYIdx]);
        Integer hitboxWidth = Integer.parseInt(lineData[hitboxWidthIdx]);
        Integer hitboxHeight = Integer.parseInt(lineData[hitboxHeightIdx]);

        TileData current = new TileData(lineData[spriteNameIdx], blocking, fps, totalFrames, cols, rows, frameWidth, frameHeight, velocity, direction, priority, hitboxOffsetX, hitboxOffsetY, hitboxWidth, hitboxHeight);
        tileDataMap.put(lineData[tileCodeIdx], current);
    }

    class TileData
    {
        public String spriteName;
        public Boolean blocking;
        public Integer fps, totalFrames, cols, rows, frameWidth, frameHeight, velocity, direction, priority, hitboxOffsetX, hitboxOffsetY, hitboxWidth, hitboxHeight;

        public TileData(String spriteName, Boolean blocking, Integer fps, Integer totalFrames, Integer cols, Integer rows, Integer frameWidth, Integer frameHeight, Integer velocity, Integer direction, Integer priority, Integer hitboxOffsetX, Integer hitboxOffsetY, Integer hitboxWidth, Integer hitboxHeight)
        {
            this.spriteName = spriteName;
            this.blocking = blocking;
            this.fps = fps;
            this.totalFrames = totalFrames;
            this.cols = cols;
            this.rows = rows;
            this.frameWidth = frameWidth;
            this.frameHeight = frameHeight;
            this.velocity = velocity;
            this.direction = direction;
            this.priority = priority;
            this.hitboxOffsetX = hitboxOffsetX;
            this.hitboxOffsetY = hitboxOffsetY;
            this.hitboxWidth = hitboxWidth;
            this.hitboxHeight = hitboxHeight;
        }
    }

    private void addToPriorityLayer(Sprite sprite, int layer)
    {
        switch (layer)
        {
            case 0:
                bttmLayer.add(sprite); break;
            case 1:
                mediumLayer.add(sprite); break;
            case 2:
                upperLayer.add(sprite); break;
            default:
                throw new RuntimeException("Layer not defined");
        }
    }

    private void readTile(String[] lineData, Boolean isPassiv)
    {
        for (int i = 0; i < lineData.length; i++)
        {
            if (tileDataMap.containsKey(lineData[i]))
            {
                TileData tile = tileDataMap.get(lineData[i]);
                Sprite ca = createSprite(tile, 64 * i, currentVerticalTile * 64);

                if (lineData[i].equals("X"))
                    player = ca;

                if (isPassiv)
                    passivLayer.add(ca);
                else
                    addToPriorityLayer(ca, tile.priority);
            }
            else if (!lineData[i].equals("_"))
                System.out.println("WorldLoader readTile: tile definition not found: " + lineData[i]);

            maxHorizontalTile = i > maxHorizontalTile ? i : maxHorizontalTile;
        }

        currentVerticalTile++;
        if (maxVerticalTile < currentVerticalTile)
            maxVerticalTile = currentVerticalTile;
    }

    private void readActors(String[] lineData)
    {
        TileData tile = tileDataMap.get(lineData[0]);
        if (tile != null)
        {
            Sprite ca = createSprite(tile, Integer.parseInt(lineData[1]), Integer.parseInt(lineData[2]));
            addToPriorityLayer(ca, tile.priority);

            if (lineData[0].equals("Player"))
                player = ca;
        }
        else
            System.out.println("Tile definition not found: " + lineData[0]);
    }

    class ActorData
    {
        List<String> alternativeSprites = new ArrayList<>();

        ActorData(String[] input)
        {
            for(int i = 0; i<input.length; i++)
            {
                if(i != 0)
                {
                    alternativeSprites.add(input[i]);
                }
            }
        }

        @Override
        public String toString()
        {
            return alternativeSprites.toString();
        }
    }

    private void readActorData()
    {
        for(String[] dataline : actordata)
        {
            actorDataMap.put(dataline[0], new ActorData(dataline));
        }
    }

    private Sprite createSprite(TileData tile, Integer x, Integer y)
    {
        Sprite ca;
        if (tile.totalFrames > 1)
            ca = new Sprite(tile.spriteName, tile.fps, tile.totalFrames, tile.cols, tile.rows, tile.frameWidth, tile.frameHeight, tile.direction);
        else
            ca = new Sprite(tile.spriteName, tile.direction);

        ca.setName(tile.spriteName);
        ca.setPosition(x, y);
        ca.setBlocker(tile.blocking);
        ca.setSpeed(tile.velocity);
        ca.setActor(new Actor(ca));

        //If Hitbox differs
        if (tile.hitboxOffsetX != 0 || tile.hitboxOffsetY != 0 || tile.hitboxWidth != 0 || tile.hitboxHeight != 0)
            ca.setHitBox(tile.hitboxOffsetX, tile.hitboxOffsetY, tile.hitboxWidth, tile.hitboxHeight);

        return ca;
    }

    public Rectangle2D getBorders()
    {
        return borders;
    }

    public List<Sprite> getMediumLayer()
    {
        return mediumLayer;
    }

    public List<Sprite> getBttmLayer()
    {
        return bttmLayer;
    }

    public List<Sprite> getUpperLayer()
    {
        return upperLayer;
    }

    public Sprite getPlayer()
    {
        return player;
    }

    public List<Sprite> getPassivLayer()
    {
        return passivLayer;
    }

}
