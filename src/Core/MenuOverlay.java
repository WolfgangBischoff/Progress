package Core;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static Core.Config.*;

public class MenuOverlay
{
    private static final String CLASSNAME = "MenuOverlay-";
    private Canvas menuCanvas;
    private GraphicsContext menuGc;
    private WritableImage menuImage;
    private Actor player;

    Image cornerTopLeft;
    Image cornerBtmRight;

    public MenuOverlay()
    {
        menuCanvas = new Canvas(INVENTORY_WIDTH, INVENTORY_HEIGHT);
        menuGc = menuCanvas.getGraphicsContext2D();
        player = WorldView.getPlayer().actor;
        cornerTopLeft = new Image(IMAGE_DIRECTORY_PATH + "txtbox/textboxTL.png");
        cornerBtmRight = new Image(IMAGE_DIRECTORY_PATH + "txtbox/textboxBL.png");
    }

    private void draw() throws NullPointerException
    {
        String methodName = "draw() ";
        player = WorldView.getPlayer().actor; //Just needed as long the player resets with stage load (so we have always new Player)
        menuGc.clearRect(0, 0, INVENTORY_WIDTH, INVENTORY_HEIGHT);
        Color background = Color.rgb(60, 90, 85);
        double hue = background.getHue();
        double sat = background.getSaturation();
        double brig = background.getBrightness();
        Color marking = Color.hsb(hue, sat - 0.2, brig + 0.2);
        Color font = Color.hsb(hue, sat + 0.15, brig + 0.4);

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
                menuGc.fillRect(slotX + 2, slotY + 2, itemTileWidth - 4, itemTileWidth - 4);

                //Item slot images
                Collectible current = null;
                if(itemSlotNumber < player.inventory.itemsList.size())
                    current = player.inventory.itemsList.get(itemSlotNumber);
                if (current != null)
                    menuGc.drawImage(current.image, slotX, slotY);
                itemSlotNumber++;
            }
        }

        menuGc.setFill(font);
        menuGc.fillText("Game Menu " + player.actorInGameName, 30, 30);
        menuGc.fillText(player.inventory.itemsList.toString(), 30, 60);

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

    public static int getMenuWidth()
    {
        return INVENTORY_WIDTH;
    }

    public static int getMenuHeight()
    {
        return INVENTORY_HEIGHT;
    }
}
