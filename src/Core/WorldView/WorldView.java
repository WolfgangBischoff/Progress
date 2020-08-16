package Core.WorldView;

import Core.*;
import Core.Enums.Direction;
import Core.Enums.TriggerType;
import Core.Menus.DaySummary.DaySummaryScreenController;
import Core.Menus.DiscussionGame.DiscussionGame;
import Core.Menus.Inventory.InventoryOverlay;
import Core.Menus.Personality.PersonalityScreenController;
import Core.Menus.StatusBarOverlay;
import Core.Menus.Textbox.Textbox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.Config.*;
import static Core.WorldView.WorldViewStatus.*;

public class WorldView implements GUIController
{
    private static final String CLASSNAME = "WorldView ";
    private static WorldView singleton;

    @FXML
    Pane root;
    FXMLLoader fxmlLoader;

    Canvas worldCanvas;
    GraphicsContext gc;
    Canvas shadowMask;
    GraphicsContext ShadowMaskGc;
    Map<String, Image> lightsImageMap = new HashMap<>();
    Color shadowColor;
    WorldViewController worldViewController = new WorldViewController();

    //Inventory Overlay
    static boolean isInventoryActive = false;
    static InventoryOverlay inventoryOverlay;
    static Point2D inventoryOverlayPosition = INVENTORY_POSITION;
    static Long lastTimeMenuWasOpened = 0L;

    //TextBox Overlay
    static boolean isTextBoxActive = false;
    static Textbox textbox;
    static Point2D textBoxPosition = new Point2D(CAMERA_WIDTH / 2f - Textbox.getTEXT_BOX_WIDTH() / 2, CAMERA_HEIGHT - Textbox.getTEXT_BOX_HEIGHT() - 32);

    //Personality Overlay
    static boolean isPersonalityScreenActive = false;
    static PersonalityScreenController personalityScreenController;
    static Point2D personalityScreenPosition = PERSONALITY_POSITION;

    //Discussion Game Overlay
    static boolean isDiscussionGameActive = false;
    static DiscussionGame discussionGame;
    static Point2D discussionGamePosition = DISCUSSION_POSITION;

    //DaySummary Overlay
    private static boolean isDaySummaryActive = false;
    static DaySummaryScreenController daySummaryScreenController = new DaySummaryScreenController();
    static Point2D daySummaryScreenPosition = DAY_SUMMARY_POSITION;

    //Management Attention Meter Overlay
    private static boolean isManagementAttentionMeterActive = true;
    static StatusBarOverlay mamOverlay = new StatusBarOverlay(MAM_BAR_WIDTH, MAM_BAR_HEIGHT, GameVariables.getPlayerMaM_duringDayProperty(), 100);
    static Point2D mamOverlayPosition = MAM_BAR_POSITION;

    //Sprites
    String levelName;
    private static Rectangle2D borders;
    static List<Sprite> activeSpritesLayer = new ArrayList<>();
    static List<Sprite> passiveCollisionRelevantSpritesLayer = new ArrayList<>();
    static List<Sprite> passiveSpritesLayer = new ArrayList<>();
    static List<Sprite> bottomLayer = new ArrayList<>();
    static List<Sprite> middleLayer = new ArrayList<>();
    static List<Sprite> topLayer = new ArrayList<>();
    static Sprite player;
    static Map<String, WorldLoader.SpawnData> spawnPointsMap = new HashMap<>();
    static List<Sprite> toRemove = new ArrayList<>();

    //Camera
    double offsetMaxX;
    double offsetMaxY;
    int offsetMinX = 0;
    int offsetMinY = 0;
    static double camX;
    static double camY;

    double bumpX = 0, bumpY = 0, rumbleGrade = RUMBLE_GRADE;
    Long timeStartBump = null;
    float durationBump = RUMBLE_MAX_DURATION;
    boolean bumpActive = false;

