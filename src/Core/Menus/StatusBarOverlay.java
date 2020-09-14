package Core.Menus;

import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class StatusBarOverlay
{
    private static final String CLASSNAME = "StatusBarOverlay ";
    private final int WIDTH;
    private final int HEIGHT;
    Canvas canvas;
    GraphicsContext graphicsContext;
    WritableImage writableImage;

    int maxValue;
    int current;
    IntegerProperty baseValue;

    public StatusBarOverlay(int WIDTH, int HEIGHT, IntegerProperty integerProperty, int maxValue)
    {
        this.baseValue = integerProperty;
        current = baseValue.getValue();
        baseValue.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
            {
                current = (int)newValue;
            }
        });
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.maxValue = maxValue;
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
        //graphicsContext.fillRect(backgroundOffsetX, backgroundOffsetY, WIDTH - backgroundOffsetX * 2, HEIGHT - backgroundOffsetY * 2);
        graphicsContext.fillRect(backgroundOffsetX, backgroundOffsetY, WIDTH - backgroundOffsetX, HEIGHT - backgroundOffsetY * 2);

        //Fill bar
        float fillPercentage= current / (float) maxValue * WIDTH;
        graphicsContext.setFill(marking);
        graphicsContext.fillRect(backgroundOffsetX, backgroundOffsetY, fillPercentage, HEIGHT - backgroundOffsetY * 2);
        String msg = "Current: " + current + " max: " + maxValue +  " Percent: " + current / (float) maxValue;
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
