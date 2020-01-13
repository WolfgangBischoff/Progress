package Core;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.*;

public class WorldView implements GUIController
{
    private static Rectangle2D borders;
    static List<Sprite> activeLayers = new ArrayList<>();
    static List<Sprite> passivLayer = new ArrayList<>();
    static List<Sprite> bottomLayer = new ArrayList<>();
    static List<Sprite> middleLayer = new ArrayList<>();
    static List<Sprite> topLayer = new ArrayList<>();
    Map<Integer, List<Point2D>> lightMap = new HashMap<>();
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
    Canvas mask;
    GraphicsContext maskGc;

    /*
    public WorldView(String levelName, Canvas worldCanvas)
    {
        this.levelName = levelName;
        loadEnvironment();
        worldCanvas.setWidth(VIEWPORT_SIZE_X);
        worldCanvas.setHeight(VIEWPORT_SIZE_Y);
        gc = worldCanvas.getGraphicsContext2D();
        offsetMaxX = borders.getMaxX() - VIEWPORT_SIZE_X;
        offsetMaxY = borders.getMaxY() - VIEWPORT_SIZE_Y;
    }*/

    public WorldView(String levelName, Pane root)
    {
        this.root = root;
        worldCanvas = new Canvas(VIEWPORT_SIZE_X, VIEWPORT_SIZE_Y);
        mask = new Canvas(VIEWPORT_SIZE_X, VIEWPORT_SIZE_Y);
        gc = worldCanvas.getGraphicsContext2D();
        maskGc = mask.getGraphicsContext2D();

        //root.getChildren().add(worldCanvas);
        this.levelName = levelName;
        loadEnvironment();
        //worldCanvas.setWidth(VIEWPORT_SIZE_X);
        //worldCanvas.setHeight(VIEWPORT_SIZE_Y);
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


        int playerlight = 200;//Radius of Player Light
        int mediumLight = 125;
        float worldDarkness = 0.3f;
        Rectangle screenShadowRectangle = new Rectangle(0, 0, VIEWPORT_SIZE_X, VIEWPORT_SIZE_Y);
        Circle playerLight = new Circle(player.positionX - camX + player.getHitBoxOffsetX() + player.getHitBoxWidth() / 2, player.positionY - camY + player.getHitBoxOffsetY() + player.getHitBoxHeight() / 2, playerlight);
        Circle testLight = new Circle(300 - camX, 500 - camY, mediumLight);
        Circle test = new Circle(700 - camX, 500 - camY, mediumLight);

        RadialGradient gradient1 = new RadialGradient(
                0,
                0,
                0.5,
                0.5,
                1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.35, Color.TRANSPARENT)
                //new Stop(0.35, Color.WHITE)
                , new Stop(0.5, Color.rgb(0, 0, 0, worldDarkness))
                //, new Stop(0.5, Color.TRANSPARENT)
        );
   /*
   gc.setGlobalBlendMode(BlendMode.ADD);
        gc.setFill(Color.rgb(70, 70, 70));
        gc.setFill(Color.rgb(100, 100, 100));

        gc.fillOval(200, 250, 200, 200);
        gc.fillOval(
                player.positionX  + player.getHitBoxOffsetX() + player.getHitBoxWidth() / 2,
                player.positionY  + player.getHitBoxOffsetY() + player.getHitBoxHeight() / 2,  200, 200);

        gc.setGlobalBlendMode(BlendMode.DIFFERENCE);
        //gc.setFill(Color.rgb(40, 40, 40));
        gc.fillRect(camX, camY, VIEWPORT_SIZE_X, VIEWPORT_SIZE_Y);

        gc.setGlobalBlendMode(BlendMode.SRC_OVER);


        root.setBlendMode(BlendMode.SRC_OVER);
*/

        maskGc.setFill(Color.rgb(50,50,50));
        maskGc.fillRect(0, 0, VIEWPORT_SIZE_X, VIEWPORT_SIZE_Y);
        maskGc.drawImage(new Image("/res/img/" + "whitelight" + ".png"), 300, 300);
        maskGc.drawImage(new Image("/res/img/" + "whitelight" + ".png"), 400, 300);
        maskGc.drawImage(new Image("/res/img/" + "whitelight" + ".png"), 500, 500);
        maskGc.drawImage(new Image("/res/img/" + "whitelight" + ".png"), 500, 400);
        maskGc.drawImage(new Image("/res/img/" + "whitelight" + ".png"), player.positionX + player.getHitBoxOffsetX() + player.getHitBoxWidth() / 2 - 64, player.positionY +player.getHitBoxOffsetY() + player.getHitBoxHeight() / 2 -64);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = mask.snapshot(params, null);
        gc.setGlobalBlendMode(BlendMode.MULTIPLY);
        //gc.setGlobalBlendMode(BlendMode.ADD);
        worldCanvas.getGraphicsContext2D().drawImage(image, 0, 0);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);

        root.getChildren().clear();
        root.getChildren().add(worldCanvas);

        gc.translate(camX, camY);
    }

    private void calcLight()
    {

    }

    private void setInverseClip(final Node node, final Shape clip)
    {
        final Rectangle inverse = new Rectangle();
        inverse.setWidth(node.getLayoutBounds().getWidth());
        inverse.setHeight(node.getLayoutBounds().getHeight());
        node.setClip(Shape.subtract(inverse, clip));
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