    private WorldView(String levelName)
    {
        fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PlayerView.fxml"));
        fxmlLoader.setController(this);

        worldCanvas = new Canvas(CAMERA_WIDTH, Config.CAMERA_HEIGHT);
        shadowMask = new Canvas(CAMERA_WIDTH, Config.CAMERA_HEIGHT);
        gc = worldCanvas.getGraphicsContext2D();
        ShadowMaskGc = shadowMask.getGraphicsContext2D();
        loadStage(levelName, "default");
        inventoryOverlay = new InventoryOverlay();
        textbox = new Textbox();
        worldViewController.setWorldViewStatus(WORLD);
    }

    public void saveStage()
    {
        String levelNameToSave = this.levelName;
        activeSpritesLayer.remove(player);
        middleLayer.remove(player); //Player Layer
        GameVariables.setPlayer(player);
        GameVariables.saveLevelState(new LevelState(levelNameToSave, GameVariables.getDay(), borders, activeSpritesLayer, passiveSpritesLayer, bottomLayer, middleLayer, topLayer, shadowColor, spawnPointsMap));
    }

    public void loadStage(String levelName, String spawnId)
    {
        String methodName = "loadStage() ";
        boolean debug = false;

        clearLevel();
        this.levelName = levelName;
        //check if level was already loaded today
        LevelState levelState = GameVariables.getLevelData().get(this.levelName);
        if (levelState != null && levelState.getDay() == GameVariables.getDay())
        {
            if (debug)
                System.out.println(CLASSNAME + methodName + "loaded from state");
            loadFromLevelState(levelState, spawnId);
        }
        else
        {
            if (debug)
                System.out.println(CLASSNAME + methodName + "loaded from file");
            loadLevelFromFile(spawnId);
        }

        offsetMaxX = borders.getMaxX() - CAMERA_WIDTH;
        offsetMaxY = borders.getMaxY() - Config.CAMERA_HEIGHT;
    }

    private void clearLevel()
    {
        //Not clear(), lists are copied to LevelState
        passiveSpritesLayer = new ArrayList<>();
        activeSpritesLayer = new ArrayList<>();
        bottomLayer = new ArrayList<>();
        middleLayer = new ArrayList<>();
        topLayer = new ArrayList<>();
        passiveCollisionRelevantSpritesLayer = new ArrayList<>();
        borders = null;
        shadowColor = null;
    }

    private void loadLevelFromFile(String spawnId)
    {
        WorldLoader worldLoader = new WorldLoader();
        worldLoader.load(levelName, spawnId);
        player = worldLoader.getPlayer();
        passiveSpritesLayer = worldLoader.getPassivLayer(); //No collision just render
        activeSpritesLayer = worldLoader.getActiveLayer();
        bottomLayer = worldLoader.getBttmLayer(); //Render height
        middleLayer = worldLoader.getMediumLayer();
        topLayer = worldLoader.getUpperLayer();
        passiveCollisionRelevantSpritesLayer.addAll(bottomLayer); //For passive collision check
        passiveCollisionRelevantSpritesLayer.addAll(middleLayer);
        passiveCollisionRelevantSpritesLayer.addAll(topLayer);
        borders = worldLoader.getBorders();
        shadowColor = worldLoader.getShadowColor();
        spawnPointsMap = worldLoader.getSpawnPointsMap();
    }

