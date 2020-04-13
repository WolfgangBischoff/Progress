package Core;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class MenuOverlay
{
    private static final String CLASSNAME = "MenuOverlay-";
    private static final int MENU_WIDTH = 500;
    private static final int MENU_HEIGHT = 300;

    private Canvas menuCanvas;
    private GraphicsContext menuGc;
    private WritableImage menuImage;

    public MenuOverlay()
    {
        menuCanvas = new Canvas(MENU_WIDTH, MENU_HEIGHT);
        menuGc = menuCanvas.getGraphicsContext2D();
        draw();
    }

    private void draw() throws NullPointerException
    {
        String methodName = "draw()";
        menuGc.clearRect(0, 0, MENU_WIDTH, MENU_HEIGHT);

        menuGc.setFill(Color.DARKSLATEGREY);
        menuGc.fillRect(0, 0, MENU_WIDTH, MENU_HEIGHT);

        menuGc.setFill(Color.BLANCHEDALMOND);
        menuGc.fillText("Game Menu", 30, 30);

        menuImage = menuCanvas.snapshot(new SnapshotParameters(), null);

    }

    public WritableImage getMenuImage()
    {
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
