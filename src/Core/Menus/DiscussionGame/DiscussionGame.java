package Core.Menus.DiscussionGame;

import Core.GameWindow;
import Core.Utilities;
import Core.WorldView;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.Config.*;

public class DiscussionGame
{
    //moving symbols like pokemon, you must know which to click before disappeared, traits more powerfull
    private static final String CLASSNAME = "DiscussionGame-";
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private WritableImage writableImage;
    Point2D mousePosRelativeToDiscussionOverlay;
    List<Shape> shapeList = new ArrayList<>();
    List<CharacterCoin> coinsList = new ArrayList<>();
    List<CharacterCoin> visibleCoinsList = new ArrayList<>();
    List<CharacterCoin> removedCoinsList = new ArrayList<>();
    Element xmlRoot;
    Long gameStartTime;
    boolean isFinished = false;
    Map<String, Integer> clickedCoins = new HashMap<>();

    public DiscussionGame()
    {
        init();
    }

    private void init()
    {
        canvas = new Canvas(DISCUSSION_WIDTH, DISCUSSION_HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        loadDiscussion();
        gameStartTime = GameWindow.getSingleton().getRenderTime();
    }

    private void loadDiscussion()
    {
        String methodName = "loadDiscussion() ";
        //read from File
        xmlRoot = Utilities.readXMLFile("src/res/discussions/test.xml");
        NodeList coins = xmlRoot.getElementsByTagName("coin");
        for (int i = 0; i < coins.getLength(); i++) //iterate coins of file
        {
            Element currentCoin = ((Element) coins.item(i));
            CharacterCoin characterCoin = new CharacterCoin(currentCoin);
            coinsList.add(characterCoin);
        }
    }


    public void update(Long currentNanoTime)
    {
        String methodName = "update() ";
        double elapsedTime = (currentNanoTime - gameStartTime) / 1000000000.0;
        //System.out.println(CLASSNAME + methodName + "Elapsed time: " + elapsedTime + " removed: " + removedCoinsList.size());

        visibleCoinsList.clear();
        for (CharacterCoin coin : coinsList)
        {
            if (coin.time_ms <= elapsedTime && !removedCoinsList.contains(coin))
            {
                visibleCoinsList.add(coin);
            }
        }

        //Do movement
        for (Integer i = 0; i < visibleCoinsList.size(); i++)
        {
            CharacterCoin coin = visibleCoinsList.get(i);
            Circle circle = coin.collisionCircle;

            if (coin.movementType.equals("stay"))
            {
                //Nothing
            }
            if (coin.movementType.equals("falling"))
                circle.setCenterY(circle.getCenterY() + coin.speed);

            if (coin.movementType.equals("jump"))
            {
                double elapsedTimeSinceSpawn = (currentNanoTime - gameStartTime) / 1000000000.0;
                coin.relativeJumpHeight = -4 * elapsedTimeSinceSpawn + coin.speed;
                circle.setCenterY(circle.getCenterY() - coin.relativeJumpHeight);
                //System.out.println(CLASSNAME + methodName + "Movement " + coin.relativeJumpHeight);
            }

            //Check if is visible
            if (!new Rectangle2D(0, 0, canvas.getWidth(), canvas.getHeight()).
                    intersects(circle.getCenterX() - circle.getRadius(), circle.getCenterY() - circle.getRadius(), circle.getCenterX() + circle.getRadius(), circle.getCenterY() + circle.getRadius()))
            {
                removedCoinsList.add(coin);
            }
        }

        if (removedCoinsList.size() == coinsList.size())
            isFinished = true;
    }

    private void draw(Long currentNanoTime) throws NullPointerException
    {
        String methodName = "draw() ";
        graphicsContext.clearRect(0, 0, DISCUSSION_WIDTH, DISCUSSION_HEIGHT);
        Color background = Color.rgb(60, 90, 85);
        double hue = background.getHue();
        double sat = background.getSaturation();
        double brig = background.getBrightness();
        Color marking = Color.hsb(hue, sat - 0.2, brig + 0.2);
        Color font = Color.hsb(hue, sat + 0.15, brig + 0.4);


        //Background
        graphicsContext.setGlobalAlpha(0.8);
        graphicsContext.setFill(background);
        int backgroundOffsetX = 0, backgroundOffsetY = 0;
        graphicsContext.fillRect(backgroundOffsetX, backgroundOffsetY, INVENTORY_WIDTH - backgroundOffsetX * 2, INVENTORY_HEIGHT - backgroundOffsetY * 2);
        graphicsContext.setGlobalAlpha(1);

        update(currentNanoTime);
        //Draw list of shapes
        graphicsContext.setFill(marking);
        for (int i = 0; i < visibleCoinsList.size(); i++)
        {
            CharacterCoin coin = visibleCoinsList.get(i);
            Circle circle = coin.collisionCircle;
            shapeList.add(circle);
            graphicsContext.drawImage(coin.image, circle.getCenterX() - circle.getRadius(), circle.getCenterY() - circle.getRadius());
        }

        if (isFinished)
        {
            String text = "You got " + clickedCoins.toString();
            graphicsContext.setFont(new Font(30));
            graphicsContext.setFill(font);
            graphicsContext.setTextAlign(TextAlignment.CENTER);
            graphicsContext.setTextBaseline(VPos.CENTER);
            graphicsContext.fillText(text, DISCUSSION_WIDTH / 2, DISCUSSION_HEIGHT / 2);
            graphicsContext.fillText("Finished!", DISCUSSION_WIDTH / 2, DISCUSSION_HEIGHT / 2 + graphicsContext.getFont().getSize() + 10);
        }

        SnapshotParameters transparency = new SnapshotParameters();
        transparency.setFill(Color.TRANSPARENT);
        writableImage = canvas.snapshot(transparency, null);

    }

    public void processMouse(Point2D mousePosition, boolean isMouseClicked, Long currentNanoTime)
    {
        String methodName = "processMouse(Point2D, boolean) ";
        Point2D discussionOverlayPosition = WorldView.getPersonalityScreenPosition();
        Rectangle2D discussionPosRelativeToWorldview = new Rectangle2D(discussionOverlayPosition.getX(), discussionOverlayPosition.getY(), DISCUSSION_WIDTH, DISCUSSION_HEIGHT);
        List<CharacterCoin> hoveredElements = new ArrayList<>();

        //Calculate Mouse Position relative to Discussion
        if (discussionPosRelativeToWorldview.contains(mousePosition))
            mousePosRelativeToDiscussionOverlay = new Point2D(mousePosition.getX() - discussionOverlayPosition.getX(), mousePosition.getY() - discussionOverlayPosition.getY());
        else
        {
            mousePosRelativeToDiscussionOverlay = null;
            return;
        }

        //Check for hovered elements
        for (Integer i = 0; i < visibleCoinsList.size(); i++)
        {
            Circle circle = visibleCoinsList.get(i).collisionCircle;
            if (circle.contains(mousePosRelativeToDiscussionOverlay))
                hoveredElements.add(visibleCoinsList.get(i));
        }

        if (GameWindow.getSingleton().isMouseMoved() && !hoveredElements.isEmpty())//Set highlight if mouse moved
        {
            GameWindow.getSingleton().setMouseMoved(false);
        }

        //Process click
        if (isMouseClicked && !hoveredElements.isEmpty())
        {
            for (Integer i = 0; i < hoveredElements.size(); i++)
            {
                Circle circle = hoveredElements.get(i).collisionCircle;
                //System.out.println(CLASSNAME + methodName + "clicked on: " + circle);
                countClickedCoin(hoveredElements.get(i));
                shapeList.remove(circle);
                removedCoinsList.add(hoveredElements.get(i));

            }

        }
    }

    private void countClickedCoin(CharacterCoin coin)
    {
        if (!clickedCoins.containsKey(coin.characteristic.toString()))
            clickedCoins.put(coin.characteristic.toString(), 0);
        clickedCoins.put(coin.characteristic.toString(), (clickedCoins.get(coin.characteristic.toString()) + 1));
    }

    public WritableImage getWritableImage(Long currentNanoTime)
    {
        draw(currentNanoTime);
        return writableImage;
    }

    public static int getMenuWidth()
    {
        return INVENTORY_WIDTH;
    }

    public static int getMenuHeight()
    {
        return INVENTORY_HEIGHT;
    }


}