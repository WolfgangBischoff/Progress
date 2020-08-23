package Core.Menus.Inventory;

import Core.Actor;
import Core.Config;
import Core.WorldView.WorldView;
import Core.WorldView.WorldViewController;
import Core.WorldView.WorldViewStatus;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static Core.Config.*;

public class InventoryController
{
    Canvas canvas;
    GraphicsContext graphicsContext;
    static InventoryOverlay playerInventoryOverlay;
    static InventoryOverlay otherInventoryOverlay;
    WritableImage playerInventory;
    WritableImage exchangeInventory;
    static Actor exchangeInventoryActor;
    static Actor playerActor;
    private static int WIDTH = CAMERA_WIDTH;
    private static int HEIGHT = CAMERA_HEIGHT;
    private static String CLASSNAME = "InventoryController ";
    Point2D playerInventoryPosition = Config.INVENTORY_POSITION;
    Point2D exchangeInventoryPosition = EXCHANGE_INVENTORY_POSITION;

    public InventoryController()
    {
        canvas = new Canvas(WIDTH, HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        playerInventoryOverlay = new InventoryOverlay(WorldView.getPlayer().getActor(), playerInventoryPosition);
        playerActor = WorldView.getPlayer().getActor();
        otherInventoryOverlay = new InventoryOverlay(null, exchangeInventoryPosition);
    }


    public WritableImage getMenuImage()
    {
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);
        playerInventory = playerInventoryOverlay.getMenuImage();
        graphicsContext.drawImage(playerInventory, playerInventoryPosition.getX(), playerInventoryPosition.getY());

        if (WorldViewController.getWorldViewStatus() == WorldViewStatus.INVENTORY_EXCHANGE)
        {
            otherInventoryOverlay.setActor(exchangeInventoryActor);
            exchangeInventory = otherInventoryOverlay.getMenuImage();
            graphicsContext.drawImage(exchangeInventory, exchangeInventoryPosition.getX(), exchangeInventoryPosition.getY());
        }

        SnapshotParameters transparency = new SnapshotParameters();
        transparency.setFill(Color.TRANSPARENT);
        return canvas.snapshot(transparency, null);
    }

    public void processMouse(Point2D mousePosition, boolean isMouseClicked, Long currentNanoTime)
    {
        playerInventoryOverlay.processMouse(mousePosition, isMouseClicked, currentNanoTime);
        if (WorldViewController.getWorldViewStatus() == WorldViewStatus.INVENTORY_EXCHANGE)
        {
            otherInventoryOverlay.processMouse(mousePosition, isMouseClicked, currentNanoTime);
        }
    }

    public static void setExchangeInventoryActor(Actor exchangeInventoryActor)
    {
        String methodName = "setExchangeInventoryActor() ";
        InventoryController.exchangeInventoryActor = exchangeInventoryActor;
    }
}
