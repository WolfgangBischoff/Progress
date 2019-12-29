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
    final int tileCodeIdx = 0;
    final int spriteNameIdx = 1;
    final int blockingIdx = 2;
    final int layerIdx = 3;
    final int fpsIdx = 4;
    final int totalFramesIdx = 5;
    final int colsIdx = 6;
    final int rowsIdx = 7;
    final int frameWidthIdx = 8;
    final int frameHeightIdx = 9;
    final int velocityIdx = 10;
    final int directionIdx = 11;



    private Rectangle2D borders;
    List<Sprite> mediumLayer = new ArrayList<>();
    List<Sprite> bttmLayer = new ArrayList<>();
    List<Sprite> upperLayer = new ArrayList<>();
    String levelName;
    GraphicsContext gc;
    List<String[]> leveldata = new ArrayList<>();
    Sprite player;
    Image background;
    Map<String, TileData> tileDataMap = new HashMap<>();
    int numberTileLine = 0;

    private final String KEYWORD_BACKGROUND = "background:";
    private final String KEYWORD_TILES = "tiles:";
    private final String KEYWORD_ACTORS = "actors:";
    private final String KEYWORD_TILEDEF = "tiledefinition:";

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
            keywords.add(KEYWORD_ACTORS);
            keywords.add(KEYWORD_TILEDEF);
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
                case KEYWORD_TILEDEF:
                    tileDefinition(lineData);
                    continue;
                case KEYWORD_TILES:
                    readTile(lineData);
                    continue;
                case KEYWORD_ACTORS:
                    readActors(lineData);
                    continue;

            }
        }

        borders = new Rectangle2D(0, 0, background.getWidth() - player.basewidth, background.getHeight() - player.baseheight);
        //System.out.println("WorldLoader: " + tileDataMap);

    }

    private void tileDefinition(String[] lineData)
    {
        Boolean blocking = Boolean.parseBoolean(lineData[blockingIdx]);
        Integer layer = Integer.parseInt(lineData[layerIdx]);
        Integer fps = Integer.parseInt(lineData[fpsIdx]);
        Integer totalFrames = Integer.parseInt(lineData[totalFramesIdx]);
        Integer cols = Integer.parseInt(lineData[colsIdx]);
        Integer rows = Integer.parseInt(lineData[rowsIdx]);
        Integer frameWidth = Integer.parseInt(lineData[frameWidthIdx]);
        Integer frameHeight = Integer.parseInt(lineData[frameHeightIdx]);
        Integer velocity = Integer.parseInt(lineData[velocityIdx]);
        Integer direction = Integer.parseInt(lineData[directionIdx]);
        TileData current = new TileData(lineData[spriteNameIdx], blocking, layer, fps, totalFrames, cols, rows, frameWidth, frameHeight, velocity, direction);
        tileDataMap.put(lineData[tileCodeIdx], current);
    }

    class TileData
    {
        public String spriteName;
        public Boolean blocking;
        public Integer fps, totalFrames, cols, rows, frameWidth, frameHeight, velocity, direction, layer;

        public TileData(String spriteName, Boolean blocking, Integer layer, Integer fps, Integer totalFrames, Integer cols, Integer rows, Integer frameWidth, Integer frameHeight, Integer velocity, Integer direction)
        {
            this.spriteName = spriteName;
            this.blocking = blocking;
            this.layer = layer;
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

    private void addToLayer(Sprite sprite, int layer)
    {
        switch (layer)
        {
            case 0: bttmLayer.add(sprite); break;
            case 1: mediumLayer.add(sprite); break;
            case 2: upperLayer.add(sprite);break;
            default: throw new RuntimeException("Layer not defined");
        }
    }

    private void readTile(String[] lineData)
    {
        for (int i = 0; i < lineData.length; i++)
        {
            if (tileDataMap.containsKey(lineData[i]))
            {
                TileData tile = tileDataMap.get(lineData[i]);
                int layer = tile.layer;
                Sprite ca = createSprite(tile, 64 * i, numberTileLine * 64);

                if (lineData[0].equals("Player"))
                    player = ca;
                addToLayer(ca, layer);
            }
            else
                System.out.println("Tile definition not found: " + lineData[i]);
        }
        numberTileLine++;

    }

    private void readActors(String[] lineData)
    {
        TileData tile = tileDataMap.get(lineData[0]);
        if (tile != null)
        {
            Sprite ca = createSprite(tile, Integer.parseInt(lineData[1]), Integer.parseInt(lineData[2]));
            int layer = tile.layer;
            addToLayer(ca, layer);

            if (lineData[0].equals("Player"))
                player = ca;
        }
        else
            System.out.println("Tile definition not found: " + lineData[0]);
    }

    private void readBackground(String[] lineData)
    {
        background = new Image("/res/img/" + lineData[0] + ".jpg");
    }

    private Sprite createSprite(TileData tile, Integer x, Integer y)
    {
        Sprite ca;
        if (tile.totalFrames > 1)
            ca = new Sprite(tile.spriteName, tile.fps, tile.totalFrames, tile.cols, tile.rows, tile.frameWidth, tile.frameHeight, tile.direction);
        else
            ca = new Sprite(tile.spriteName, tile.direction);

        ca.setName(tile.spriteName);
        ca.setPosition(x, y);
        ca.setBlocker(tile.blocking);
        ca.setSpeed(tile.velocity);

        return ca;
    }

    public Rectangle2D getBorders()
    {
        return borders;
    }

    public List<Sprite> getMediumLayer()
    {
        return mediumLayer;
    }

    public List<Sprite> getBttmLayer()
    {
        return bttmLayer;
    }

    public List<Sprite> getUpperLayer()
    {
        return upperLayer;
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
