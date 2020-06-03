package Core.Menus.DiscussionGame;

import Core.GameWindow;
import Core.Menus.MyersBriggsCharacteristic;
import Core.Utilities;
import Core.WorldView;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.TextAlignment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

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
    List<CharacterCoin> coinList = new ArrayList<>();

    Element xmlRoot;
    Image introvert;
    Image extrovert;

    public DiscussionGame()
    {
        init();
    }

    private void init()
    {
        canvas = new Canvas(DISCUSSION_WIDTH, DISCUSSION_HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        loadDiscussion();
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
            String imagepath = currentCoin.getAttribute("image");
            MyersBriggsCharacteristic myersBriggsCharacteristic = MyersBriggsCharacteristic.getType(currentCoin.getAttribute("characteristic"));
            int startX = Integer.parseInt(currentCoin.getAttribute("x"));
            int startY = Integer.parseInt(currentCoin.getAttribute("y"));
            int speed = Integer.parseInt(currentCoin.getAttribute("speed"));
            int radius = Integer.parseInt(currentCoin.getAttribute("radius"));
            String movement = currentCoin.getAttribute("movementType");

            CharacterCoin characterCoin = new CharacterCoin(imagepath, myersBriggsCharacteristic, new Point2D(startX, startY), radius, speed, movement);
            coinList.add(characterCoin);
        }

/*
        shapeList.add(new Circle(100, 100, 32));
        shapeList.add(new Circle(230, 150, 32));
        shapeList.add(new Circle(360, 200, 32));
        shapeList.add(new Circle(490, 150, 32));
        introvert = new Image("Core/Menus/DiscussionGame/introvert.png");
        extrovert = new Image("Core/Menus/DiscussionGame/exzentr.png");*/
    }


    public void update()
    {
        String methodName = "update() ";
        for (Integer i = 0; i < coinList.size(); i++)
        {
            CharacterCoin coin = coinList.get(i);
            Circle circle = coin.collisionCircle;

            //Do movement
            circle.setCenterY(circle.getCenterY() + coin.speed);

            //Check if is visible
            if (!new Rectangle2D(0, 0, canvas.getWidth(), canvas.getHeight()).
                    intersects(circle.getCenterX() - circle.getRadius(), circle.getCenterY() - circle.getRadius(), circle.getCenterX() + circle.getRadius(), circle.getCenterY() + circle.getRadius()))
            {
                circle.setCenterY(0);
            }

        }
       /* for (Integer i = 0; i < shapeList.size(); i++)
        {
            Shape shape = shapeList.get(i);
            if (shape instanceof Circle)
            {
                Circle circle = (Circle) shape;

                //Do movement
                circle.setCenterY(circle.getCenterY() + 2);

                //Check if is visible
                if (!new Rectangle2D(0, 0, canvas.getWidth(), canvas.getHeight()).
                        intersects(circle.getCenterX() - circle.getRadius(), circle.getCenterY() - circle.getRadius(), circle.getCenterX() + circle.getRadius(), circle.getCenterY() + circle.getRadius()))
                {
                    circle.setCenterY(0);
                }
            }
        }
        */
    }

    private void draw() throws NullPointerException
    {
        String methodName = "draw() ";
        graphicsContext.clearRect(0, 0, DISCUSSION_WIDTH, DISCUSSION_HEIGHT);
        Color background = Color.rgb(60, 90, 85);
        double hue = background.getHue();
        double sat = background.getSaturation();
        double brig = background.getBrightness();
        Color marking = Color.hsb(hue, sat - 0.2, brig + 0.2);
        Color font = Color.hsb(hue, sat + 0.15, brig + 0.4);
        //interfaceElements_list.clear(); //Filled with each draw() Maybe better if filled just if elements change

        graphicsContext.setTextAlign(TextAlignment.LEFT);
        graphicsContext.setTextBaseline(VPos.TOP);

        //Background
        graphicsContext.setGlobalAlpha(0.8);
        graphicsContext.setFill(background);
        int backgroundOffsetX = 0, backgroundOffsetY = 0;
        graphicsContext.fillRect(backgroundOffsetX, backgroundOffsetY, INVENTORY_WIDTH - backgroundOffsetX * 2, INVENTORY_HEIGHT - backgroundOffsetY * 2);
        graphicsContext.setGlobalAlpha(1);

        update();
        //Draw list of shapes
        graphicsContext.setFill(marking);
        for (Integer i = 0; i < coinList.size(); i++)
        {
            CharacterCoin coin = coinList.get(i);
            Circle circle = coin.collisionCircle;
            shapeList.add(circle);
            graphicsContext.drawImage(coin.image, circle.getCenterX() - circle.getRadius(), circle.getCenterY() - circle.getRadius());
        }

/*
        for (Integer i = 0; i < shapeList.size(); i++)
        {
            Shape shape = shapeList.get(i);
            if (shape instanceof Circle)
            {
                Circle circle = (Circle) shape;
                //graphicsContext.fillOval(circle.getCenterX() - circle.getRadius(), circle.getCenterY() - circle.getRadius(), circle.getRadius() * 2, circle.getRadius() * 2);
                if (i % 2 == 0)
                    graphicsContext.drawImage(introvert, circle.getCenterX() - circle.getRadius(), circle.getCenterY() - circle.getRadius());
                else
                    graphicsContext.drawImage(extrovert, circle.getCenterX() - circle.getRadius(), circle.getCenterY() - circle.getRadius());
            }
        }*/

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
        for (Integer i = 0; i < coinList.size(); i++)
        {
            Shape shape = coinList.get(i).collisionCircle;
            if (shape instanceof Circle)
            {
                Circle circle = (Circle) shape;
                if (circle.contains(mousePosRelativeToDiscussionOverlay))
                    hoveredElements.add(coinList.get(i));
            }
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
                Shape shape = hoveredElements.get(i).collisionCircle;
                if (shape instanceof Circle)
                {
                    Circle circle = (Circle) shape;
                    //System.out.println(CLASSNAME + methodName + "clicked on: " + circle);
                    shapeList.remove(circle);
                    coinList.remove(hoveredElements.get(i));
                }
            }

        }
    }

    public WritableImage getWritableImage()
    {
        draw();
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
