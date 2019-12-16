package Core;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

public class Sprite
{
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    private Boolean isBlocker = false;
    private String name = "noSet";
    private double speed = 50;


    public Sprite()
    {
        positionX = 0;
        positionY = 0;
        velocityX = 0;
        velocityY = 0;
    }


    public void addVelocity(double x, double y)
    {
        velocityX += x;
        velocityY += y;
    }


    public void update(double time)
    {
        Rectangle2D worldBorders = WorldView.getBorders();
        List<Sprite> otherSprites = WorldView.getSprites();
        Rectangle2D plannedPosition = new Rectangle2D(positionX + velocityX * time, positionY + velocityY * time, width, height);

        //System.out.println(worldBorders);
        //System.out.println("X " +  positionX + velocityX * time +" Y" + positionY + velocityY * time);
        //System.out.println("Sprite widht: "+ width + " Height: " + height);

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
        gc.drawImage(image, positionX, positionY);
    }

    public Rectangle2D getBoundary()
    {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    public boolean intersects(Sprite s)
    {
        return s.getBoundary().intersects(this.getBoundary());
    }

    public String toString()
    {
        return name + " Position: [" + positionX + "," + positionY + "]"
                + " Velocity: [" + velocityX + "," + velocityY + "]"
                + " Speed: " + getSpeed()
                + " Blocker: " + isBlocker
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

    public void setImage(Image i)
    {
        image = i;
        width = i.getWidth();
        height = i.getHeight();
    }

    public void setImage(String filename)
    {
        Image i = new Image(filename);
        setImage(i);
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
}
