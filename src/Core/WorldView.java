package Core;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        leveldata = Utilities.readAllLineFromTxt("src/res/level/" + levelName + ".csv", true);
        for (int i = 0; i < leveldata.size(); i++)
        {
            String[] entityData = leveldata.get(i);

            Sprite ca;
            if(Boolean.parseBoolean(entityData[5]))
                ca = new AnimatedSprite(null, "diffuserSmokeSprites", 5, 6, 6, 1, 120, 140);
            else
                ca = new Sprite(entityData[0]);
            ca.setName(entityData[0]);
            //ca.setImage(new Image("/res/img/" + entityData[0] + ".png"));
            ca.setPosition(Integer.parseInt(entityData[1]), Integer.parseInt(entityData[2]));
            ca.setBlocker(Boolean.parseBoolean(entityData[3]));
            ca.setSpeed(Double.valueOf(entityData[4]));
            ca.setAnimated(Boolean.parseBoolean(entityData[5]));

            //name of Player sprite
            if (entityData[0].equals("bee"))
                player = ca;
            else
                sprites.add(ca);
            System.out.println(ca);
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
            player.addVelocity(-player.getSpeed(), 0);
        if (input.contains("RIGHT") || input.contains("D"))
            player.addVelocity(player.getSpeed(), 0);
        if (input.contains("UP") || input.contains("W"))
            player.addVelocity(0, -player.getSpeed());
        if (input.contains("DOWN") || input.contains("S"))
            player.addVelocity(0, player.getSpeed());

        player.update(elapsedTime);
    }

    @Override
    public void render(Double elapsedTime)
    {
        gc.clearRect(0, 0, 512, 512);
        gc.drawImage(background, 0, 0);
        for (Sprite sprite : sprites)
        {
            if(sprite.getAnimated())
            {
                ((AnimatedSprite)sprite).render(gc, elapsedTime);
            }
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
