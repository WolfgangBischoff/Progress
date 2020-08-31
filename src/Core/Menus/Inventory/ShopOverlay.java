package Core.Menus.Inventory;

import Core.Actor;
import Core.Collectible;
import Core.Enums.CollectableType;
import Core.GameVariables;
import Core.GameWindow;
import Core.WorldView.WorldViewController;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static Core.Config.*;
import static Core.Config.IMAGE_DIRECTORY_PATH;
import static Core.WorldView.WorldViewStatus.INVENTORY_EXCHANGE;

public class ShopOverlay
{
    private static final String CLASSNAME = "InventoryOverlay ";
    private Canvas menuCanvas;
    private GraphicsContext menuGc;
    private WritableImage menuImage;
    private Actor actor;
    private List<String> interfaceElements_list = new ArrayList<>();
    private List<Rectangle2D> interfaceElements_Rectangles = new ArrayList<>();
    private Integer highlightedElement = 0;
    private static int WIDTH = INVENTORY_WIDTH;
    private static int HEIGHT = INVENTORY_HEIGHT;
    private Point2D SCREEN_POSITION;

    Image cornerTopLeft;
    Image cornerBtmRight;

    public ShopOverlay(Actor actor, Point2D SCREEN_POSITION)
    {
        menuCanvas = new Canvas(WIDTH, HEIGHT);
        menuGc = menuCanvas.getGraphicsContext2D();
        this.actor = actor;
        cornerTopLeft = new Image(IMAGE_DIRECTORY_PATH + "txtbox/textboxTL.png");
        cornerBtmRight = new Image(IMAGE_DIRECTORY_PATH + "txtbox/textboxBL.png");
        this.SCREEN_POSITION = SCREEN_POSITION;
    }

    private void draw() throws NullPointerException
    {
        String methodName = "draw() ";
        menuGc.clearRect(0, 0, WIDTH, HEIGHT);
        Color background = Color.rgb(60, 90, 85);
        double hue = background.getHue();
        double sat = background.getSaturation();
        double brig = background.getBrightness();
        Color marking = Color.hsb(hue, sat - 0.2, brig + 0.2);
        Color font = Color.hsb(hue, sat + 0.15, brig + 0.4);
        Color red = Color.hsb(0, 0.33, 0.90);
        Color darkRed = Color.hsb(0, 0.23, 0.70);
        Color green = Color.hsb(140, 0.33, 0.90);
        interfaceElements_Rectangles.clear();
        interfaceElements_list.clear();

        //Background
        menuGc.setGlobalAlpha(0.8);
        menuGc.setFill(background);
        int backgroundOffsetX = 16, backgroundOffsetY = 10;
        menuGc.fillRect(backgroundOffsetX, backgroundOffsetY, WIDTH - backgroundOffsetX * 2, HEIGHT - backgroundOffsetY * 2);

       //Item Slots
        menuGc.setGlobalAlpha(1);
        int itemTileWidth = 192;
        int itemTileHeight = 64;
        int numberColumns = 2;
        int numberRows = 5;
        int spaceBetweenTiles = 10;
        int initialOffsetX = (WIDTH - (numberColumns * itemTileWidth + (numberColumns - 1) * spaceBetweenTiles)) / 2; //Centered
        int initialOffsetY = 120 + 75;
        int itemSlotNumber = 0;
        int slotNumber = 0;
        for (int y = 0; y < numberRows; y++)
        {
            int slotY = y * (itemTileHeight + spaceBetweenTiles) + initialOffsetY;
            for (int i = 0; i < numberColumns; i++)
            {
                //Rectangle
                int slotX = i * (itemTileWidth + spaceBetweenTiles) + initialOffsetX;
                menuGc.setFill(font);
                menuGc.fillRect(slotX, slotY, itemTileWidth, itemTileHeight);
                menuGc.setFill(marking);
                Rectangle2D rectangle2D = new Rectangle2D(slotX + 2, slotY + 2, itemTileWidth - 4, itemTileHeight - 4);
                interfaceElements_Rectangles.add(rectangle2D);
                interfaceElements_list.add(Integer.valueOf(slotNumber).toString());

                //Highlighting
                if (highlightedElement == slotNumber)
                    menuGc.setFill(font);
                else
                    menuGc.setFill(marking);
                menuGc.fillRect(rectangle2D.getMinX(), rectangle2D.getMinY(), rectangle2D.getWidth(), rectangle2D.getHeight());
                slotNumber++;

                //Item slot images
                Collectible current = null;
                if (itemSlotNumber < actor.getInventory().itemsList.size())
                    current = actor.getInventory().itemsList.get(itemSlotNumber);
                if (current != null)
                {
                    menuGc.drawImage(current.getImage(), slotX, slotY);
                    menuGc.setFill(red);
                    menuGc.fillText(current.getIngameName() + " Price: " + current.getBaseValue(), slotX + 60, slotY + 32);
                }
                itemSlotNumber++;
            }
        }

        //Text
        int offsetYFirstLine = 60;
        int dateLength = 200;
        menuGc.setFill(font);
        menuGc.fillText("Inventory of " + actor.getActorInGameName(), initialOffsetX, offsetYFirstLine);
        menuGc.fillText("Shop", WIDTH - dateLength, offsetYFirstLine);

        //Decoration
        menuGc.drawImage(cornerTopLeft, 0, 0);
        menuGc.drawImage(cornerBtmRight, WIDTH - cornerBtmRight.getWidth(), HEIGHT - cornerBtmRight.getHeight());

        SnapshotParameters transparency = new SnapshotParameters();
        transparency.setFill(Color.TRANSPARENT);
        menuImage = menuCanvas.snapshot(transparency, null);

    }

