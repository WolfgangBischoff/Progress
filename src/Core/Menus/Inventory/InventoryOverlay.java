package Core.Menus.Inventory;

import Core.*;
import Core.Enums.CollectableType;
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

public class InventoryOverlay
{
    private static final String CLASSNAME = "MenuOverlay-";
    private Canvas menuCanvas;
    private GraphicsContext menuGc;
    private WritableImage menuImage;
    private Actor player;
    private Point2D relativeMousePosition;
    private List<String> interfaceElements_list = new ArrayList<>();
    private List<Rectangle2D> interfaceElements_Rectangles = new ArrayList<>();
    private Integer highlightedElement = 0;

    Image cornerTopLeft;
    Image cornerBtmRight;

    public InventoryOverlay()
    {
        menuCanvas = new Canvas(INVENTORY_WIDTH, INVENTORY_HEIGHT);
        menuGc = menuCanvas.getGraphicsContext2D();
        player = WorldView.getPlayer().getActor();
        cornerTopLeft = new Image(IMAGE_DIRECTORY_PATH + "txtbox/textboxTL.png");
        cornerBtmRight = new Image(IMAGE_DIRECTORY_PATH + "txtbox/textboxBL.png");
    }

    private void draw() throws NullPointerException
    {
        String methodName = "draw() ";
        player = WorldView.getPlayer().getActor(); //Just needed as long the player resets with stage load (so we have always new Player)
        menuGc.clearRect(0, 0, INVENTORY_WIDTH, INVENTORY_HEIGHT);
        Color background = Color.rgb(60, 90, 85);
        double hue = background.getHue();
        double sat = background.getSaturation();
        double brig = background.getBrightness();
        Color marking = Color.hsb(hue, sat - 0.2, brig + 0.2);
        Color font = Color.hsb(hue, sat + 0.15, brig + 0.4);
        Color red = Color.hsb(0, 0.33, 0.90);
        Color darkred = Color.hsb(0, 0.23, 0.70);
        Color green = Color.hsb(140, 0.33, 0.90);
        interfaceElements_Rectangles.clear();
        interfaceElements_list.clear();

        //Background
        menuGc.setGlobalAlpha(0.8);
        menuGc.setFill(background);
        int backgroundOffsetX = 16, backgroundOffsetY = 10;
        menuGc.fillRect(backgroundOffsetX, backgroundOffsetY, INVENTORY_WIDTH - backgroundOffsetX * 2, INVENTORY_HEIGHT - backgroundOffsetY * 2);

        //Item Slots
        menuGc.setGlobalAlpha(1);
        int itemTileWidth = 64;
        int numberTilesRow = 10;
        int numberRows = 3;
        int spaceBetweenTiles = 10;
        int initialOffsetX = (INVENTORY_WIDTH - (numberTilesRow * itemTileWidth + (numberTilesRow - 1) * spaceBetweenTiles)) / 2; //Centered
        int initialOffsetY = 75;
        int itemSlotNumber = 0;
        int slotNumber = 0;
        for (int y = 0; y < numberRows; y++)
        {
            int slotY = y * (itemTileWidth + spaceBetweenTiles) + initialOffsetY;
            for (int i = 0; i < numberTilesRow; i++)
            {
                //Rectangle
                int slotX = i * (itemTileWidth + spaceBetweenTiles) + initialOffsetX;
                menuGc.setFill(font);
                menuGc.fillRect(slotX, slotY, itemTileWidth, itemTileWidth);
                menuGc.setFill(marking);
                Rectangle2D rectangle2D = new Rectangle2D(slotX + 2, slotY + 2, itemTileWidth - 4, itemTileWidth - 4);
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
                if (itemSlotNumber < player.getInventory().itemsList.size())
                    current = player.getInventory().itemsList.get(itemSlotNumber);
                if (current != null)
                {
                    menuGc.drawImage(current.getImage(), slotX, slotY);
                    //Stolen sign
                    if(GameVariables.getStolenCollectibles().contains(current))
                    {
                        menuGc.setFill(darkred);
                        menuGc.fillOval(slotX + 44, slotY + 44, 16,16);
                        menuGc.setFill(red);
                        menuGc.fillOval(slotX + 46, slotY + 46, 12 ,12);
                    }
                }
                itemSlotNumber++;
            }
        }

        menuGc.setFill(font);
        menuGc.fillText("Inventory of " + player.getActorInGameName(), initialOffsetX, 60);

        //Decoration
        menuGc.drawImage(cornerTopLeft, 0, 0);
        menuGc.drawImage(cornerBtmRight, INVENTORY_WIDTH - cornerBtmRight.getWidth(), INVENTORY_HEIGHT - cornerBtmRight.getHeight());

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
        Point2D discussionOverlayPosition = WorldView.getPersonalityScreenPosition();
        Rectangle2D discussionPosRelativeToWorldview = new Rectangle2D(discussionOverlayPosition.getX(), discussionOverlayPosition.getY(), DISCUSSION_WIDTH, DISCUSSION_HEIGHT);

        //Calculate Mouse Position relative to Discussion
        if (discussionPosRelativeToWorldview.contains(mousePosition))
            relativeMousePosition = new Point2D(mousePosition.getX() - discussionOverlayPosition.getX(), mousePosition.getY() - discussionOverlayPosition.getY());
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
        if (player.getInventory().itemsList.size() > highlightedElement && highlightedElement >= 0)
            collectible = player.getInventory().itemsList.get(highlightedElement);
        System.out.println(CLASSNAME + methodName + "clicked " + collectible);

        if (collectible != null && collectible.getType() == CollectableType.FOOD)
        {
            System.out.println(CLASSNAME + methodName + "You ate " + collectible.getIngameName());
            //Item vanishes competely if consumed.
            player.getInventory().itemsList.remove(collectible);
            GameVariables.getStolenCollectibles().remove(collectible);
        }

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
        return INVENTORY_WIDTH;
    }

    public static int getMenuHeight()
    {
        return INVENTORY_HEIGHT;
    }
}