    private void loadFromLevelState(LevelState levelState, String spawnId)
    {
        String methodName = "loadFromLevelState() ";
        passiveSpritesLayer = levelState.getPassiveSpritesLayer(); //No collision just render
        activeSpritesLayer = levelState.getActiveSpritesLayer();
        bottomLayer = levelState.getBottomLayer(); //Render height
        middleLayer = levelState.getMiddleLayer();
        topLayer = levelState.getTopLayer();
        borders = levelState.getBorders();
        shadowColor = levelState.getShadowColor();
        spawnPointsMap = levelState.getSpawnPointsMap();

        //Player
        player = GameVariables.getPlayer();
        WorldLoader.SpawnData spawnData = levelState.getSpawnPointsMap().get(spawnId);
        player.getActor().setDirection(spawnData.getDirection());
        player.setPosition(spawnData.getX() * 64, spawnData.getY() * 64);
        middleLayer.add(player); //assumption player on layer 1
        activeSpritesLayer.add(player);

        passiveCollisionRelevantSpritesLayer.addAll(bottomLayer); //For passive collision check
        passiveCollisionRelevantSpritesLayer.addAll(middleLayer);
        passiveCollisionRelevantSpritesLayer.addAll(topLayer);

    }

    public static void setIsPersonalityScreenActive(boolean isPersonalityScreenActive)
    {
        WorldView.isPersonalityScreenActive = isPersonalityScreenActive;
    }

    @Override
    public void update(Long currentNanoTime)
    {
        String methodName = "update(Long) ";
        ArrayList<String> input = GameWindow.getInput();
        double elapsedTimeSinceLastInteraction = (currentNanoTime - lastTimeMenuWasOpened) / 1000000000.0;

        //Test Menu Hotkeys
        if (input.contains("T") && elapsedTimeSinceLastInteraction > 1)
            loadStage("test", "default");
        if (input.contains("Z") && elapsedTimeSinceLastInteraction > 1)
        {
            if (timeStartBump == null)//To set just once
                timeStartBump = currentNanoTime;
            bumpActive = true;
        }


        if (input.contains(KEYBOARD_INVENTORY) && elapsedTimeSinceLastInteraction > 1)
        {
            worldViewController.command(KEYBOARD_INVENTORY);
            isInventoryActive = !isInventoryActive;//toggle
            lastTimeMenuWasOpened = currentNanoTime;
        }

        //Process Input
        if (isTextBoxActive)
        {
            if (player.getActor().isMoving())
                player.getActor().setVelocity(0, 0);
            textbox.processKey(input, currentNanoTime);
        }
        else if (isPersonalityScreenActive)
        {
            if (player.getActor().isMoving())
                player.getActor().setVelocity(0, 0);
            personalityScreenController.processKey(input, currentNanoTime);
        }
        else if (isDaySummaryActive)
        {
            if (player.getActor().isMoving())
                player.getActor().setVelocity(0, 0);
            daySummaryScreenController.processKey(input, currentNanoTime);
        }
        else
            processInputAsMovement(input);
        processMouse(currentNanoTime);

        //Update Sprites
        player.update(currentNanoTime);
        for (Sprite active : activeSpritesLayer)
            active.update(currentNanoTime);

        //Remove Sprites
        for (Sprite sprite : toRemove)
        {
            WorldView.bottomLayer.remove(sprite);
            WorldView.middleLayer.remove(sprite);
            WorldView.topLayer.remove(sprite);
            WorldView.activeSpritesLayer.remove(sprite);
            WorldView.passiveSpritesLayer.remove(sprite);
            WorldView.passiveCollisionRelevantSpritesLayer.remove(sprite);
        }
        toRemove.clear();

        calcCameraPosition();
    }

