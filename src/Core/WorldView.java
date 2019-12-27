package Core;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
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
    Image background;

    public WorldView(String levelName, GraphicsContext graphicsContext)
    {
        gc = graphicsContext;
        this.levelName = levelName;
        loadEnvironment();
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

        background = worldLoader.getBackground();
        borders = worldLoader.getBorders();
    }

    public static Rectangle2D getBorders()
    {
        return borders;
    }

    public static List<Sprite> getBottomLayer()
    {
        return bottomLayer;
    }

    @Override
    public void update(Long currentNanoTime)
    {
        // game logic
        ArrayList<String> input = GameWindow.getInput();
        //System.out.println("WorldView update: " + input.toString());

        player.setVelocity(0, 0);
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
        if(input.contains("E"))
        {
            player.setInteract(true);
        }

        player.update(currentNanoTime);
    }

    @Override
    public void render(Long currentNanoTime)
    {
        gc.clearRect(0, 0, 512, 512);
        //Background
        gc.drawImage(background, 0, 0);

        //Bottom layer
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
}
