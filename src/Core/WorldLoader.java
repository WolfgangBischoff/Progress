package Core;

import Core.Enums.Direction;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.*;

import static Core.Config.*;

public class WorldLoader
{
    private static final String CLASSNAME = "WorldLoader/";

    String levelName;
    String spawnId;
    Sprite player;
    Color shadowColor;
    private Rectangle2D borders;
    List<String[]> leveldata = new ArrayList<>();
    List<Sprite> passivLayer = new ArrayList<>();
    List<Sprite> activeLayer = new ArrayList<>();
    List<Sprite> bttmLayer = new ArrayList<>();
    List<Sprite> mediumLayer = new ArrayList<>();
    List<Sprite> upperLayer = new ArrayList<>();


    Map<String, SpriteData> tileDataMap = new HashMap<>();
    Map<String, ActorData> actorDataMap = new HashMap<>();
    Map<String, SpawnData> spawnPointsMap = new HashMap<>();
    StageMonitor stageMonitor = new StageMonitor();
    Map<String, ActorGroupData> actorGroupDataMap = new HashMap<>();
    String readMode;
    int maxVerticalTile = 0;
    int currentVerticalTile = 0;
    int currentHorizontalTile = 0;
    int maxHorizontalTile = 0;

    public WorldLoader(String stageName, String spawnId)
    {
        levelName = stageName;
        this.spawnId = spawnId;
    }

    public void load()
    {
        leveldata = Utilities.readAllLineFromTxt(STAGE_FILE_PATH + levelName + CSV_POSTFIX);
        readMode = null;
        //Check for keyword
        Set<String> keywords = new HashSet<>();
        keywords.add(KEYWORD_NEW_LAYER);
        keywords.add(KEYWORD_ACTORS);
        keywords.add(KEYWORD_TILEDEF);
        keywords.add(KEYWORD_PASSIV_LAYER);
        keywords.add(KEYWORD_WORLDSHADOW);
        keywords.add(KEYWORD_GROUPS);
        keywords.add(KEYWORD_SPAWNPOINTS);

        for (int i = 0; i < leveldata.size(); i++)
        {
            String[] lineData = leveldata.get(i);

            if (keywords.contains(lineData[0].toLowerCase()))
            {
                readMode = lineData[0].toLowerCase();
                currentVerticalTile = 0;
                continue;
            }

            try
            {
                //process line according to keyword
                switch (readMode)
                {
                    case KEYWORD_TILEDEF:
                        tileDataMap.put(lineData[SpriteData.tileCodeIdx], SpriteData.tileDefinition(lineData));
                        continue;
                    case KEYWORD_NEW_LAYER:
                        readLineOfTiles(lineData, false);
                        continue;
                    case KEYWORD_PASSIV_LAYER:
                        readLineOfTiles(lineData, true);
                        continue;
                    case KEYWORD_ACTORS:
                        readActorData(lineData);
                        continue;
                    case KEYWORD_WORLDSHADOW:
                        readWorldShadow(lineData);
                        continue;
                    case KEYWORD_GROUPS:
                        readActorGroups(lineData);
                        continue;
                    case KEYWORD_SPAWNPOINTS:
                        readSpawnPoint(lineData);
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : lineData)
                    stringBuilder.append(s).append("; ");
                throw new IndexOutOfBoundsException(e.getMessage() + "\nRead Mode: " + readMode + "\nat\t" + stringBuilder.toString());
            }

        }
        borders = new Rectangle2D(0, 0, (maxHorizontalTile + 1) * 64, (maxVerticalTile) * 64);
    }

    private void readSpawnPoint(String[] lineData)
    {
        int spawnIdIdx = 0;
        int spawnXId = 1;
        int spawnYId = 2;
        int directionIdx =3;
        Integer x = Integer.parseInt(lineData[spawnXId]);
        Integer y = Integer.parseInt(lineData[spawnYId]);
        Direction direction = Direction.getDirectionFromValue(lineData[directionIdx]);
        SpawnData spawnData = new SpawnData(x,y,direction);
        spawnPointsMap.put(lineData[spawnIdIdx], spawnData);
    }

