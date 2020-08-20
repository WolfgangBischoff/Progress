package Core.Menus.Inventory;

import Core.Actor;
import Core.Config;
import Core.GameWindow;
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
    InventoryOverlay playerInventoryOverlay;
    InventoryOverlay otherInventoryOverlay;
    WritableImage playerInventory;
    WritableImage exchangeInventory;
    static Actor exchangeInventoryActor;
    private static int WIDTH = CAMERA_WIDTH;
    private static int HEIGHT = CAMERA_HEIGHT;
    private static String CLASSNAME = "InventoryController ";
    private static int otherInventoryOffsetX = 100;
    private static int otherInventoryOffsetY = 0;
    Point2D playerInventoryPosition = Config.INVENTORY_POSITION;
    Point2D exchangeInventoryPosition = EXCHANGE_INVENTORY_POSITION;

    public InventoryController()
    {
        canvas = new Canvas(WIDTH, HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        playerInventoryOverlay = new InventoryOverlay(WorldView.getPlayer().getActor(), playerInventoryPosition);
    }


    public WritableImage getMenuImage()
    {
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);
        playerInventory = playerInventoryOverlay.getMenuImage();
        //graphicsContext.drawImage(playerInventory, 0, 0);
        graphicsContext.drawImage(playerInventory, playerInventoryPosition.getX(), playerInventoryPosition.getY());

        if (WorldViewController.getWorldViewStatus() == WorldViewStatus.INVENTORY_EXCHANGE)
        {
            otherInventoryOverlay = new InventoryOverlay(exchangeInventoryActor, exchangeInventoryPosition);
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
        //GameWindow.getSingleton().setMouseMoved(false);
    }

    public static void setExchangeInventoryActor(Actor exchangeInventoryActor)
    {
        String methodName = "setExchangeInventoryActor() ";
        InventoryController.exchangeInventoryActor = exchangeInventoryActor;
    }
}
