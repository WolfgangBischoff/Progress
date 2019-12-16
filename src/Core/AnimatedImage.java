package Core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimatedImage extends Sprite
{
    public Image[] frames;
    public double duration;
    BufferedImage notTiled;
 /*   private final int cols; //Number of columns on the sprite sheet
    private final int rows; //Number of rows on the sprite sheet
    private final int frameWidth; //Width of an individual frame
    private final int frameHeight; //Height of an individual frame
    private int currentCol = 0;
    private int currentRow = 0;
    public List<Image> images = new ArrayList<>();

     public AnimatedImage()
    {
        cols = 6;
        rows = 1;
        frameWidth = 120;
        frameHeight = 140;

       try {
            notTiled = ImageIO.read(new File("/res/img/" + "diffueserSmokeSprites" + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int y=0; y<rows; y++)
        {
            for(int x=0; x<cols; x++)
                Image i = notTiled.getSubimage(x*frameWidth, y*frameHeight, frameWidth, frameHeight);
                images.add();

        }

    }*/

 AnimatedImage(String name)
 {
     super(name);
 }

 /*
 @Override
    public void render(GraphicsContext gc)
    {
        gc.drawImage(image, positionX, positionY);
    }
 */

    public Image getFrame(double time)
    {
        int index = (int)((time % (frames.length * duration)) / duration);
        return frames[index];
    }
}