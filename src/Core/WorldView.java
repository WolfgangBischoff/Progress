package Core;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldView implements GUIController
{
    private static Rectangle2D borders;
    static List<Sprite> activeLayers = new ArrayList<>();
    static List<Sprite> passivLayer = new ArrayList<>();
    static List<Sprite> bottomLayer = new ArrayList<>();
    static List<Sprite> middleLayer = new ArrayList<>();
    static List<Sprite> topLayer = new ArrayList<>();
    String levelName;
    Sprite player;

    double VIEWPORT_SIZE_X = Config.CAMERA_WIDTH;
    double VIEWPORT_SIZE_Y = Config.CAMERA_HEIGTH;
    double offsetMaxX;
    double offsetMaxY;
    int offsetMinX = 0;
    int offsetMinY = 0;
    double camX;
    double camY;

    Pane root;
    Canvas worldCanvas;
    GraphicsContext gc;
    Canvas shadowMask;
    GraphicsContext ShadowMaskGc;
    Map<String, Image> lightsImageMap = new HashMap<>();
    Color shadowColor;

    public WorldView(String levelName, Pane root)
    {
        this.root = root;
        worldCanvas = new Canvas(VIEWPORT_SIZE_X, VIEWPORT_SIZE_Y);
        shadowMask = new Canvas(VIEWPORT_SIZE_X, VIEWPORT_SIZE_Y);
        gc = worldCanvas.getGraphicsContext2D();
        ShadowMaskGc = shadowMask.getGraphicsContext2D();
        this.levelName = levelName;

        loadEnvironment();
        offsetMaxX = borders.getMaxX() - VIEWPORT_SIZE_X;
        offsetMaxY = borders.getMaxY() - VIEWPORT_SIZE_Y;
    }

    private void loadEnvironment()
    {
        WorldLoader worldLoader = new WorldLoader(levelName);
        worldLoader.load();
        player = worldLoader.getPlayer();
        passivLayer = worldLoader.getPassivLayer();
        bottomLayer = worldLoader.getBttmLayer();
        middleLayer = worldLoader.getMediumLayer();
        topLayer = worldLoader.getUpperLayer();

        activeLayers.addAll(bottomLayer);
        activeLayers.addAll(middleLayer);
        activeLayers.addAll(topLayer);

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
        //player.setVelocity(0, 0);

        //Interpret Input from GameWindow
        if (input.contains("UP") || input.contains("W"))
        {
            //player.addVelocity(0, -player.getSpeed());
            addedVelocityY += -player.getSpeed();
            if (player.getDirection() != Direction.NORTH)
                player.setDirection(Direction.NORTH);
            moveButtonPressed = true;
        }
        if (input.contains("DOWN") || input.contains("S"))
        {
            //player.addVelocity(0, player.getSpeed());
            addedVelocityY += player.getSpeed();
            if (player.getDirection() != Direction.SOUTH)
                player.setDirection(Direction.SOUTH);
            moveButtonPressed = true;
        }
        if (input.contains("LEFT") || input.contains("A"))
        {
            //player.addVelocity(-player.getSpeed(), 0);
            addedVelocityX += -player.getSpeed();
            if (player.getDirection() != Direction.WEST)
                player.setDirection(Direction.WEST);
            moveButtonPressed = true;
        }
        if (input.contains("RIGHT") || input.contains("D"))
        {
            //player.addVelocity(player.getSpeed(), 0);
            addedVelocityX += player.getSpeed();
            if (player.getDirection() != Direction.EAST)
                player.setDirection(Direction.EAST);
            moveButtonPressed = true;
        }

        if (moveButtonPressed)
            player.setVelocity(addedVelocityX, addedVelocityY);
        else if(player.isMoving())
            player.setVelocity(0, 0);

        if (input.contains("E"))
        {
            player.setInteract(true);
        }

        player.update(currentNanoTime);

        //Camera at world border
        camX = player.positionX - VIEWPORT_SIZE_X / 2;
        camY = player.positionY - VIEWPORT_SIZE_Y / 2;
        if (camX < offsetMinX)
            camX = offsetMinX;
        if (camY < offsetMinY)
            camY = offsetMinY;
        if (camX > offsetMaxX)
            camX = offsetMaxX;
        if (camY > offsetMaxY)
            camY = offsetMaxY;

        //If World smaller as Camera
        if (VIEWPORT_SIZE_X > borders.getWidth())
            camX = borders.getWidth() / 2 - VIEWPORT_SIZE_X / 2;
        if (VIEWPORT_SIZE_Y > borders.getHeight())
            camY = borders.getHeight() / 2 - VIEWPORT_SIZE_Y / 2;


    }

    @Override
    public void render(Long currentNanoTime)
    {

        gc.clearRect(0, 0, VIEWPORT_SIZE_X, VIEWPORT_SIZE_Y);
        gc.translate(-camX, -camY);

        //Passiv Layer
        for (Sprite sprite : passivLayer)
        {
            sprite.render(gc, currentNanoTime);
        }

        //Bottom priority
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

        if (Config.DEBUGMODE)
        {
            gc.setStroke(Color.RED);
            gc.strokeRect(borders.getMinX(), borders.getMinY(), borders.getWidth() + player.basewidth, borders.getHeight() + player.baseheight);
        }

        //LightMap
        if (shadowColor != null)
        {
            ShadowMaskGc.setFill(shadowColor);
            ShadowMaskGc.fillRect(0, 0, VIEWPORT_SIZE_X, VIEWPORT_SIZE_Y);
            for (Sprite sprite : activeLayers)
            {
                if (sprite.getLightningSpriteName().toLowerCase().equals("none"))
                    continue;

                String lightSpriteName = sprite.getLightningSpriteName();
                if (!lightsImageMap.containsKey(sprite.getLightningSpriteName()))
                    lightsImageMap.put(lightSpriteName, new Image("/res/img/lightglows/" + lightSpriteName + ".png"));
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


        root.getChildren().clear();
        root.getChildren().add(worldCanvas);

        gc.translate(camX, camY);
    }


    @Override
    public Pane load()
    {
        return null;
    }

    public static List<Sprite> getActiveLayers()
    {
        return activeLayers;
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
}
