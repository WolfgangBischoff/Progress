package Core;

import Core.Enums.Direction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import static Core.Config.CAMERA_HEIGTH;
import static Core.Config.CAMERA_WIDTH;

public class WorldView implements GUIController
{
    @FXML
    Pane root;
    FXMLLoader fxmlLoader;

    Canvas worldCanvas;
    GraphicsContext gc;
    Canvas shadowMask;
    GraphicsContext ShadowMaskGc;
    Map<String, Image> lightsImageMap = new HashMap<>();
    Color shadowColor;

    //Textbox
    boolean isTextBoxActive = false;
    Textbox textbox = new Textbox();
    String textboxIdentifier;

    //Sprites
    private static Rectangle2D borders;
    static List<Sprite> activeSpritesLayer = new ArrayList<>();
    static List<Sprite> passiveCollisionRelevantSpritesLayer = new ArrayList<>();
    static List<Sprite> passiveSpritesLayer = new ArrayList<>();
    static List<Sprite> bottomLayer = new ArrayList<>();
    static List<Sprite> middleLayer = new ArrayList<>();
    static List<Sprite> topLayer = new ArrayList<>();
    String levelName;
    static Sprite player;

    //Camera
    double offsetMaxX;
    double offsetMaxY;
    int offsetMinX = 0;
    int offsetMinY = 0;
    double camX;
    double camY;

    //public WorldView(String levelName, Pane root)
    public WorldView(String levelName)
    {
        fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PlayerView.fxml"));
        fxmlLoader.setController(this);

        //this.root = root;
        worldCanvas = new Canvas(CAMERA_WIDTH, Config.CAMERA_HEIGTH);
        shadowMask = new Canvas(CAMERA_WIDTH, Config.CAMERA_HEIGTH);
        gc = worldCanvas.getGraphicsContext2D();
        ShadowMaskGc = shadowMask.getGraphicsContext2D();
        this.levelName = levelName;

        loadEnvironment();
        offsetMaxX = borders.getMaxX() - CAMERA_WIDTH;
        offsetMaxY = borders.getMaxY() - Config.CAMERA_HEIGTH;
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
        // game logic
        ArrayList<String> input = GameWindow.getInput();
        boolean moveButtonPressed = false;
        int addedVelocityX = 0, addedVelocityY = 0;
        Actor playerActor = player.actor;

        //Interpret Input from GameWindow
        if (input.contains("UP") || input.contains("W"))
        {
            addedVelocityY += -playerActor.getSpeed();
            moveButtonPressed = true;
            if (playerActor.getDirection() != Direction.NORTH)
                playerActor.setDirection(Direction.NORTH);
        }
        if (input.contains("DOWN") || input.contains("S"))
        {
            addedVelocityY += playerActor.getSpeed();
            moveButtonPressed = true;
            if (playerActor.getDirection() != Direction.SOUTH)
                playerActor.setDirection(Direction.SOUTH);
        }
        if (input.contains("LEFT") || input.contains("A"))
        {
            addedVelocityX += -playerActor.getSpeed();
            moveButtonPressed = true;
            if (playerActor.getDirection() != Direction.WEST)
                playerActor.setDirection(Direction.WEST);
        }
        if (input.contains("RIGHT") || input.contains("D"))
        {
            addedVelocityX += playerActor.getSpeed();
            moveButtonPressed = true;
            if (playerActor.getDirection() != Direction.EAST)
                playerActor.setDirection(Direction.EAST);
        }

        if (moveButtonPressed)
            player.actor.setVelocity(addedVelocityX, addedVelocityY);
        else if (player.actor.isMoving())
            player.actor.setVelocity(0, 0);

        if (input.contains("E"))
        {
            if (isTextBoxActive)
                textbox.nextMessage(currentNanoTime);
            else
                player.setInteract(true);
        }


        //Todo set/unset textbox Visible from Actors
        player.update(currentNanoTime);

        for (Sprite active : activeSpritesLayer)
            active.update(currentNanoTime);

        //Camera at world border
        camX = player.positionX - CAMERA_WIDTH / 2;
        camY = player.positionY - CAMERA_HEIGTH / 2;
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
            camX = borders.getWidth() / 2 - CAMERA_WIDTH / 2;
        if (Config.CAMERA_HEIGTH > borders.getHeight())
            camY = borders.getHeight() / 2 - Config.CAMERA_HEIGTH / 2;


    }

    @Override
    public void render(Long currentNanoTime)
    {

        gc.clearRect(0, 0, CAMERA_WIDTH, Config.CAMERA_HEIGTH);
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
            ShadowMaskGc.setFill(shadowColor);
            ShadowMaskGc.fillRect(0, 0, CAMERA_WIDTH, Config.CAMERA_HEIGTH);
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

        //Debugdata
        if (Config.DEBUGMODE)
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
            //WritableImage textBoxImg = textbox.showMessage(textboxIdentifier);
            WritableImage textBoxImg = textbox.showMessage();
            gc.drawImage(textBoxImg, CAMERA_WIDTH / 2 - textBoxImg.getWidth() / 2, CAMERA_HEIGTH - textBoxImg.getHeight() - 32);
        }
    }


    @Override
    /*public Pane load()
    {
        return null;
    }*/

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
}
