package Core;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.*;

public class WorldLoader
{
    private Rectangle2D borders;
    List<Sprite> sprites = new ArrayList<>();
    String levelName;
    GraphicsContext gc;
    List<String[]> leveldata = new ArrayList<>();
    Sprite player;
    Image background;
    Map<String, TileData> tileDataMap = new HashMap<>();
    int numberTileLine = 0;

    private final String KEYWORD_BACKGROUND = "background:";
    private final String KEYWORD_TILES = "tiles:";

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
            keywords.add(KEYWORD_BACKGROUND);
            keywords.add(KEYWORD_TILES);
            keywords.add("actors:");
            keywords.add("tiledefinition:");
            if (keywords.contains(lineData[0]))
            {
                readMode = lineData[0];
                continue;
            }

            //process line according to keyword
            switch (readMode)
            {
                case KEYWORD_BACKGROUND:
                    readBackground(lineData);
                    continue;
                case "tiledefinition:":
                    tileDefinition(lineData);
                    continue;
                case KEYWORD_TILES:
                    readTile(lineData);
                    continue;
                case "actors:":
                    readActors(lineData);
                    continue;

            }
        }

        //System.out.println("WorldLoader: " + tileDataMap);

    }

    private void tileDefinition(String[] lineData)
    {
        Boolean blocking = Boolean.parseBoolean(lineData[2]);
        Integer fps = Integer.parseInt(lineData[3]);
        Integer totalFrames = Integer.parseInt(lineData[4]);
        Integer cols = Integer.parseInt(lineData[5]);
        Integer rows = Integer.parseInt(lineData[6]);
        Integer frameWidth = Integer.parseInt(lineData[7]);
        Integer frameHeight = Integer.parseInt(lineData[8]);
        Integer velocity = Integer.parseInt(lineData[9]);
        Integer direction = Integer.parseInt(lineData[10]);
        TileData current = new TileData(lineData[1], blocking, fps, totalFrames, cols, rows, frameWidth, frameHeight, velocity, direction);
        tileDataMap.put(lineData[0], current);
    }

    class TileData
    {
        public String spritename;
        public Boolean blocking;
        public Integer fps, totalFrames, cols, rows, frameWidth, frameHeight, velocity,direction;

        public TileData(String spritename, Boolean blocking, Integer fps, Integer totalFrames, Integer cols, Integer rows, Integer frameWidth, Integer frameHeight, Integer velocity, Integer direction)
        {
            this.spritename = spritename;
            this.blocking = blocking;
            this.fps = fps;
            this.totalFrames = totalFrames;
            this.cols = cols;
            this.rows = rows;
            this.frameWidth = frameWidth;
            this.frameHeight = frameHeight;
            this.velocity = velocity;
            this.direction = direction;
        }
    }

    private void readActors(String[] lineData)
    {
        TileData tile = tileDataMap.get(lineData[0]);
        if (tile != null)
        {
            Sprite ca = createSprite(tile, Integer.parseInt(lineData[1]), Integer.parseInt(lineData[2]));
            if (lineData[0].equals("Player"))
                player = ca;
            else
                sprites.add(ca);
        }
        else
            System.out.println("Tile definition not found: " + lineData[0]);
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
            if (tileDataMap.containsKey(lineData[i]))
            {
                TileData tile = tileDataMap.get(lineData[i]);
                Sprite ca = createSprite(tile, 64 * i, numberTileLine * 64);
                if (lineData[0].equals("Player"))
                    player = ca;
                else
                    sprites.add(ca);
            }
            else
                System.out.println("Tile definition not found: " + lineData[i]);
        }
        numberTileLine++;

    }

    private Sprite createSprite(TileData tile, Integer x, Integer y)
    {
        Sprite ca;
        if (tile.totalFrames > 1)
            ca = new Sprite(tile.spritename, tile.fps, tile.totalFrames, tile.cols, tile.rows, tile.frameWidth, tile.frameHeight, tile.direction);
        else
            ca = new Sprite(tile.spritename, tile.direction);

        ca.setName(tile.spritename);
        ca.setPosition(x, y);
        ca.setBlocker(tile.blocking);
        ca.setSpeed(tile.velocity);
        /*if (tile.totalFrames > 1)
            ca.setAnimated(true);
        else
            ca.setAnimated(false);
*/
        return ca;
    }

    public Image rotateImage(Image image, int rotation) {
        ImageView iv = new ImageView(image);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        params.setTransform(new Rotate(rotation, image.getHeight() / 2, image.getWidth() / 2));
        params.setViewport(new Rectangle2D(0, 0, image.getHeight(), image.getWidth()));
        return iv.snapshot(params, null);
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