    private void processInputAsMovement(ArrayList<String> input)
    {
        String methodName = "processInputAsMovement(ArrayList<String>()";
        boolean moveButtonPressed = false;
        int addedVelocityX = 0, addedVelocityY = 0;
        Direction newDirection = null;
        Actor playerActor = player.getActor();

        //Interpret Input from GameWindow
        if (input.contains("LEFT") || input.contains("A"))
        {
            addedVelocityX += -playerActor.getSpeed();
            moveButtonPressed = true;
            newDirection = Direction.WEST;
        }
        if (input.contains("RIGHT") || input.contains("D"))
        {
            addedVelocityX += playerActor.getSpeed();
            moveButtonPressed = true;
            newDirection = Direction.EAST;
        }
        if (input.contains("UP") || input.contains("W"))
        {
            addedVelocityY += -playerActor.getSpeed();
            moveButtonPressed = true;
            newDirection = Direction.NORTH;
        }
        if (input.contains("DOWN") || input.contains("S"))
        {
            addedVelocityY += playerActor.getSpeed();
            moveButtonPressed = true;
            newDirection = Direction.SOUTH;
        }

        if (moveButtonPressed)
            player.getActor().setVelocity(addedVelocityX, addedVelocityY);
        else if (player.getActor().isMoving())
            player.getActor().setVelocity(0, 0);

        if (newDirection != null && playerActor.getDirection() != newDirection)
            playerActor.setDirection(newDirection);

        if (input.contains("E"))
            player.setInteract(true);

    }

    private void processMouse(Long currentNanoTime)
    {
        String methodName = "processMouse() ";
        double screenWidth = GameWindow.getSingleton().getScreenWidth();
        double screenHeight = GameWindow.getSingleton().getScreenHeight();
        Point2D mousePosition = GameWindow.getSingleton().getMousePosition();
        Point2D mousePositionRelativeToCamera = new Point2D(mousePosition.getX() - (screenWidth - Config.CAMERA_WIDTH) / 2, mousePosition.getY() - (screenHeight - Config.CAMERA_HEIGHT) / 2);
        boolean isMouseClicked = GameWindow.getSingleton().isMouseClicked();

        //check for marked Sprites
        List<Sprite> mouseHoveredSprites = new ArrayList<>();
        for (Sprite active : activeSpritesLayer)//NOTE Send to Actor one time, not every Sprite, maybe with map that check already triggered actors
            if (active.intersectsRelativeToWorldView(mousePositionRelativeToCamera)
                    && active.getActor().getSensorStatus().getOnInteraction_TriggerSprite() != TriggerType.NOTHING)//Just add sprites of actors you can interact by onInteraction
                mouseHoveredSprites.add(active);

        if (isDiscussionGameActive)
        {
            discussionGame.processMouse(mousePositionRelativeToCamera, isMouseClicked, currentNanoTime);
        }
        else if (isPersonalityScreenActive)
        {
            personalityScreenController.processMouse(mousePositionRelativeToCamera, isMouseClicked, currentNanoTime);
        }
        else if (isTextBoxActive)
        {
            textbox.processMouse(mousePositionRelativeToCamera, isMouseClicked);
        }
        else if (isInventoryActive)
        {
            inventoryOverlay.processMouse(mousePositionRelativeToCamera, isMouseClicked, currentNanoTime);
        }
        else if (isDaySummaryActive)
        {
            daySummaryScreenController.processMouse(mousePositionRelativeToCamera, isMouseClicked, currentNanoTime);
        }
        else
        {
            for (Sprite clicked : mouseHoveredSprites)
                if (isMouseClicked)
                {
                    clicked.onClick(currentNanoTime);//Used onInteraction Trigger
                }
                else
                {
                    //System.out.println(classname + methodname + "Hovered over: " + clicked.getName());
                }
        }

        for (Sprite active : activeSpritesLayer)
            if (active.intersectsRelativeToWorldView(mousePositionRelativeToCamera) && DEBUG_MOUSE_ANALYSIS && active.getActor() != null && isMouseClicked)
            {
                Actor actor = active.getActor();
                System.out.println(actor.getActorInGameName() + ": " + actor.getSensorStatus().getStatusName() + " Sprite: " + actor.getGeneralStatus());
            }

        GameWindow.getSingleton().setMouseClicked(false);
    }

