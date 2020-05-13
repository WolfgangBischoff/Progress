package Core;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class MenuOverlay
{
    private static final String CLASSNAME = "MenuOverlay/";
    private static final int MENU_WIDTH = 500;
    private static final int MENU_HEIGHT = 300;

    private Canvas menuCanvas;
    private GraphicsContext menuGc;
    private WritableImage menuImage;

    private Actor player;

    public MenuOverlay()
    {
        menuCanvas = new Canvas(MENU_WIDTH, MENU_HEIGHT);
        menuGc = menuCanvas.getGraphicsContext2D();
        player = WorldView.getPlayer().actor;
    }

    private void draw() throws NullPointerException
    {
        String methodName = "draw() ";
        menuGc.clearRect(0, 0, MENU_WIDTH, MENU_HEIGHT);
        Color background = Color.rgb(60, 90, 85);
        double hue = background.getHue();
        double sat = background.getSaturation();
        double brig = background.getBrightness();
        Color marking = Color.hsb(hue, sat - 0.2, brig + 0.2);
        Color font = Color.hsb(hue, sat + 0.15, brig + 0.4);

        menuGc.setGlobalAlpha(0.8);
        menuGc.setFill(background);
        menuGc.fillRect(0, 0, MENU_WIDTH, MENU_HEIGHT);

        menuGc.setGlobalAlpha(1);
        menuGc.setFill(marking);

        for (int y = 0; y < 3; y++)
        {
            int offsetY = y * (64 + 10) + 40;
            for (int i = 0; i < 6; i++)
            {
                int squareX = i * (64 + 10) + 10;
                menuGc.fillRect(squareX, offsetY, 64, 64);
            }
        }

        menuGc.setFill(font);
        menuGc.fillText("Game Menu " + player.actorInGameName, 30, 30);
        //System.out.println(CLASSNAME + methodName + player.inventory.itemsList.toString());
        menuGc.fillText(player.inventory.itemsList.toString(), 30, 60);

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
        return MENU_WIDTH;
    }

    public static int getMenuHeight()
    {
        return MENU_HEIGHT;
    }
}
