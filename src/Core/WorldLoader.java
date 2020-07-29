package Core;

import Core.Enums.Direction;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

import java.util.*;

import static Core.Config.*;

public class WorldLoader
{
    private static final String CLASSNAME = "WorldLoader/";
    private static final Set<String> keywords = new HashSet<>();

    String levelName;
    String spawnId;
    Sprite player;
    Color shadowColor;
    private Rectangle2D borders;
    List<Sprite> passivLayer = new ArrayList<>();
    List<Sprite> activeLayer = new ArrayList<>();
    List<Sprite> bttmLayer = new ArrayList<>();
    List<Sprite> mediumLayer = new ArrayList<>();
    List<Sprite> upperLayer = new ArrayList<>();

    Set<String> definedMapCodesSet = new HashSet<>();
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

    public WorldLoader()
    {
        maxVerticalTile = 0;
        currentVerticalTile = 0;
        currentHorizontalTile = 0;
        maxHorizontalTile = 0;
        if (keywords.isEmpty())
        {
            keywords.add(KEYWORD_NEW_LAYER);
            keywords.add(KEYWORD_ACTORS);
            keywords.add(KEYWORD_TILEDEF);
            keywords.add(KEYWORD_PASSIV_LAYER);
            keywords.add(KEYWORD_WORLDSHADOW);
            keywords.add(KEYWORD_GROUPS);
            keywords.add(KEYWORD_SPAWNPOINTS);
            keywords.add(KEYWORD_INCLUDE);
        }

    }