    private void calcCameraPosition()
    {
        String methodName = "calcCameraPosition() ";
        //Camera at world border
        camX = player.getPositionX() - CAMERA_WIDTH / 2f;
        camY = player.getPositionY() - CAMERA_HEIGHT / 2f;
        if (camX < offsetMinX)
            camX = offsetMinX;
        if (camY < offsetMinY)
            camY = offsetMinY;
        if (camX > offsetMaxX)
            camX = offsetMaxX;
        if (camY > offsetMaxY)
            camY = offsetMaxY;

        //If World smaller as Camera
        if (CAMERA_WIDTH > borders.getWidth())
            camX = borders.getWidth() / 2 - CAMERA_WIDTH / 2f;
        if (CAMERA_HEIGHT > borders.getHeight())
            camY = borders.getHeight() / 2 - CAMERA_HEIGHT / 2f;


        //Bump
        if (bumpActive)
        {
            double elapsedTimeSinceBump = (GameWindow.getSingleton().getRenderTime() - timeStartBump) / 1000000000.0;
            double offsetCamX = 0, offsetCamY = 0;
            if (durationBump < elapsedTimeSinceBump)
            {
                bumpActive = false;
                rumbleGrade = RUMBLE_GRADE;//Reset
                timeStartBump = null;//To manual retrigger
            }
            else
            {
                offsetCamX += Math.sin(bumpX) * rumbleGrade;
                offsetCamY += Math.cos(bumpY) * (rumbleGrade + 3);
                bumpX++;
                bumpY++;
                rumbleGrade = rumbleGrade - RUMBLE_GRADE_DECREASE;//Decreasing rumble
                rumbleGrade = Math.max(rumbleGrade, 0);
            }
            camX += offsetCamX;
            camY += offsetCamY;

        }

    }

    @Override
    public void render(Long currentNanoTime)
    {
        String methodName = "render(Long) ";

        gc.clearRect(0, 0, CAMERA_WIDTH, Config.CAMERA_HEIGHT);
        gc.translate(-camX, -camY);

        //Passiv Layer
        for (Sprite sprite : passiveSpritesLayer)
        {
            sprite.render(gc, currentNanoTime);
        }

        //Bottom heightLayer
        //bottomLayer.sort(new SpriteComparator());//To prevent wrong render sequence when sprites change layer or are added
        for (Sprite sprite : bottomLayer)
        {
            sprite.render(gc, currentNanoTime);
        }
        //Middle Layer
        middleLayer.sort(new SpriteComparator());//To prevent wrong render sequence when sprites change layer or are added
        for (Sprite sprite : middleLayer)
        {
            sprite.render(gc, currentNanoTime);
        }

        //Top Layer
        topLayer.sort(new SpriteComparator());//To prevent wrong render sequence when sprites change layer or are added
        for (Sprite sprite : topLayer)
        {
            sprite.render(gc, currentNanoTime);
        }

        //LightMap
        if (shadowColor != null)
        {
            renderLightEffect();
        }

        //Debugdata
        if (Config.DEBUG_BLOCKER)
        {
            gc.setStroke(Color.RED);
            gc.strokeRect(borders.getMinX(), borders.getMinY(), borders.getWidth() + player.getBasewidth(), borders.getHeight() + player.getBaseheight());
        }

        root.getChildren().clear();
        root.getChildren().add(worldCanvas);
        gc.translate(camX, camY);

        //Overlays
        if (isManagementAttentionMeterActive)
        {
            WritableImage writableImage = mamOverlay.getWritableImage();
            gc.drawImage(writableImage, mamOverlayPosition.getX(), mamOverlayPosition.getY());
        }
        if (isTextBoxActive)
        {
            WritableImage textBoxImg = textbox.showMessage();
            gc.drawImage(textBoxImg, textBoxPosition.getX(), textBoxPosition.getY());
        }
        if (isInventoryActive)
        {
            WritableImage inventoryOverlayMenuImage = inventoryOverlay.getMenuImage();
            gc.drawImage(inventoryOverlayMenuImage, inventoryOverlayPosition.getX(), inventoryOverlayPosition.getY());
        }
        if (isPersonalityScreenActive)
        {
            WritableImage personalityScreenOverlay = personalityScreenController.getWritableImage();
            gc.drawImage(personalityScreenOverlay, personalityScreenPosition.getX(), personalityScreenPosition.getY());
        }
        if (isDiscussionGameActive)
        {
            WritableImage discussionGameImage = discussionGame.getWritableImage(currentNanoTime);
            gc.drawImage(discussionGameImage, discussionGamePosition.getX(), discussionGamePosition.getY());
        }
        if (isDaySummaryActive)
        {
            WritableImage daySummaryImage = daySummaryScreenController.getWritableImage();
            gc.drawImage(daySummaryImage, daySummaryScreenPosition.getX(), daySummaryScreenPosition.getY());
        }

    }