    private void readActorGroups(String[] lineData)
    {
        String methodName = "readActorGroups(String[])";
        boolean debug = false;

        int groupName_Idx = 0;
        int groupLogic_Idx = 1;
        int dependentGroupName_Idx = 2;
        int start_idx_memberIds = 3;
        stageMonitor.groupToLogicMap.put(lineData[groupName_Idx], lineData[groupLogic_Idx]);
        stageMonitor.groupIdToInfluencedGroupIdMap.put(lineData[groupName_Idx], lineData[dependentGroupName_Idx]);

        //map for all contained group members in which groups they are: actor -> groups
        ActorGroupData actorGroupData;
        for (int membersIdx = start_idx_memberIds; membersIdx < lineData.length; membersIdx++)
        {
            String actorId = lineData[membersIdx];
            if (!actorGroupDataMap.containsKey(actorId))
            {
                actorGroupDataMap.put(actorId, new ActorGroupData());
            }
            actorGroupData = actorGroupDataMap.get(actorId);
            actorGroupData.memberOfGroups.add(lineData[groupName_Idx]);
        }

        if (debug)
        {
            for (Map.Entry<String, ActorGroupData> actorData : actorGroupDataMap.entrySet())
                System.out.println(CLASSNAME + methodName + actorData.getKey() + " " + actorData.getValue().memberOfGroups);
        }


    }

    class ActorGroupData
    {
        List<String> memberOfGroups = new ArrayList();
    }

