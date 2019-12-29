package Core;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class WorldView implements GUIController
{
    private static Rectangle2D borders;
    static List<Sprite> allLayers = new ArrayList<>();
    static List<Sprite> bottomLayer = new ArrayList<>();
    static List<Sprite> middleLayer = new ArrayList<>();
    static List<Sprite> topLayer = new ArrayList<>();
    String levelName;
    GraphicsContext gc;
    Sprite player;
    //Image background;

    double VIEWPORT_SIZE_X = Config.CAMERA_WIDTH;
    double VIEWPORT_SIZE_Y = Config.CAMERA_HEIGTH;
    double offsetMaxX;
    double offsetMaxY;
    int offsetMinX = 0;
    int offsetMinY = 0;
    double camX;
    double camY;

    public WorldView(String levelName, Canvas worldCanvas)
    {
        this.levelName = levelName;
        loadEnvironment();
        worldCanvas.setWidth(VIEWPORT_SIZE_X);
        worldCanvas.setHeight(VIEWPORT_SIZE_Y);
        gc = worldCanvas.getGraphicsContext2D();
        offsetMaxX = borders.getMaxX() - VIEWPORT_SIZE_X;
        offsetMaxY = borders.getMaxY() - VIEWPORT_SIZE_Y;
    }

    private void loadEnvironment()
    {
        WorldLoader worldLoader = new WorldLoader(levelName);
        worldLoader.load();
        player = worldLoader.getPlayer();
        bottomLayer = worldLoader.getBttmLayer();
        middleLayer = worldLoader.getMediumLayer();
        topLayer = worldLoader.getUpperLayer();
        allLayers.addAll(bottomLayer);
        allLayers.addAll(middleLayer);
        allLayers.addAll(topLayer);

        //background = worldLoader.getBackground();
        borders = worldLoader.getBorders();
    }


    @Override
    public void update(Long currentNanoTime)
    {
        // game logic
        ArrayList<String> input = GameWindow.getInput();
        player.setVelocity(0, 0);

        //Interpret Input from GameWindow
        if (input.contains("LEFT") || input.contains("A"))
        {
            player.addVelocity(-player.getSpeed(), 0);
            player.setDirection(Direction.WEST);
        }
        if (input.contains("RIGHT") || input.contains("D"))
        {
            player.addVelocity(player.getSpeed(), 0);
            player.setDirection(Direction.EAST);
        }
        if (input.contains("UP") || input.contains("W"))
        {
            player.addVelocity(0, -player.getSpeed());
            player.setDirection(Direction.NORTH);
        }
        if (input.contains("DOWN") || input.contains("S"))
        {
            player.addVelocity(0, player.getSpeed());
            player.setDirection(Direction.SOUTH);
        }
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
        if(VIEWPORT_SIZE_X > borders.getWidth())
            camX = borders.getWidth() / 2 - VIEWPORT_SIZE_X /2;
        if(VIEWPORT_SIZE_Y > borders.getHeight())
            camY = borders.getHeight() / 2 - VIEWPORT_SIZE_Y / 2;

    }

    @Override
    public void render(Long currentNanoTime)
    {
        gc.clearRect(0, 0, VIEWPORT_SIZE_X, VIEWPORT_SIZE_Y);
        gc.translate(-camX, -camY);

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
        gc.translate(camX, camY);
    }

    @Override
    public Pane load()
    {
        return null;
    }

    public static List<Sprite> getAllLayers()
    {
        return allLayers;
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
