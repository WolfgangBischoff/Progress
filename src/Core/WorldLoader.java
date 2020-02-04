package Core;

import Core.Enums.Direction;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

import java.util.*;

public class WorldLoader
{
    private Rectangle2D borders;
    List<Sprite> passivLayer = new ArrayList<>();
    List<Sprite> activeLayer = new ArrayList<>();
    List<Sprite> bttmLayer = new ArrayList<>();
    List<Sprite> mediumLayer = new ArrayList<>();
    List<Sprite> upperLayer = new ArrayList<>();
    String levelName;
    List<String[]> leveldata = new ArrayList<>();
    Sprite player;
    Map<String, SpriteData> tileDataMap = new HashMap<>();
    Map<String, ActorData> actorDataMap = new HashMap<>();
    Color shadowColor;
    int maxVerticalTile = 0;
    int currentVerticalTile = 0;
    int maxHorizontalTile = 0;
    String readMode;

    private final String className = "WorldLoader ";
    private final String KEYWORD_NEW_LAYER = "layer:";
    private final String KEYWORD_PASSIV_LAYER = "passivlayer:";
    private final String KEYWORD_ACTORS = "actors:";
    private final String KEYWORD_TILEDEF = "tiledefinition:";
    private final String KEYWORD_WORLDSHADOW = "shadow:";

    public WorldLoader(String stageName)
    {
        levelName = stageName;
    }

    public void load()
    {
        leveldata = Utilities.readAllLineFromTxt("src/res/level/" + levelName + ".csv");
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
            keywords.add(KEYWORD_WORLDSHADOW);
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
                    tileDataMap.put(lineData[SpriteData.tileCodeIdx], SpriteData.tileDefinition(lineData));
                    continue;
                case KEYWORD_NEW_LAYER:
                    readTile(lineData, false);
                    continue;
                case KEYWORD_PASSIV_LAYER:
                    readTile(lineData, true);
                    continue;
                case KEYWORD_ACTORS:
                    readActors(lineData);
                    continue;
                case KEYWORD_WORLDSHADOW:
                    readWorldShadow(lineData);
                    continue;

            }
        }
        borders = new Rectangle2D(0, 0, (maxHorizontalTile + 1) * 64, (maxVerticalTile) * 64);
    }

    private void readWorldShadow(String[] lineData)
    {
        String methodName = className + " readWorldShadow ";
        Integer red, green, blue;
        if (lineData[0].toLowerCase().equals("none"))
            shadowColor = null;
        else
        {
            red = Integer.parseInt(lineData[0]);
            green = Integer.parseInt(lineData[1]);
            blue = Integer.parseInt(lineData[2]);
            shadowColor = Color.rgb(red, green, blue);
        }

    }

    private void addToCollisionLayer(Sprite sprite, int layer)
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
        String methodName = "readTile ";
        //from left to right, reads tile codes
        for (int i = 0; i < lineData.length; i++)
        {
            //Is Tile
            if (tileDataMap.containsKey(lineData[i]))
            {
                SpriteData tile = tileDataMap.get(lineData[i]);
                Sprite ca = createSprite(tile, 64 * i, currentVerticalTile * 64);

                if (isPassiv)
                    passivLayer.add(ca);
                else
                    addToCollisionLayer(ca, tile.heightLayer);
            }
            //Is Actor
            else if (actorDataMap.containsKey(lineData[i]))
            {
                ActorData actorData = actorDataMap.get(lineData[i]);

                //foreach Sprite Data add Sprite to layer, Actor save sprite
                Actor actor = new Actor(actorData.actorname, actorData.generalStatus, actorData.direction);
                actor.updateCompoundStatus();
                List<SpriteData> spriteDataList = actor.spriteDataMap.get(actor.compoundStatus);

                for (int j = 0; j < spriteDataList.size(); j++)
                {
                    Sprite actorSprite;
                    SpriteData spriteData = spriteDataList.get(j);
                    actorSprite = createSprite(spriteData, i * 64, currentVerticalTile * 64);
                    actorSprite.actor = actor;
                    actor.setSpeed(spriteData.velocity);//Set as often as Sprites exist?
                    actor.dialogueStatusID = spriteData.dialogueID;

                    if (actor.getDirection() != Direction.UNDEFINED)//If a sprite has a direction it typically can move or detect something actively
                        activeLayer.add(actorSprite);

                    actor.addSprite(actorSprite);
                    addToCollisionLayer(actorSprite, spriteDataList.get(j).heightLayer);
                }

            }
            //Is Placeholder
            else if (!lineData[i].equals(Config.MAPDEFINITION_EMPTY))
                System.out.println("WorldLoader readTile: tile definition not found: " + lineData[i]);

            maxHorizontalTile = i > maxHorizontalTile ? i : maxHorizontalTile;
        }

        currentVerticalTile++;
        if (maxVerticalTile < currentVerticalTile)
            maxVerticalTile = currentVerticalTile;
    }

    private void readActors(String[] lineData)
    {
        String methodName = "readActors ";
        //Reads sprite data from given status and add to tile definition, later actor will be added
        int actorCodeIdx = 0;
        int actorNameIdx = 1;
        int statusIdx = 2;
        int directionIdx = 3;
        Direction direction = Direction.getDirectionFromValue(lineData[directionIdx]);
        ActorData actorData = new ActorData(lineData[actorNameIdx], lineData[statusIdx], direction);
        actorDataMap.put(lineData[actorCodeIdx], actorData);
    }

    class ActorData
    {
        String actorname;
        String generalStatus;
        Direction direction;

        public ActorData(String actorname, String generalStatus, Direction direction)
        {
            this.actorname = actorname;
            this.generalStatus = generalStatus;
            this.direction = direction;
        }

        @Override
        public String toString()
        {
            return "ActorData{" +
                    "actorname='" + actorname + '\'' +
                    '}';
        }
    }

    private Sprite createSprite(SpriteData tile, Integer x, Integer y)
    {
        Sprite ca;
        if (tile.totalFrames > 1)
            ca = new Sprite(tile.spriteName, tile.fps, tile.totalFrames, tile.cols, tile.rows, tile.frameWidth, tile.frameHeight);
        else
            ca = new Sprite(tile.spriteName);

        ca.setName(tile.name);
        ca.setPosition(x, y);
        ca.setBlocker(tile.blocking);
        ca.setLightningSpriteName(tile.lightningSprite);

        if (ca.getName().toLowerCase().equals("player"))
            player = ca;

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

    public Color getShadowColor()
    {
        return shadowColor;
    }
}