    private void readWorldShadow(String[] lineData)
    {
        String methodName = CLASSNAME + " readWorldShadow ";
        int red, green, blue;
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
                bttmLayer.add(sprite);
                break;
            case 1:
                mediumLayer.add(sprite);
                break;
            case 2:
                upperLayer.add(sprite);
                break;
            default:
                throw new RuntimeException("Layer not defined");
        }
    }

    private void readLineOfTiles(String[] lineData, Boolean isPassiv) throws IllegalArgumentException
    {
        String methodName = "readTile ";
        String lineNumber = "[not set]";


        //from left to right, reads tile codes
        for (currentHorizontalTile = 0; currentHorizontalTile < lineData.length; currentHorizontalTile++)
        {
            //if first column is line number
            if (currentHorizontalTile == 0 && lineData[currentHorizontalTile].chars().allMatch(x -> Character.isDigit(x)))
            {
                lineNumber = lineData[0];
                lineData = Arrays.copyOfRange(lineData, 1, lineData.length);
            }


            //Is Tile
            if (tileDataMap.containsKey(lineData[currentHorizontalTile]))
            {
                SpriteData tile = tileDataMap.get(lineData[currentHorizontalTile]);
                Sprite ca;
                try
                {
                    ca = createSprite(tile, 64 * currentHorizontalTile, currentVerticalTile * 64);
                }
                catch (IllegalArgumentException e)
                {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s : lineData)
                    {
                        stringBuilder.append(s).append(" ");
                    }
                    throw new IllegalArgumentException("\nLine: " + stringBuilder.toString() +
                            "\n " + lineData[currentHorizontalTile] + " ===> /res/img/" + tile.spriteName + ".png" + " not found");
                }


                if (isPassiv)
                    passivLayer.add(ca);
                else
                    addToCollisionLayer(ca, tile.heightLayer);
            }
            //Is Actor
            else if (actorDataMap.containsKey(lineData[currentHorizontalTile]))
            {
                Actor actor = createActor(lineData[currentHorizontalTile]);
                activeLayer.addAll(actor.spriteList);
                List<SpriteData> spriteDataList = actor.spriteDataMap.get(actor.compoundStatus);
                for (int j = 0; j < spriteDataList.size(); j++)
                    addToCollisionLayer(actor.spriteList.get(j), spriteDataList.get(j).heightLayer);
            }
            //Is Placeholder for black background
            else if (isPassiv && lineData[currentHorizontalTile].equals(Config.MAPDEFINITION_EMPTY))
            {
                passivLayer.add(createSprite(new SpriteData("black", "void_64_64", true, 0d, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "none"), currentHorizontalTile * 64, currentVerticalTile * 64));
            }
            else if (isPassiv && lineData[currentHorizontalTile].equals(MAPDEFINITION_NO_TILE))
            {
            //Do nothing
            }
            else if (!lineData[currentHorizontalTile].equals(Config.MAPDEFINITION_EMPTY))
                System.out.println("WorldLoader readTile: tile definition not found: " + lineData[currentHorizontalTile] + " in line " + lineNumber + " column " + currentHorizontalTile);

            maxHorizontalTile = Math.max(currentHorizontalTile, maxHorizontalTile);
        }

        currentVerticalTile++;
        if (maxVerticalTile < currentVerticalTile)
            maxVerticalTile = currentVerticalTile;
    }

    private Actor createActor(String actorId)
    {
        return createActor(actorId, currentHorizontalTile, currentVerticalTile);
    }

    private Actor createActor(String actorId, Integer x, Integer y)
    {
        ActorData actorData = actorDataMap.get(actorId);

        //foreach Sprite Data add Sprite to layer, Actor save sprite
        Actor actor = new Actor(actorData.actorFileName, actorData.actorInGameName, actorData.generalStatus, actorData.sensor_status, actorData.direction);
        actor.updateCompoundStatus();
        List<SpriteData> spriteDataList = actor.spriteDataMap.get(actor.compoundStatus);
        actor.stageMonitor = stageMonitor;

        //check for actorgroup Data
        ActorGroupData actorGroupData = actorGroupDataMap.get(actorId);
        if (actorGroupData != null)
        {
            actor.memberActorGroups.addAll(actorGroupData.memberOfGroups);
            for (String groupName : actor.memberActorGroups)
                stageMonitor.addActorToActorSystem(groupName, actor);
        }

        //Create initial Sprites of Actor
        for (int j = 0; j < spriteDataList.size(); j++)
        {
            Sprite actorSprite;
            SpriteData spriteData = spriteDataList.get(j);
            actorSprite = createSprite(spriteData, x * 64, y * 64);
            actorSprite.actor = actor;
            actorSprite.setAnimationEnds(spriteData.animationEnds);
            actor.setSpeed(spriteData.velocity);//Set as often as Sprites exist?
            actor.dialogueStatusID = spriteData.dialogueID;
            actor.addSprite(actorSprite);

            //activeLayer.add(actorSprite);
            //addToCollisionLayer(actorSprite, spriteDataList.get(j).heightLayer);
        }
        return actor;
    }

    private void readActorData(String[] lineData)
    {
        String methodName = "readActorData() ";
        //Reads sprite data from given status and add to tile definition, later actor will be added
        int actorCodeIdx = 0;
        int actorFileNameIdx = 1;
        int actorIngameNameIdx = 2;
        int sprite_statusIdx = 3;
        int sensor_statusIdx = 4;
        int directionIdx = 5;

        Direction direction = Direction.getDirectionFromValue(lineData[directionIdx]);
        ActorData actorData = new ActorData(lineData[actorFileNameIdx], lineData[actorIngameNameIdx], lineData[sprite_statusIdx], lineData[sensor_statusIdx], direction);
        actorDataMap.put(lineData[actorCodeIdx], actorData);

        //Player start position is not based on tile schema
        if (actorData.actorFileName.toLowerCase().equals("player"))
            createPlayer(actorData);
    }

    private void createPlayer(ActorData actorData)
    {
        String methodName = "createPlayer(ActorData) ";
        SpawnData playerSpawn = null;
        if(spawnPointsMap.containsKey(spawnId))
            playerSpawn = spawnPointsMap.get(spawnId);
        else
            throw new RuntimeException("Spawn Point " + spawnId + " not set in " + levelName + "\nSpawn Points: " + spawnPointsMap.toString());
        Actor actor = createActor("player", playerSpawn.x, playerSpawn.y);
        actor.setDirection(playerSpawn.direction);
        activeLayer.addAll(actor.spriteList);
        List<SpriteData> spriteDataList = actor.spriteDataMap.get(actor.compoundStatus);
        for (int j = 0; j < spriteDataList.size(); j++)
            addToCollisionLayer(actor.spriteList.get(j), spriteDataList.get(j).heightLayer);

        player = actor.spriteList.get(0);
    }

    class SpawnData
    {
        Integer x,y;
        Direction direction;

        public SpawnData(Integer x, Integer y, Direction direction)
        {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
    }

    class ActorData
    {
        String actorFileName;
        String actorInGameName;
        String generalStatus;
        String sensor_status;
        Direction direction;

        public ActorData(String actorname, String actorInGameName, String generalStatus, String sensor_status, Direction direction)
        {
            this.actorFileName = actorname;
            this.actorInGameName = actorInGameName;
            this.sensor_status = sensor_status;
            this.generalStatus = generalStatus;
            this.direction = direction;
        }

        @Override
        public String toString()
        {
            return "ActorData{" +
                    "actorname='" + actorFileName + '\'' +
                    '}';
        }
    }

    private Sprite createSprite(SpriteData tile, Integer x, Integer y)
    {
        Sprite ca;
        try
        {
            if (tile.totalFrames > 1)
                ca = new Sprite(tile.spriteName, tile.fps, tile.totalFrames, tile.cols, tile.rows, tile.frameWidth, tile.frameHeight);
            else
                ca = new Sprite(tile.spriteName);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            ca = new Sprite(IMAGE_DIRECTORY_PATH + "notfound_64_64" + CSV_POSTFIX);
        }

        ca.setName(tile.name);
        ca.setPosition(x, y);
        ca.setBlocker(tile.blocking);
        ca.setLightningSpriteName(tile.lightningSprite);

        //if (ca.getName().toLowerCase().equals("player"))
         //   player = ca;

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
