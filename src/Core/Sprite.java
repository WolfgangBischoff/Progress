package Core;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

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
    Long lastInteraction = 0l;
    private int totalFrames; //Total number of frames in the sequence
    private int cols; //Number of columns on the sprite sheet
    private int rows; //Number of rows on the sprite sheet
    private double frameWidth; //Width of an individual frame
    private double frameHeight; //Height of an individual frame
    private int currentCol = 0; //last used frame in case of animation
    private int currentRow = 0;
    private Boolean isBlocker = false;
    private Boolean animated = false;
    private Direction direction = Direction.SOUTH;
    private Boolean interact = false;
    private Boolean blockedByOtherSprite = false;
    Rectangle2D interactionArea;
    Rectangle2D hitBox;
    private double hitBoxOffsetX = 0, hitBoxOffsetY = 0, hitBoxWidth, hitBoxHeight;
    Actor actor; //Logic for sprite


    public Sprite(String imagename, int direction)
    {
        animated = false;
        this.direction = Direction.getDirectionFromValue(direction);
        setImage("/res/img/" + imagename + ".png");
        frameWidth = basewidth;
        frameHeight = baseheight;

        //Hitbox is the same per default
        //hitBox = new Rectangle2D(0,0,frameWidth, frameHeight);
        hitBoxWidth = frameWidth;
        hitBoxHeight = frameHeight;
    }

    public Sprite(String imagename, float fps, int totalFrames, int cols, int rows, int frameWidth, int frameHeight, int direction)
    {
        animated = true;
        this.direction = Direction.getDirectionFromValue(direction);
        setImage("/res/img/" + imagename + ".png");
        this.fps = fps;
        this.totalFrames = totalFrames;
        this.cols = cols;
        this.rows = rows;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        //Hitbox is the same per default
        //hitBox = new Rectangle2D(0,0,frameWidth, frameHeight);
        hitBoxWidth = frameWidth;
        hitBoxHeight = frameHeight;
    }

    public void addVelocity(double x, double y)
    {
        velocityX += x;
        velocityY += y;
    }

    private Rectangle2D calcInteractionRectangle(int maxInteractionDistance)
    {
        int interactionWidth = 8;
        switch (direction)
        {
            case NORTH:
                //return new Rectangle2D(positionX + frameHeight/2 - interactionWidth/2, positionY - maxInteractionDistance, interactionWidth, frameHeight);
                return new Rectangle2D(positionX + hitBoxOffsetX / 2 + hitBoxWidth / 2 - interactionWidth / 2, positionY + hitBoxOffsetY - maxInteractionDistance, interactionWidth, maxInteractionDistance);
            case EAST:
                return new Rectangle2D(positionX + frameWidth, positionY + hitBoxOffsetY + hitBoxHeight / 2 - interactionWidth / 2, maxInteractionDistance, interactionWidth);
            case SOUTH:
                //return new Rectangle2D(positionX + frameHeight/2 - interactionWidth/2, positionY + frameHeight, interactionWidth, frameHeight);
                return new Rectangle2D(positionX + hitBoxOffsetX / 2 + hitBoxWidth / 2 - interactionWidth / 2, positionY + hitBoxOffsetY + hitBoxHeight, interactionWidth, maxInteractionDistance);
            case WEST:
                //return new Rectangle2D(positionX - maxInteractionDistance, positionY + frameHeight/2 - interactionWidth/2, frameWidth, interactionWidth);
                return new Rectangle2D(positionX + hitBoxOffsetX - maxInteractionDistance, positionY + hitBoxOffsetY + hitBoxHeight / 2 - interactionWidth / 2, maxInteractionDistance, interactionWidth);
            default:
                throw new RuntimeException("calcInteractionRectangle: No Direction Set");
        }
    }

    public void update(Long currentNanoTime)
    {
        double time = (currentNanoTime - lastFrame) / 1000000000.0;
        double elapsedTimeSinceLastInteraction = (currentNanoTime - lastInteraction) / 1000000000.0;
        int maxDistanceInteraction = 30;
        interactionArea = calcInteractionRectangle(maxDistanceInteraction);
        Rectangle2D worldBorders = WorldView.getBorders();
        List<Sprite> otherSprites = WorldView.getActiveLayers();
        Rectangle2D plannedPosition = new Rectangle2D(positionX + hitBoxOffsetX + velocityX * time, positionY + hitBoxOffsetY + velocityY * time, hitBoxWidth, hitBoxHeight);
        //Rectangle2D plannedPosition = new Rectangle2D(positionX + velocityX * time, positionY + velocityY * time, basewidth, baseheight);

        for (Sprite otherSprite : otherSprites)
        {
            if (otherSprite == this)
                continue;

            if
            (otherSprite.isBlocker && otherSprite.getBoundary().intersects(plannedPosition)
                    || !worldBorders.contains(positionX + velocityX * time, positionY + velocityY * time)
            )
            {
                System.out.println("Blocked");
                blockedByOtherSprite = true;
                break;
            }

            if (interact && otherSprite.getBoundary().intersects(interactionArea) && elapsedTimeSinceLastInteraction > 3)
            {
                System.out.println("Action with " + otherSprite.name);
                lastInteraction = currentNanoTime;
                interact = false; //Interacts with first found sprite; TODO Just with sprite that can be interacted
            }

            if (intersects(otherSprite))
            {
                //System.out.println("Intersects");
            }
        }

        if (!blockedByOtherSprite)
        {
            positionX += velocityX * time;
            positionY += velocityY * time;
        }

        interact = false;
        blockedByOtherSprite = false;
        lastFrame = currentNanoTime;

    }


    public boolean intersects(Sprite s)
    {
        return s.getBoundary().intersects(this.getBoundary());
    }

    public void render(GraphicsContext gc, Long now)
    {
        if (getAnimated())
            renderAnimated(gc, now);
        else
            renderSimple(gc);

        if (Config.DEBUGMODE)
        {
            gc.strokeRect(positionX + hitBoxOffsetX, positionY + hitBoxOffsetY, hitBoxWidth, hitBoxHeight);
            if (interactionArea != null)
                gc.strokeRect(interactionArea.getMinX(), interactionArea.getMinY(), interactionArea.getWidth(), interactionArea.getHeight());
            gc.setStroke(Color.BLUE);
        }
    }

    public void renderSimple(GraphicsContext gc)
    {
        gc.drawImage(baseimage, positionX, positionY);
    }

    public void renderAnimated(GraphicsContext gc, Long now)
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
        return new Rectangle2D(positionX + hitBoxOffsetX, positionY + hitBoxOffsetY, hitBoxWidth, hitBoxHeight);
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

    public void setImage(String filename)
    {
        Image i = new Image(filename);

        //Visible Sprite
        if (!animated)
        {
            i = rotateImage(i, direction.value * 90);
        }
        baseimage = i;
        basewidth = i.getWidth();
        baseheight = i.getHeight();
    }

    public void setHitBox(double hitBoxOffsetX, double hitBoxOffsetY, double hitBoxWidth, double hitBoxHeight)
    {
        this.hitBoxOffsetX = hitBoxOffsetX;
        this.hitBoxOffsetY = hitBoxOffsetY;
        this.hitBoxWidth = hitBoxWidth;
        this.hitBoxHeight = hitBoxHeight;
    }

    public Image rotateImage(Image image, int rotation)
    {
        ImageView iv = new ImageView(image);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        params.setTransform(new Rotate(rotation, image.getHeight() / 2, image.getWidth() / 2));
        //params.setViewport(new Rectangle2D(0, 0, image.getHeight(), image.getWidth()));
        return iv.snapshot(params, null);
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

    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }

    public void setInteract(Boolean interact)
    {
        this.interact = interact;
    }

    public String getName()
    {
        return name;
    }
}
