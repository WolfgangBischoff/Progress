package Core;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

public class Sprite
{
    //https://stackoverflow.com/questions/10708642/javafx-2-0-an-approach-to-game-sprite-animation
    Image baseimage;
    private String name = "notSet";
    double basewidth; //width of whole sprite, in therms of animation multiple frames
    double baseheight;
    double positionX;//referece is upper left corner
    double positionY;
    private double speed = 50; //in case sprite moves
    private double velocityX;
    private double velocityY;
    private float fps; //frames per second I.E. 24
    Long lastFrame = 0l;
    private int totalFrames; //Total number of frames in the sequence
    private int cols; //Number of columns on the sprite sheet
    private int rows; //Number of rows on the sprite sheet
    private int frameWidth; //Width of an individual frame
    private int frameHeight; //Height of an individual frame
    private int currentCol = 0; //last used frame in case of animation
    private int currentRow = 0;
    private Boolean isBlocker = false;
    private Boolean animated = false;


    public Sprite(String name)
    {
        positionX = 0;
        positionY = 0;
        velocityX = 0;
        velocityY = 0;
        setImage("/res/img/" + name + ".png");

    }

    public Sprite(String imagepath, float fps, int totalFrames, int cols, int rows, int frameWidth, int frameHeight)
    {
        this(imagepath);
        this.fps = fps;
        this.totalFrames = totalFrames;
        this.cols = cols;
        this.rows = rows;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    public void addVelocity(double x, double y)
    {
        velocityX += x;
        velocityY += y;
    }


    public void update(Long currentNanoTime)
    {
        double time = (currentNanoTime - lastFrame) / 1000000000.0;

        Rectangle2D worldBorders = WorldView.getBorders();
        List<Sprite> otherSprites = WorldView.getSprites();
        Rectangle2D plannedPosition = new Rectangle2D(positionX + velocityX * time, positionY + velocityY * time, basewidth, baseheight);
        lastFrame = currentNanoTime;

        for (Sprite otherSprite : otherSprites)
        {
            if (otherSprite.isBlocker && otherSprite.getBoundary().intersects(plannedPosition))
            {
                System.out.println("Blocked");
                return;
            }

            if (intersects(otherSprite))
            {
                System.out.println("Intersects");
            }
        }

        if (worldBorders.contains(positionX + velocityX * time, positionY + velocityY * time))
        {
            positionX += velocityX * time;
            positionY += velocityY * time;
        }


    }

    public void render(GraphicsContext gc)
    {
        gc.drawImage(baseimage, positionX, positionY);
    }
/*
    public Rectangle2D getBoundary()
    {
        return new Rectangle2D(positionX, positionY, basewidth, baseheight);
    }*/

    public boolean intersects(Sprite s)
    {
        return s.getBoundary().intersects(this.getBoundary());
    }


    public void render(GraphicsContext gc, Long now)
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
        }

        gc.drawImage(baseimage, currentCol * frameWidth, currentRow * frameHeight, frameWidth, frameHeight, positionX, positionY, frameWidth, frameHeight); //(img, srcX, srcY, srcWidht, srcHeight, TargetX, TargetY, TargetWidht, TargetHeight)
    }

    public Rectangle2D getBoundary()
    {
        if (!animated)
            return new Rectangle2D(positionX, positionY, basewidth, baseheight);
        else
            return new Rectangle2D(positionX, positionY, frameWidth, frameHeight);
    }


    public String toString()
    {
        return name + " Position: [" + positionX + "," + positionY + "]"
                + " Velocity: [" + velocityX + "," + velocityY + "]"
                + " Speed: " + getSpeed()
                //+ " Blocker: " + isBlocker
                + " Animated: " + animated
                ;
    }

    public Boolean getBlocker()
    {
        return isBlocker;
    }

    public void setBlocker(Boolean blocker)
    {
        isBlocker = blocker;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setBaseimage(Image i)
    {
        baseimage = i;
        basewidth = i.getWidth();
        baseheight = i.getHeight();
    }

    public void setImage(String filename)
    {
        Image i = new Image(filename);
        setBaseimage(i);
    }

    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
    }

    public void setVelocity(double x, double y)
    {
        velocityX = x;
        velocityY = y;
    }

    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public double getSpeed()
    {
        return speed;
    }

    public void setAnimated(Boolean animated)
    {
        this.animated = animated;
    }

    public Boolean getAnimated()
    {
        return animated;
    }
}