    private void renderLightEffect()
    {
        ShadowMaskGc.setFill(shadowColor);
        ShadowMaskGc.fillRect(0, 0, CAMERA_WIDTH, Config.CAMERA_HEIGHT);
        for (Sprite sprite : passiveCollisionRelevantSpritesLayer)
        {
            if (sprite.getLightningSpriteName().toLowerCase().equals("none"))
                continue;

            String lightSpriteName = sprite.getLightningSpriteName();
            if (!lightsImageMap.containsKey(sprite.getLightningSpriteName()))
            {
                try
                {
                    lightsImageMap.put(lightSpriteName, new Image("/res/img/lightglows/" + lightSpriteName + ".png"));
                }
                catch (IllegalArgumentException e)
                {
                    System.out.println("Invalid URL: " + "/res/img/lightglows/" + lightSpriteName + ".png" + " of sprite " + sprite.getName());
                    continue;
                }
            }
            Image lightImage = lightsImageMap.get(lightSpriteName);
            ShadowMaskGc.drawImage(lightImage, sprite.getPositionX() + sprite.getHitBoxOffsetX() + sprite.getHitBoxWidth() / 2 - lightImage.getWidth() / 2 - camX, sprite.getPositionY() + sprite.getHitBoxOffsetY() + sprite.getHitBoxHeight() / 2 - lightImage.getHeight() / 2 - camY);
        }

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = shadowMask.snapshot(params, null);
        gc.setGlobalBlendMode(BlendMode.MULTIPLY);
        worldCanvas.getGraphicsContext2D().drawImage(image, camX, camY);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
    }