    public WritableImage getMenuImage()
    {
        draw();
        return menuImage;
    }

    public void processMouse(Point2D mousePosition, boolean isMouseClicked, Long currentNanoTime)
    {
        String methodName = "processMouse(Point2D, boolean) ";
        Rectangle2D posRelativeToWorldview = new Rectangle2D(SCREEN_POSITION.getX(), SCREEN_POSITION.getY(), WIDTH, HEIGHT);

        //Calculate Mouse Position relative to Discussion
        Point2D relativeMousePosition;
        if (posRelativeToWorldview.contains(mousePosition))
            relativeMousePosition = new Point2D(mousePosition.getX() - SCREEN_POSITION.getX(), mousePosition.getY() - SCREEN_POSITION.getY());
        else relativeMousePosition = null;

        Integer hoveredElement = null;
        for (int i = 0; i < interfaceElements_Rectangles.size(); i++)
        {
            if (interfaceElements_Rectangles.get(i).contains(relativeMousePosition))
            {
                hoveredElement = interfaceElements_list.indexOf(Integer.toString(i));
            }
        }

        if (GameWindow.getSingleton().isMouseMoved() && hoveredElement != null)//Set highlight if mouse moved
        {
            setHighlightedElement(hoveredElement);
            GameWindow.getSingleton().setMouseMoved(false);
        }


        if (isMouseClicked && hoveredElement != null)//To prevent click of not hovered
        {
            activateHighlightedOption(currentNanoTime);
        }
    }

    private void activateHighlightedOption(Long currentNanoTime)
    {
        String methodName = "activateHighlightedOption() ";
        Collectible collectible = null;
        if (actor.getInventory().itemsList.size() > highlightedElement && highlightedElement >= 0)
            collectible = actor.getInventory().itemsList.get(highlightedElement);

        System.out.println(CLASSNAME + methodName + actor.getActorInGameName() + " inventory clicked " + collectible);

    }

    public void setHighlightedElement(Integer highlightedElement)
    {
        String methodName = "setHighlightedElement() ";
        boolean debug = false;
        if (debug && !this.highlightedElement.equals(highlightedElement))
            System.out.println(CLASSNAME + methodName + highlightedElement + " " + interfaceElements_list.get(highlightedElement));
        this.highlightedElement = highlightedElement;
    }

    public static int getMenuWidth()
    {
        return WIDTH;
    }

    public static int getMenuHeight()
    {
        return HEIGHT;
    }

    public void setMenuCanvas(Canvas menuCanvas)
    {
        this.menuCanvas = menuCanvas;
    }

    public void setMenuGc(GraphicsContext menuGc)
    {
        this.menuGc = menuGc;
    }

    public void setMenuImage(WritableImage menuImage)
    {
        this.menuImage = menuImage;
    }

    public void setActor(Actor actor)
    {
        this.actor = actor;
    }

    public void setInterfaceElements_list(List<String> interfaceElements_list)
    {
        this.interfaceElements_list = interfaceElements_list;
    }

    public void setInterfaceElements_Rectangles(List<Rectangle2D> interfaceElements_Rectangles)
    {
        this.interfaceElements_Rectangles = interfaceElements_Rectangles;
    }

    public void setSCREEN_POSITION(Point2D SCREEN_POSITION)
    {
        this.SCREEN_POSITION = SCREEN_POSITION;
    }

    public void setCornerTopLeft(Image cornerTopLeft)
    {
        this.cornerTopLeft = cornerTopLeft;
    }

    public void setCornerBtmRight(Image cornerBtmRight)
    {
        this.cornerBtmRight = cornerBtmRight;
    }
}
