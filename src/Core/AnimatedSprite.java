
package Core;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AnimatedSprite extends AnimationTimer
{
    //https://stackoverflow.com/questions/10708642/javafx-2-0-an-approach-to-game-sprite-animation
    private final ImageView imageView; //Image view that will display our sprite
    private final float fps; //frames per second I.E. 24
    private final int totalFrames; //Total number of frames in the sequence
    private final int cols; //Number of columns on the sprite sheet
    private final int rows; //Number of rows on the sprite sheet
    private final int frameWidth; //Width of an individual frame
    private final int frameHeight; //Height of an individual frame
    private int currentCol = 0;
    private int currentRow = 0;
    private long lastFrame = 0;



    public AnimatedSprite(ImageView imageView, String imagepath, float fps, int totalFrames, int cols, int rows, int frameWidth, int frameHeight)
    {
        this.imageView = imageView;
        imageView.setImage(new Image("img/"+imagepath));
        imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
        this.fps = fps;
        this.totalFrames = totalFrames;
        this.cols = cols;
        this.rows = rows;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        lastFrame = System.nanoTime();
    }


    public void handle(long now)
    {
        int frameJump = (int) Math.floor((now - lastFrame) / (1000000000 / fps)); //Determine how many frames we need to advance to maintain frame rate independence
//Do a bunch of math to determine where the viewport needs to be positioned on the sprite sheet
        if (frameJump >= 1)
        {
            lastFrame = now;

            int addRows = (int) Math.floor((float) frameJump / (float) cols);
            int frameAdd = frameJump - (addRows * cols);

            if (currentCol + frameAdd >= cols)
            {
                currentRow += addRows + 1;
                currentCol = frameAdd - (cols - currentCol);
            }
            else
            {
                currentRow += addRows;
                currentCol += frameAdd;
            }
            currentRow = (currentRow >= rows) ? currentRow - ((int) Math.floor((float) currentRow / rows) * rows) : currentRow;

            //The last row may or may not contain the full number of columns
            if ((currentRow * cols) + currentCol >= totalFrames)
            {
                currentRow = 0;
                currentCol = Math.abs(currentCol - (totalFrames - (int) (Math.floor((float) totalFrames / cols) * cols)));
            }

            imageView.setViewport(new Rectangle2D(currentCol * frameWidth, currentRow * frameHeight, frameWidth, frameHeight));

        }
    }


}
