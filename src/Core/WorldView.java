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
    static List<Sprite> sprites = new ArrayList<>();
    String levelName;
    GraphicsContext gc;
    List<String[]> leveldata = new ArrayList<>();
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
        sprites = worldLoader.getSprites();
        background = worldLoader.getBackground();
        borders = worldLoader.getBorders();
    }

    public static Rectangle2D getBorders()
    {
        return borders;
    }

    public static List<Sprite> getSprites()
    {
        return sprites;
    }

    @Override
    public void update(Long currentNanoTime)
    {
        // game logic
        ArrayList<String> input = GameWindow.getInput();
        //System.out.println("WorldView update: " + input.toString());

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
        if(input.contains("E") || input.contains("SPACE"))
        {
            player.setInteract(true);
        }

        player.update(currentNanoTime);

        //Update other chars if exist
    }

    @Override
    public void render(Long currentNanoTime)
    {
        gc.clearRect(0, 0, 512, 512);
        gc.drawImage(background, 0, 0);

        //Non Player Sprites
        for (Sprite sprite : sprites)
        {
            if(sprite.getAnimated())
                (sprite).render(gc, currentNanoTime);
            else
                sprite.render(gc);
        }

        player.render(gc);
    }

    @Override
    public Pane load()
    {
        return null;
    }
}
