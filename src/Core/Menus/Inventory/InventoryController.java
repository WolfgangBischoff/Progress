package Core.Menus.Inventory;

import Core.Actor;
import Core.Config;
import Core.WorldView.WorldView;
import Core.WorldView.WorldViewController;
import Core.WorldView.WorldViewStatus;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static Core.Config.*;

public class InventoryController
{
    private static String CLASSNAME = "InventoryController ";
    Canvas canvas;
    GraphicsContext graphicsContext;
    static InventoryOverlay playerInventoryOverlay;
    static InventoryOverlay otherInventoryOverlay;
    static ShopOverlay shopOverlay;
    WritableImage playerInventory;
    WritableImage interactionInventoryImage;
    static Actor exchangeInventoryActor;
    static Actor playerActor;
    private static int WIDTH = CAMERA_WIDTH;
    private static int HEIGHT = CAMERA_HEIGHT;
    Point2D playerInventoryPosition = Config.INVENTORY_POSITION;
    Point2D exchangeInventoryPosition = EXCHANGE_INVENTORY_POSITION;
    Point2D shopInterfacePosition = EXCHANGE_INVENTORY_POSITION;

    public InventoryController()
    {
        canvas = new Canvas(WIDTH, HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        playerInventoryOverlay = new InventoryOverlay(WorldView.getPlayer().getActor(), playerInventoryPosition);
        playerActor = WorldView.getPlayer().getActor();
        otherInventoryOverlay = new InventoryOverlay(null, exchangeInventoryPosition);
        shopOverlay = new ShopOverlay(null, shopInterfacePosition);
    }


    public WritableImage getMenuImage()
    {
        String methodName = "getMenuImage() ";
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);
        playerInventory = playerInventoryOverlay.getMenuImage();
        graphicsContext.drawImage(playerInventory, playerInventoryPosition.getX(), playerInventoryPosition.getY());

        if (WorldViewController.getWorldViewStatus() == WorldViewStatus.INVENTORY_EXCHANGE)
        {
            otherInventoryOverlay.setActor(exchangeInventoryActor);
            interactionInventoryImage = otherInventoryOverlay.getMenuImage();
            graphicsContext.drawImage(interactionInventoryImage, exchangeInventoryPosition.getX(), exchangeInventoryPosition.getY());
        }
        else if(WorldViewController.getWorldViewStatus() == WorldViewStatus.INVENTORY_SHOP)
        {
            shopOverlay.setActor(exchangeInventoryActor);
            interactionInventoryImage = shopOverlay.getMenuImage();
            graphicsContext.drawImage(interactionInventoryImage, shopInterfacePosition.getX(), shopInterfacePosition.getY());
        }

        SnapshotParameters transparency = new SnapshotParameters();
        transparency.setFill(Color.TRANSPARENT);
        return canvas.snapshot(transparency, null);
    }

    public void processMouse(Point2D mousePosition, boolean isMouseClicked, Long currentNanoTime)
    {
        String methodName = "processMouse() ";
        boolean clickedIntoOverlayPlayer = playerInventoryOverlay.getSCREEN_AREA().contains(mousePosition);
        boolean clickedIntoOverlayOther = otherInventoryOverlay.getSCREEN_AREA().contains(mousePosition);
        boolean clickedIntoOverlayShop = shopOverlay.getSCREEN_AREA().contains(mousePosition);


        if(isMouseClicked && !clickedIntoOverlayPlayer && WorldViewController.getWorldViewStatus() == WorldViewStatus.INVENTORY)
        {
            WorldViewController.setWorldViewStatus(WorldViewStatus.WORLD);
            playerActor.setLastInteraction(currentNanoTime);
        }else
            playerInventoryOverlay.processMouse(mousePosition, isMouseClicked, currentNanoTime);


        if (WorldViewController.getWorldViewStatus() == WorldViewStatus.INVENTORY_EXCHANGE)
        {
            if(isMouseClicked && !clickedIntoOverlayPlayer && !clickedIntoOverlayOther)
            {
                WorldViewController.setWorldViewStatus(WorldViewStatus.WORLD);
                playerActor.setLastInteraction(currentNanoTime);
            }else
            otherInventoryOverlay.processMouse(mousePosition, isMouseClicked, currentNanoTime);
        }
        else if(WorldViewController.getWorldViewStatus() == WorldViewStatus.INVENTORY_SHOP)
        {
            if(isMouseClicked && !clickedIntoOverlayPlayer && !clickedIntoOverlayShop)
            {
                WorldViewController.setWorldViewStatus(WorldViewStatus.WORLD);
                playerActor.setLastInteraction(currentNanoTime);
            }else
            shopOverlay.processMouse(mousePosition, isMouseClicked, currentNanoTime);
        }




    }

    public static void setExchangeInventoryActor(Actor exchangeInventoryActor)
    {
        String methodName = "setExchangeInventoryActor() ";
        InventoryController.exchangeInventoryActor = exchangeInventoryActor;
    }
}