    private void readFile(String fileName)
    {
        String methodName = "readFile() ";
        boolean debug = false;
        List<String[]> leveldata = Utilities.readAllLineFromTxt(STAGE_FILE_PATH + fileName + CSV_POSTFIX);
        definedMapCodesSet.clear();

        readMode = null;
        if (debug)
            System.out.println(CLASSNAME + methodName + "begin read file: " + fileName);
        for (int i = 0; i < leveldata.size(); i++)
        {
            String[] lineData = leveldata.get(i);

            try
            {
                readLine(lineData);
            }
            catch (IndexOutOfBoundsException | NumberFormatException e)
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : lineData)
                    stringBuilder.append(s).append("; ");
                throw new RuntimeException(e.getMessage() + "\nRead Mode: " + readMode + "\nat\t" + stringBuilder.toString());
            }
        }

        if (definedMapCodesSet.size() > 0)
            System.out.println(CLASSNAME + methodName + " found unsued tile or actor definition in " + fileName + ": " + definedMapCodesSet);

        if (debug)
            System.out.println(CLASSNAME + methodName + "finished read file: " + fileName);

    }

    public void load(String levelName, String spawnId)
    {
        String methodName = "load() ";
        this.levelName = levelName;
        this.spawnId = spawnId;
        readFile(this.levelName);
        borders = new Rectangle2D(0, 0, (maxHorizontalTile + 1) * 64, (maxVerticalTile) * 64);
    }

    private void readLine(String[] lineData)
    {
        String methodName = "readLine() ";

        if (keywords.contains(lineData[0].toLowerCase()))
        {
            readMode = lineData[0].toLowerCase();
            currentVerticalTile = 0;
            return;
        }

        {
            //process line according to keyword
            switch (readMode)
            {
                case KEYWORD_TILEDEF:
                    tileDataMap.put(lineData[SpriteData.tileCodeIdx], SpriteData.tileDefinition(lineData));
                    definedMapCodesSet.add(lineData[SpriteData.tileCodeIdx]);
                    break;
                case KEYWORD_NEW_LAYER:
                    readLineOfTiles(lineData, false);
                    break;
                case KEYWORD_PASSIV_LAYER:
                    readLineOfTiles(lineData, true);
                    break;
                case KEYWORD_ACTORS:
                    readActorData(lineData);
                    break;
                case KEYWORD_WORLDSHADOW:
                    readWorldShadow(lineData);
                    break;
                case KEYWORD_GROUPS:
                    readActorGroups(lineData);
                    break;
                case KEYWORD_SPAWNPOINTS:
                    readSpawnPoint(lineData);
                    break;
                case KEYWORD_INCLUDE:
                    String readModeTmp = readMode;
                    readInclude(lineData);
                    readMode = readModeTmp;
                    break;
                default:
                    throw new RuntimeException(CLASSNAME + methodName + "readMode unknown: " + readMode);
            }
        }

    }

    private void readInclude(String[] lineData)
    {
        String methodName = "readInclude()";
        boolean debug = false;
        int includeFilePathIdx = 0;
        int includeConditionTypeIdx = 1;
        int includeConditionParamsStartIdx = 2;


        //Check include condition
        String condition = lineData[includeConditionTypeIdx];
        switch (condition)
        {
            case INCLUDE_CONDITION_suspicion_lessequal:
                int suspicionThreshold = Integer.parseInt(lineData[includeConditionParamsStartIdx]);
                int currentSuspicion = GameVariables.getPlayerMaM_dayStart();
                if (currentSuspicion <= suspicionThreshold)//condition met
                {
                    if (debug)
                        System.out.println(CLASSNAME + methodName + "" + Arrays.toString(lineData) + " " + currentSuspicion + " / " + suspicionThreshold);
                    break;
                }
                else
                    return;
            case INCLUDE_CONDITION_day_greaterequal:
                int dayThreshold = Integer.parseInt(lineData[includeConditionParamsStartIdx]);
                int currentDay = GameVariables.getDay();
                if (currentDay >= dayThreshold)
                    break;
                else
                    return;
            default:
                throw new RuntimeException(CLASSNAME + methodName + " Include Condition unknown: " + condition);

        }

        readFile(lineData[includeFilePathIdx]);

    }


    private void readSpawnPoint(String[] lineData)
    {
        String methodName = "readSpawnPoint()";
        boolean debug = true;
        int spawnIdIdx = 0;
        int spawnXId = 1;
        int spawnYId = 2;
        int directionIdx = 3;
        Integer x = Integer.parseInt(lineData[spawnXId]);
        Integer y = Integer.parseInt(lineData[spawnYId]);
        Direction direction = Direction.getDirectionFromValue(lineData[directionIdx]);
        SpawnData spawnData = new SpawnData(x, y, direction);
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
        //System.out.println(CLASS_NAME + methodName + Arrays.toString(lineData));
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
                    ca = Sprite.createSprite(tile, 64 * currentHorizontalTile, currentVerticalTile * 64);
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
                passivLayer.add(Sprite.createSprite(new SpriteData("black", "void_64_64", false, 0d, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "none"), currentHorizontalTile * 64, currentVerticalTile * 64));
            }
            else if (isPassiv && lineData[currentHorizontalTile].equals(MAPDEFINITION_NO_TILE))
            {
                //Do nothing
            }
            else if (!lineData[currentHorizontalTile].equals(Config.MAPDEFINITION_EMPTY))
                System.out.println("WorldLoader readTile: tile definition not found: " + lineData[currentHorizontalTile] + " in line " + lineNumber + " column " + currentHorizontalTile);

            definedMapCodesSet.remove(lineData[currentHorizontalTile]); //For usage check of defined tiles and actors
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
            actorSprite = Sprite.createSprite(spriteData, x * 64, y * 64);
            actorSprite.actor = actor;
            actorSprite.setAnimationEnds(spriteData.animationEnds);
            actor.setSpeed(spriteData.velocity);//Set as often as Sprites exist?
            actor.dialogueStatusID = spriteData.dialogueID;
            actor.addSprite(actorSprite);
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
        else
            definedMapCodesSet.add(lineData[actorCodeIdx]);//Player is not defined by layers
    }

    private void createPlayer(ActorData actorData)
    {
        String methodName = "createPlayer(ActorData) ";
        SpawnData playerSpawn;
        if (spawnPointsMap.containsKey(spawnId))
            playerSpawn = spawnPointsMap.get(spawnId);
        else
            throw new RuntimeException("Spawn Point " + spawnId + " not set in " + levelName + "\nSpawn Points: " + spawnPointsMap.toString());
        Actor actor;
        //Reruse Player if already created
        if (WorldView.player != null)
        {
            actor = WorldView.player.actor;
            WorldView.player.setPosition(playerSpawn.x * 64, playerSpawn.y * 64);
        }
        else
            actor = createActor("player", playerSpawn.x, playerSpawn.y);


        actor.setDirection(playerSpawn.direction);
        activeLayer.addAll(actor.spriteList);
        List<SpriteData> spriteDataList = actor.spriteDataMap.get(actor.compoundStatus);
        for (int j = 0; j < spriteDataList.size(); j++)
        {
            //System.out.println(CLASSNAME + methodName + actor.spriteList.get(j) +" layer: "+ spriteDataList.get(j).heightLayer + " size " + spriteDataList.size());
            addToCollisionLayer(actor.spriteList.get(j), spriteDataList.get(j).heightLayer);
        }

        player = actor.spriteList.get(0);
    }

    class SpawnData
    {
        Integer x, y;
        Direction direction;

        public SpawnData(Integer x, Integer y, Direction direction)
        {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }

        @Override
        public String toString()
        {
            return  "x=" + x +
                    ", y=" + y +
                    ", direction=" + direction +
                    '}';
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

    /*
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

     */

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
