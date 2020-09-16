package Core.Menus;

import Core.Clock;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ClockOverlay
{
    private static final String CLASSNAME = "ClockOverlay ";
    private final int WIDTH;
    private final int HEIGHT;
    Canvas canvas;
    GraphicsContext graphicsContext;
    WritableImage writableImage;

    int current;
    Clock clock;

    public ClockOverlay(int WIDTH, int HEIGHT, Clock clock)
    {
        this.clock = clock;
        clock.timeProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
            {
                current = (int)newValue;
            }
        });
        current = clock.getTime();
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        canvas = new Canvas(WIDTH, HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
    }

    private void draw() throws NullPointerException
    {
        String methodName = "draw() ";
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);
        Color background = Color.rgb(60, 90, 85);
        double hue = background.getHue();
        double sat = background.getSaturation();
        double brig = background.getBrightness();
        Color marking = Color.hsb(hue, sat - 0.2, brig + 0.2);
        Color font = Color.hsb(hue, sat + 0.15, brig + 0.4);
        Color red = Color.hsb(0, 0.33, 0.90);
        Color green = Color.hsb(140, 0.33, 0.90);

        //Background
        graphicsContext.setGlobalAlpha(0.8);
        graphicsContext.setFill(background);
        int backgroundOffsetX = 16, backgroundOffsetY = 10;
        graphicsContext.fillRect(backgroundOffsetX, backgroundOffsetY, WIDTH - backgroundOffsetX, HEIGHT - backgroundOffsetY * 2);

        //Fill bar
        graphicsContext.setFill(marking);
        String msg = "Current: " + clock.getFormattedTime();
        graphicsContext.setFill(font);
        graphicsContext.fillText(msg,  backgroundOffsetX, backgroundOffsetY + graphicsContext.getFont().getSize());

        SnapshotParameters transparency = new SnapshotParameters();
        transparency.setFill(Color.TRANSPARENT);
        writableImage = canvas.snapshot(transparency, null);
    }

    public WritableImage getWritableImage()
    {
        draw();
        return writableImage;
    }

}
