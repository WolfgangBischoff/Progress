package Core;

import javafx.geometry.Rectangle2D;

import java.util.*;

public class WorldLoader
{


    private Rectangle2D borders;
    List<Sprite> passivLayer = new ArrayList<>();
    List<Sprite> bttmLayer = new ArrayList<>();
    List<Sprite> mediumLayer = new ArrayList<>();
    List<Sprite> upperLayer = new ArrayList<>();
    String levelName;
    List<String[]> leveldata = new ArrayList<>();
    Sprite player;
    Map<String, SpriteData> tileDataMap = new HashMap<>();
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
                    tileDataMap.put(lineData[SpriteData.tileCodeIdx], SpriteData.tileDefinition(lineData));
                    //tileDefinition(lineData);
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

            }
        }
        borders = new Rectangle2D(0, 0, (maxHorizontalTile + 1) * 64, (maxVerticalTile) * 64);
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
        String methodName = "readTile ";
        for (int i = 0; i < lineData.length; i++)
        {
            //Is Tile
            if (tileDataMap.containsKey(lineData[i]))
            {
                SpriteData tile = tileDataMap.get(lineData[i]);
                Sprite ca = createSprite(tile, 64 * i, currentVerticalTile * 64);

                /*
                if (lineData[i].equals("X"))
                    player = ca;
*/
                if (isPassiv)
                    passivLayer.add(ca);
                else
                    addToPriorityLayer(ca, tile.priority);
            }
            //Is Actor
            else if(actorDataMap.containsKey(lineData[i]))
            {
                ActorData actorData = actorDataMap.get(lineData[i]);
                Sprite ca = createSprite(actorData, 64 * i, currentVerticalTile * 64);

                if (isPassiv)
                    passivLayer.add(ca);
                else
                {
                    addToPriorityLayer(ca, ca.actor.spriteData.get(actorData.status).priority);
                }
            }
            //Is Placeholder
            else if (!lineData[i].equals("___"))
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
        /*Status definedStatus = Status.getStatus(lineData[statusIdx]);
        SpriteData tileData = Actor.readSpriteData(lineData[actorNameIdx], definedStatus);
        tileData.actorStatus = definedStatus;
        tileDataMap.put(lineData[actorCodeIdx], tileData);
*/
        Status actorstatus = Status.getStatus(lineData[statusIdx]);
        ActorData actorData = new ActorData(lineData[actorNameIdx], actorstatus);
        actorDataMap.put(lineData[actorCodeIdx], actorData);
        //System.out.println(methodName + actorDataMap);
    }

    class ActorData
    {
        String actorname;
        Status status;

        public ActorData(String actorname, Status status)
        {
            this.actorname = actorname;
            this.status = status;
        }

        @Override
        public String toString()
        {
            return "ActorData{" +
                    "actorname='" + actorname + '\'' +
                    ", status=" + status +
                    '}';
        }
    }

    private Sprite createSprite(ActorData actorData, Integer x, Integer y)
    {
        Actor actor = new Actor(actorData.actorname, actorData.status);
        SpriteData spriteData = actor.spriteData.get(actorData.status);
        Sprite initSprite = createSprite(spriteData, x, y);
        initSprite.actor = actor;
        actor.sprite = initSprite;
        return initSprite;
    }

    private Sprite createSprite(SpriteData tile, Integer x, Integer y)
    {
        Sprite ca;
        if (tile.totalFrames > 1)
            ca = new Sprite(tile.spriteName, tile.fps, tile.totalFrames, tile.cols, tile.rows, tile.frameWidth, tile.frameHeight, tile.direction);
        else
            ca = new Sprite(tile.spriteName, tile.direction);

        ca.setName(tile.name);
        ca.setPosition(x, y);
        ca.setBlocker(tile.blocking);
        ca.setSpeed(tile.velocity);

        //If this was defined as Actor
        //if (tile.actorStatus != null)
         //   ca.setActor(new Actor(ca, tile.actorStatus));

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

}
