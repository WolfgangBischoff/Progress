package Core.Menus.DiscussionGame;

import Core.Actor;
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
    String gameFileName;
    Actor actorOfDiscussion;

    public DiscussionGame(String gameIdentifier, Actor actorOfDiscussion)
    {
        gameFileName = gameIdentifier;
        this.actorOfDiscussion = actorOfDiscussion;
        init();
    }

    private void init()
    {
        canvas = new Canvas(DISCUSSION_WIDTH, DISCUSSION_HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        loadDiscussion();
        gameStartTime = GameWindow.getSingleton().getRenderTime();
        actorOfDiscussion.getPersonalityContainer().increaseNumberOfInteractions(2);
    }

    private void loadDiscussion()
    {
        String methodName = "loadDiscussion() ";
        //read from File
        xmlRoot = Utilities.readXMLFile("src/res/discussions/" + gameFileName + ".xml");
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
        visibleCoinsList.clear();
        for (CharacterCoin coin : coinsList)
        {
            if (coin.time_s <= elapsedTime && !removedCoinsList.contains(coin))
            {
                visibleCoinsList.add(coin);
            }
        }

        //For all Coins
        for (Integer i = 0; i < visibleCoinsList.size(); i++)
        {
            CharacterCoin coin = visibleCoinsList.get(i);
            Circle circle = coin.collisionCircle;
            double elapsedTimeSinceSpawn = ((currentNanoTime - gameStartTime) / 1000000000.0) - coin.time_s;

            if (coin.movementType.equals(COIN_BEHAVIOR_MOVING))
            {
                //tan(a) = Gegenkathete / Ankathete
                //sin(a) = Gegenkathete / Hypotenuse
                //cos(a) = Ankathete    / Hypotenuse
                //0     => right
                //45    => btm right

                double hypotenuse = coin.initSpeed;
                double angle = coin.angle;
                double angle_rad = Math.toRadians(angle);
                double x = Math.cos(angle_rad) * hypotenuse;
                double y = Math.sin(angle_rad) * hypotenuse;

                circle.setCenterX(circle.getCenterX() + x);
                circle.setCenterY(circle.getCenterY() + y);
            }

            else if (coin.movementType.equals(COIN_BEHAVIOR_JUMP))
            {
                double slowFactor = -5;
                coin.speed = slowFactor * elapsedTimeSinceSpawn + coin.initSpeed;
                circle.setCenterY(circle.getCenterY() - coin.speed);
                //System.out.println(CLASSNAME + methodName + coin.speed);
            }

            else if (coin.movementType.equals(COIN_BEHAVIOR_SPIRAL))
            {
                double angle = coin.genericVariables.get("startangle");
                double radius = coin.genericVariables.get("radius");
                double centrumX = coin.genericVariables.get("centrumx");
                double centrumY = coin.genericVariables.get("centrumy");
                double isclockwise = coin.genericVariables.get("isclockwise");

                if (isclockwise == 1)
                    angle += coin.initSpeed;
                else
                    angle -= coin.initSpeed;
                coin.genericVariables.put("startangle", angle);
                double angle_rad = Math.toRadians(angle);
                double x = Math.cos(angle_rad) * radius * elapsedTimeSinceSpawn / 2;
                double y = Math.sin(angle_rad) * radius * elapsedTimeSinceSpawn / 2;
                circle.setCenterX(centrumX + x);
                circle.setCenterY(centrumY + y);
            }

            else if (coin.movementType.equals(COIN_BEHAVIOR_CIRCLE))
            {
                double angle = coin.genericVariables.get("startangle");
                double radius = coin.genericVariables.get("radius");
                double centrumX = coin.genericVariables.get("centrumx");
                double centrumY = coin.genericVariables.get("centrumy");
                double isclockwise = coin.genericVariables.get("isclockwise");

                if (isclockwise == 1)
                    angle += coin.initSpeed;
                else
                    angle -= coin.initSpeed;
                coin.genericVariables.put("startangle", angle);
                double angle_rad = Math.toRadians(angle);
                double x = Math.cos(angle_rad) * radius;
                double y = Math.sin(angle_rad) * radius;
                circle.setCenterX(centrumX + x);
                circle.setCenterY(centrumY + y);
            }

            //Check if is visible
            if (!new Rectangle2D(0, 0, canvas.getWidth(), canvas.getHeight()).
                    intersects(circle.getCenterX() - circle.getRadius(), circle.getCenterY() - circle.getRadius(), circle.getCenterX() + circle.getRadius(), circle.getCenterY() + circle.getRadius())
                    || elapsedTimeSinceSpawn > COIN_MAX_TIME
            )
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
            graphicsContext.fillText(text, DISCUSSION_WIDTH / 2.0, DISCUSSION_HEIGHT / 2.0);
            graphicsContext.fillText("Finished!", DISCUSSION_WIDTH / 2.0, DISCUSSION_HEIGHT / 2.0 + graphicsContext.getFont().getSize() + 10);
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
        else if (isMouseClicked && isFinished)
        {
            //TODO logic when won the game
            WorldView.getTextbox().setNextDialogueFromDiscussion(false);
            WorldView.getTextbox().nextMessage(currentNanoTime);
            WorldView.setIsDiscussionGameActive(false);
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