    public Pane load()
    {

        try
        {
            return fxmlLoader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Sprite> getPassiveCollisionRelevantSpritesLayer()
    {
        return passiveCollisionRelevantSpritesLayer;
    }

    public static List<Sprite> getMiddleLayer()
    {
        return middleLayer;
    }

    public static List<Sprite> getTopLayer()
    {
        return topLayer;
    }

    public static Rectangle2D getBorders()
    {
        return borders;
    }

    public static Sprite getPlayer()
    {
        return player;
    }

    public static WorldView getSingleton()
    {
        if (singleton == null)
            singleton = new WorldView(Config.FIRST_LEVEL);
        return singleton;
    }

    public static Point2D getInventoryOverlayPosition()
    {
        return inventoryOverlayPosition;
    }

    public static Point2D getTextBoxPosition()
    {
        return textBoxPosition;
    }

    public static Point2D getPersonalityScreenPosition()
    {
        return personalityScreenPosition;
    }


    public static void setIsTextBoxActive(boolean isTextBoxActive)
    {
        WorldView.isTextBoxActive = isTextBoxActive;
    }

    public static void setPersonalityScreenController(PersonalityScreenController personalityScreenController)
    {
        WorldView.personalityScreenController = personalityScreenController;
    }

    public static void setIsDiscussionGameActive(boolean isDiscussionGameActive)
    {
        WorldView.isDiscussionGameActive = isDiscussionGameActive;
    }

    public static void setDiscussionGame(DiscussionGame discussionGame)
    {
        WorldView.discussionGame = discussionGame;
    }

    public static Textbox getTextbox()
    {
        return textbox;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public static void setIsDaySummaryActive(boolean isDaySummaryActive)
    {
        if(isDaySummaryActive)
            daySummaryScreenController.newDay();
        WorldView.isDaySummaryActive = isDaySummaryActive;
    }

    public static String getCLASSNAME()
    {
        return CLASSNAME;
    }

    public Pane getRoot()
    {
        return root;
    }

    public FXMLLoader getFxmlLoader()
    {
        return fxmlLoader;
    }

    public Canvas getWorldCanvas()
    {
        return worldCanvas;
    }

    public GraphicsContext getGc()
    {
        return gc;
    }

    public Canvas getShadowMask()
    {
        return shadowMask;
    }

    public GraphicsContext getShadowMaskGc()
    {
        return ShadowMaskGc;
    }

    public Map<String, Image> getLightsImageMap()
    {
        return lightsImageMap;
    }

    public Color getShadowColor()
    {
        return shadowColor;
    }

    public static boolean isIsInventoryActive()
    {
        return isInventoryActive;
    }

    public static InventoryOverlay getInventoryOverlay()
    {
        return inventoryOverlay;
    }

    public static Long getLastTimeMenuWasOpened()
    {
        return lastTimeMenuWasOpened;
    }

    public static boolean isIsTextBoxActive()
    {
        return isTextBoxActive;
    }

    public static boolean isIsPersonalityScreenActive()
    {
        return isPersonalityScreenActive;
    }

    public static PersonalityScreenController getPersonalityScreenController()
    {
        return personalityScreenController;
    }

    public static boolean isIsDiscussionGameActive()
    {
        return isDiscussionGameActive;
    }

    public static DiscussionGame getDiscussionGame()
    {
        return discussionGame;
    }

    public static Point2D getDiscussionGamePosition()
    {
        return discussionGamePosition;
    }

    public static boolean isIsDaySummaryActive()
    {
        return isDaySummaryActive;
    }

    public static DaySummaryScreenController getDaySummaryScreenController()
    {
        return daySummaryScreenController;
    }

    public static Point2D getDaySummaryScreenPosition()
    {
        return daySummaryScreenPosition;
    }

    public static boolean isIsManagementAttentionMeterActive()
    {
        return isManagementAttentionMeterActive;
    }

    public static StatusBarOverlay getMamOverlay()
    {
        return mamOverlay;
    }

    public static Point2D getMamOverlayPosition()
    {
        return mamOverlayPosition;
    }

    public static List<Sprite> getActiveSpritesLayer()
    {
        return activeSpritesLayer;
    }

    public static List<Sprite> getPassiveSpritesLayer()
    {
        return passiveSpritesLayer;
    }

    public static List<Sprite> getBottomLayer()
    {
        return bottomLayer;
    }

    public static Map<String, WorldLoader.SpawnData> getSpawnPointsMap()
    {
        return spawnPointsMap;
    }

    public static List<Sprite> getToRemove()
    {
        return toRemove;
    }

    public double getOffsetMaxX()
    {
        return offsetMaxX;
    }

    public double getOffsetMaxY()
    {
        return offsetMaxY;
    }

    public int getOffsetMinX()
    {
        return offsetMinX;
    }

    public int getOffsetMinY()
    {
        return offsetMinY;
    }

    public static double getCamX()
    {
        return camX;
    }

    public static double getCamY()
    {
        return camY;
    }

    public double getBumpX()
    {
        return bumpX;
    }

    public double getBumpY()
    {
        return bumpY;
    }

    public double getRumbleGrade()
    {
        return rumbleGrade;
    }

    public Long getTimeStartBump()
    {
        return timeStartBump;
    }

    public float getDurationBump()
    {
        return durationBump;
    }

    public boolean isBumpActive()
    {
        return bumpActive;
    }
}
