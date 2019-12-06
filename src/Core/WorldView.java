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
        leveldata = Utilities.readAllLineFromTxt("src/res/level/" + levelName + ".csv", false);
        for (int i = 0; i < leveldata.size(); i++)
        {
            String[] arr = leveldata.get(i);
            for (int j = 0; j < arr.length; j++)
            {
                System.out.print(arr[j] + " ");
                //TODO Create Actor from leveldata
                if (arr[0].equals("Player"))
                {
                    player = new Person().getSprite();
                    player.setPosition(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                }
                if (arr[0].equals("carrot"))
                {
                    Sprite ca = new Sprite();
                    ca.setImage(new Image("/res/img/carrot.png"));
                    ca.setPosition(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                    sprites.add(ca);
                }
            }
            System.out.println();
        }

        background = new Image("/res/img/background.jpg");
        borders = new Rectangle2D(0, 0, background.getWidth(), background.getHeight());

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
    public void update(Double elapsedTime)
    {
        // game logic
        ArrayList<String> input = GameWindow.getInput();
        player.setVelocity(0, 0);
        if (input.contains("LEFT") || input.contains("A"))
            player.addVelocity(-50, 0);
        if (input.contains("RIGHT") || input.contains("D"))
            player.addVelocity(50, 0);
        if (input.contains("UP") || input.contains("W"))
            player.addVelocity(0, -50);
        if (input.contains("DOWN") || input.contains("S"))
            player.addVelocity(0, 50);

        player.update(elapsedTime);
    }

    @Override
    public void render(Double elapsedTime)
    {
        gc.clearRect(0, 0, 512, 512);
        gc.drawImage(background, 0, 0);
        for (Sprite sprite : sprites)
            sprite.render(gc);
        player.render(gc);
    }

    @Override
    public Pane load()
    {
        return null;
    }
}
