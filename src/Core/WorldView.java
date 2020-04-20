package Core;

import Core.Enums.Direction;
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
import java.util.*;

import static Core.Config.CAMERA_HEIGHT;
import static Core.Config.CAMERA_WIDTH;

public class WorldView implements GUIController
{
    private static final String CLASSNAME = "WorldView-";

    @FXML
    Pane root;

    FXMLLoader fxmlLoader;

    Canvas worldCanvas;
    GraphicsContext gc;
    Canvas shadowMask;
    GraphicsContext ShadowMaskGc;
    Map<String, Image> lightsImageMap = new HashMap<>();
    Color shadowColor;

    //Menu overlay
    static boolean isMenuActive = false;
    static MenuOverlay menuOverlay;
    static Point2D menuOverlayPosition = new Point2D(CAMERA_WIDTH / 2f - menuOverlay.getMenuWidth() / 2, CAMERA_HEIGHT - menuOverlay.getMenuHeight());
    static Long lastTimeMenuWasOpened = 0l;

    //Textbox
    static boolean isTextBoxActive = false;
    static Textbox textbox;
    static Point2D textboxPosition;

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
    static List<Sprite> toRemove = new ArrayList<>();

    //Camera
    double offsetMaxX;
    double offsetMaxY;
    int offsetMinX = 0;
    int offsetMinY = 0;
    static double camX;
    static double camY;

    public WorldView(String levelName)
    {
        fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PlayerView.fxml"));
        fxmlLoader.setController(this);

        worldCanvas = new Canvas(CAMERA_WIDTH, Config.CAMERA_HEIGHT);
        shadowMask = new Canvas(CAMERA_WIDTH, Config.CAMERA_HEIGHT);
        gc = worldCanvas.getGraphicsContext2D();
        ShadowMaskGc = shadowMask.getGraphicsContext2D();
        this.levelName = levelName;

        loadEnvironment();
        offsetMaxX = borders.getMaxX() - CAMERA_WIDTH;
        offsetMaxY = borders.getMaxY() - Config.CAMERA_HEIGHT;

        menuOverlay = new MenuOverlay();
        textbox = new Textbox();
        textboxPosition  = new Point2D(CAMERA_WIDTH / 2f - textbox.getTEXTBOX_WIDTH() / 2, CAMERA_HEIGHT - textbox.getTEXTBOX_HEIGHT() - 32);
    }

    private void loadEnvironment()
    {
        WorldLoader worldLoader = new WorldLoader(levelName);
        worldLoader.load();
        player = worldLoader.getPlayer();
        passiveSpritesLayer = worldLoader.getPassivLayer(); //No collision just render
        activeSpritesLayer = worldLoader.activeLayer;
        bottomLayer = worldLoader.getBttmLayer(); //Render height
        middleLayer = worldLoader.getMediumLayer();
        topLayer = worldLoader.getUpperLayer();

        passiveCollisionRelevantSpritesLayer.addAll(bottomLayer); //For passive collision check
        passiveCollisionRelevantSpritesLayer.addAll(middleLayer);
        passiveCollisionRelevantSpritesLayer.addAll(topLayer);

        borders = worldLoader.getBorders();
        shadowColor = worldLoader.getShadowColor();
    }

    @Override
    public void update(Long currentNanoTime)
    {
        String methodName = "update(Long)";
        ArrayList<String> input = GameWindow.getInput();


        double elapsedTimeSinceLastInteraction = (currentNanoTime - lastTimeMenuWasOpened) / 1000000000.0;
        if (input.contains("ESCAPE") && elapsedTimeSinceLastInteraction > 1)
        {
            if (!isMenuActive)
            {
                isMenuActive = true;
            }
            else
                isMenuActive = false;
            lastTimeMenuWasOpened = currentNanoTime;
        }

        //Process Input
        if (isTextBoxActive)
        {
            if (player.actor.isMoving())
                player.actor.setVelocity(0, 0);
            textbox.processKey(input, currentNanoTime);
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
        Actor playerActor = player.actor;

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
            player.actor.setVelocity(addedVelocityX, addedVelocityY);
        else if (player.actor.isMoving())
            player.actor.setVelocity(0, 0);

        if (newDirection != null && playerActor.getDirection() != newDirection)
            playerActor.setDirection(newDirection);

        if (input.contains("E"))
            player.setInteract(true);

    }

    private void processMouse(Long currentNanoTime)
    {
        String methodName = "processMouse()";
        double screenWidth = GameWindow.getSingleton().getScreenWidth();
        double screenHeight = GameWindow.getSingleton().getScreenHeight();
        Point2D mousePosition = GameWindow.getSingleton().mousePosition;
        Point2D mousePositionRelativeToCamera = new Point2D(mousePosition.getX() - (screenWidth - Config.CAMERA_WIDTH) / 2, mousePosition.getY() - (screenHeight - Config.CAMERA_HEIGHT) / 2);
        boolean isMouseClicked = GameWindow.getSingleton().mouseClicked;

        //check for marked Sprites
        List<Sprite> mouseHoveredSprites = new ArrayList<>();
        //TODO Send to Actor one time, not every Sprite
        for (Sprite active : activeSpritesLayer)
            if (active.intersectsRelativeToWorldView(mousePositionRelativeToCamera))
                mouseHoveredSprites.add(active);


        if (isTextBoxActive)
        {
            textbox.processMouse(mousePositionRelativeToCamera, isMouseClicked);
        }
        else
        {
            for (Sprite clicked : mouseHoveredSprites)
                if (isMouseClicked)
                {
                    clicked.onClick(currentNanoTime);
                }
                else
                {
                    //System.out.println(classname + methodname + "Hovered over: " + clicked.getName());
                }
        }

        GameWindow.getSingleton().mouseClicked = false;


    }

    private void calcCameraPosition()
    {
        //Camera at world border
        camX = player.positionX - CAMERA_WIDTH / 2f;
        camY = player.positionY - CAMERA_HEIGHT / 2f;
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
        if (Config.CAMERA_HEIGHT > borders.getHeight())
            camY = borders.getHeight() / 2 - CAMERA_HEIGHT / 2f;
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
            gc.strokeRect(borders.getMinX(), borders.getMinY(), borders.getWidth() + player.basewidth, borders.getHeight() + player.baseheight);
        }

        root.getChildren().clear();
        root.getChildren().add(worldCanvas);

        gc.translate(camX, camY);

        //Overlays
        if (isTextBoxActive)
        {
            WritableImage textBoxImg = textbox.showMessage();
            gc.drawImage(textBoxImg, textboxPosition.getX(), textboxPosition.getY());
        }

        if(isMenuActive)
        {
            WritableImage menuImage = menuOverlay.getMenuImage();
            gc.drawImage(menuImage, 250, 250);
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
                } catch (IllegalArgumentException e)
                {
                    throw new IllegalArgumentException("Invalid URL: " + "/res/img/lightglows/" + lightSpriteName + ".png" + " of sprite " + sprite.getName());
                }
            }
            Image lightimage = lightsImageMap.get(lightSpriteName);
            ShadowMaskGc.drawImage(lightimage, sprite.positionX + sprite.getHitBoxOffsetX() + sprite.getHitBoxWidth() / 2 - lightimage.getWidth() / 2 - camX, sprite.positionY + sprite.getHitBoxOffsetY() + sprite.getHitBoxHeight() / 2 - lightimage.getHeight() / 2 - camY);
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
        } catch (IOException e)
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
}
