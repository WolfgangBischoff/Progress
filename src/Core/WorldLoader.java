package Core;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorldLoader
{
    private Rectangle2D borders;
    List<Sprite> sprites = new ArrayList<>();
    String levelName;
    GraphicsContext gc;
    List<String[]> leveldata = new ArrayList<>();
    Sprite player;
    Image background;
    int numberTileLine = 0;

    public WorldLoader(String stageName)
    {
        levelName = stageName;
    }

    public void load()
    {
        leveldata = Utilities.readAllLineFromTxt("src/res/level/" + levelName + ".csv");
        String readMode = null;

        for (int i = 0; i < leveldata.size(); i++)
        {
            String[] lineData = leveldata.get(i);

            //Check for keyword
            Set<String> keywords = new HashSet<>();
            keywords.add("background:");
            keywords.add("tiles:");
            keywords.add("actors:");
            if (keywords.contains(lineData[0]))
            {
                readMode = lineData[0];
                continue;
            }

            //process line according to keyword
            switch (readMode)
            {
                case "background:":
                    readBackground(lineData);
                    continue;
                case "tiles:":
                    readTile(lineData);
                    continue;
                case "actors:":
                    readActors(lineData);
                    continue;

            }
        }

    }

    private void readActors(String[] lineData)
    {
        //TODO Read Actors List

        Sprite ca;

        //TODO lookup what sprites are animated
        if (Boolean.parseBoolean(lineData[5]))
            ca = new Sprite("diffuserSmokeSprites", 5, 6, 6, 1, 120, 140);
        else
            ca = new Sprite(lineData[0]);
        ca.setName(lineData[0]);
        ca.setPosition(Integer.parseInt(lineData[1]), Integer.parseInt(lineData[2]));
        ca.setBlocker(Boolean.parseBoolean(lineData[3]));
        ca.setSpeed(Double.valueOf(lineData[4]));
        ca.setAnimated(Boolean.parseBoolean(lineData[5]));

        //name of Player sprite
        if (lineData[0].equals("bee"))
            player = ca;
        else
            sprites.add(ca);
        System.out.println(ca);
    }

    private void readBackground(String[] lineData)
    {
        background = new Image("/res/img/" + lineData[0] + ".jpg");
        borders = new Rectangle2D(0, 0, background.getWidth(), background.getHeight());
    }

    private void readTile(String[] lineData)
    {

        for (int i = 0; i < lineData.length; i++)
        {
            System.out.print(lineData[i] + " ");
            //TODO Check type
            //TODO Create Sprite
            if (lineData[i].equals("1"))
            {
                Sprite ca;
                ca = new Sprite("carrot");
                ca.setName("carrot");
                ca.setPosition(i * 64, numberTileLine * 64);
                ca.setBlocker(true);
                ca.setAnimated(false);
                sprites.add(ca);
            }
        }
        System.out.print("\n");
        numberTileLine++;

    }


    public Rectangle2D getBorders()
    {
        return borders;
    }

    public List<Sprite> getSprites()
    {
        return sprites;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public GraphicsContext getGc()
    {
        return gc;
    }

    public List<String[]> getLeveldata()
    {
        return leveldata;
    }

    public Sprite getPlayer()
    {
        return player;
    }

    public Image getBackground()
    {
        return background;
    }
}
