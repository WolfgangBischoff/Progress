package Core;

import Core.Enums.TriggerType;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.List;

import static Core.Config.DEBUG_ACTORS;
import static Core.Config.DEBUG_BLOCKER;

public class Sprite
{
    public static final String CLASS_NAME = "Sprite/";
    Image baseimage;
    double basewidth; //width of whole sprite, in therms of animation multiple frames
    double baseheight;
    double positionX;//reference is upper left corner
    double positionY;
    Long lastFrame = 0L;
    Long lastUpdated = 0L;
    Rectangle2D interactionArea;
    Actor actor; //Logic for sprite
    private String name = "notSet";
    private double fps; //frames per second I.E. 24
    private int totalFrames; //Total number of frames in the sequence
    private int cols; //Number of columns on the sprite sheet
    private int rows; //Number of rows on the sprite sheet
    private double frameWidth; //Width of an individual frame
    private double frameHeight; //Height of an individual frame
    private int currentCol = 0; //last used frame in case of animation
    private int currentRow = 0;
    private Boolean isBlocker = false;
    private Boolean animated;
    private Boolean animationEnds = false;
    private Boolean interact = false;
    private Boolean blockedByOtherSprite = false;
    private double hitBoxOffsetX = 0, hitBoxOffsetY = 0, hitBoxWidth, hitBoxHeight;
    private String lightningSpriteName;


    public Sprite(String imagename)
    {
        animated = false;
        setImage(imagename);
        frameWidth = basewidth;
        frameHeight = baseheight;
        hitBoxWidth = frameWidth;
        hitBoxHeight = frameHeight;
    }

    public Sprite(String imagename, Double fps, int totalFrames, int cols, int rows, int frameWidth, int frameHeight)
    {
        animated = true;
        setImage(imagename);
        this.fps = fps;
        this.totalFrames = totalFrames;
        this.cols = cols;
        this.rows = rows;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        hitBoxWidth = frameWidth;
        hitBoxHeight = frameHeight;
    }

    private Rectangle2D calcInteractionRectangle()
    {
        String methodName = "calcInteractionRectangle() ";
        double interactionWidth = actor.getInteractionAreaWidth();
        double maxInteractionDistance = actor.getInteractionAreaDistance();
        double offsetX = actor.getInteractionAreaOffsetX();
        double offsetY = actor.getInteractionAreaOffsetY();

        switch (actor.getDirection())
        {
            case NORTH:
                return new Rectangle2D(positionX + hitBoxOffsetX + hitBoxWidth / 2 - interactionWidth / 2 + offsetX, positionY + hitBoxOffsetY - maxInteractionDistance + offsetY, interactionWidth, maxInteractionDistance);
            case EAST:
                return new Rectangle2D(positionX + hitBoxOffsetX + hitBoxWidth + offsetX, positionY + hitBoxOffsetY + hitBoxHeight / 2 - interactionWidth / 2 + offsetY, maxInteractionDistance, interactionWidth);
            case SOUTH:
                return new Rectangle2D(positionX + hitBoxOffsetX + hitBoxWidth / 2 - interactionWidth / 2 + offsetX, positionY + hitBoxOffsetY + hitBoxHeight + offsetY, interactionWidth, maxInteractionDistance);
            case WEST:
                return new Rectangle2D(positionX + hitBoxOffsetX - maxInteractionDistance + offsetX, positionY + hitBoxOffsetY + hitBoxHeight / 2 - interactionWidth / 2 + offsetY, maxInteractionDistance, interactionWidth);
            case UNDEFINED:
                return null;
            default:
                throw new RuntimeException("calcInteractionRectangle: No Direction Set at " + name);
        }
    }

    public void update(Long currentNanoTime)
    {
        String methodName = "update()";
        boolean debugMode = true;

        double time = (currentNanoTime - lastUpdated) / 1000000000.0;
        double elapsedTimeSinceLastInteraction = (currentNanoTime - actor.getLastInteraction()) / 1000000000.0;
        Rectangle2D worldBorders = WorldView.getBorders();
        List<Sprite> activeSprites = WorldView.getPassiveCollisionRelevantSpritesLayer();
        double velocityX = actor.getVelocityX();
        double velocityY = actor.getVelocityY();
        Rectangle2D plannedPosition = new Rectangle2D(positionX + hitBoxOffsetX + velocityX * time, positionY + hitBoxOffsetY + velocityY * time, hitBoxWidth, hitBoxHeight);

        if (actor != null && actor.onUpdate != TriggerType.NOTHING)
            actor.onUpdate(currentNanoTime);

        for (Sprite otherSprite : activeSprites)
        {
            if (otherSprite == this ||
                    otherSprite.actor == actor //if actor has multiple sprites they are usually congruent
            )
                continue;

            //Collision
            if
            (otherSprite.isBlocker && otherSprite.getBoundary().intersects(plannedPosition)
                    || !worldBorders.contains(positionX + velocityX * time, positionY + velocityY * time)
            )
            {
                blockedByOtherSprite = true;
            }

            //Interact within interaction area
            if (interact || actor.onInRange != TriggerType.NOTHING || getName().toLowerCase().equals("player"))
                interactionArea = calcInteractionRectangle();

            if (interact
                    && otherSprite.actor != null
                    && otherSprite.getBoundary().intersects(interactionArea)
                    && elapsedTimeSinceLastInteraction > Config.TIME_BETWEEN_INTERACTIONS)
            {
                //System.out.println(CLASS_NAME + methodName + getName() + " interact with " + otherSprite.getName());
                otherSprite.actor.onInteraction(otherSprite, currentNanoTime); //Passive reacts
                actor.setLastInteraction(currentNanoTime);
                interact = false; //Interacts with first found sprite;
            }
            else if (debugMode && interact && getName().equals("player") && otherSprite.getName().equals("diffuser"))
               System.out.println(CLASS_NAME + methodName + getName() + " cannot interact " + otherSprite.getName() + " " + elapsedTimeSinceLastInteraction +" > "+ Config.TIME_BETWEEN_INTERACTIONS);

            //In range
            if (otherSprite.actor != null
                    && actor.onInRange != TriggerType.NOTHING
                    && otherSprite.getBoundary().intersects(interactionArea))
            {
                actor.onInRange(otherSprite, currentNanoTime);
            }

            //Intersect
            if (intersects(otherSprite) && actor.onIntersection != TriggerType.NOTHING)
            {
                actor.onIntersection(otherSprite, currentNanoTime);
            }


        }

        if (!blockedByOtherSprite)
        {
            positionX += velocityX * time;
            positionY += velocityY * time;
        }

        interact = false;
        blockedByOtherSprite = false;
        lastUpdated = currentNanoTime;

    }

    public void onClick(Long currentNanoTime)
    {
        String methodName = "onClick(Long)";
        boolean debug = false;

        if (debug)
            System.out.println(CLASS_NAME + methodName + name + " clicked: " + actor.actorInGameName);

        //Sprite is clicked by player and in Range
        Sprite player = WorldView.getPlayer();
        player.calcInteractionRectangle();
        double elapsedTimeSinceLastInteraction = (currentNanoTime - player.actor.getLastInteraction()) / 1000000000.0;
        if (getBoundary().intersects(player.interactionArea)
                && elapsedTimeSinceLastInteraction > Config.TIME_BETWEEN_INTERACTIONS)
        {
            if (debug)
                System.out.println(CLASS_NAME + methodName + player.getName() + " interact with " + getName() + " by mouseclick.");
            actor.onInteraction(this, currentNanoTime); //Passive reacts
            player.actor.setLastInteraction(currentNanoTime);
        }

    }

    public boolean intersectsRelativeToWorldView(Point2D point)
    {
        Rectangle2D positionRelativeToWorldView = new Rectangle2D(positionX + hitBoxOffsetX - WorldView.camX, positionY + hitBoxOffsetY - WorldView.camY, hitBoxWidth, hitBoxHeight);
        return positionRelativeToWorldView.contains(point);
    }

    public boolean intersects(Sprite s)
    {
        return s.getBoundary().intersects(this.getBoundary());
    }

    public void render(GraphicsContext gc, Long now)
    {
        String methodName = "render()";


        if (getAnimated())
        {
            renderAnimated(gc, now);
        }
        else
        {
            renderSimple(gc);
        }

        if(DEBUG_BLOCKER && isBlocker)
            drawDebugFrame(gc);
        else if(DEBUG_ACTORS && actor != null)
            drawDebugFrame(gc);

        /*
        if (DEBUG_BLOCKER && isBlocker)
        {
            gc.strokeRect(positionX + hitBoxOffsetX, positionY + hitBoxOffsetY, hitBoxWidth, hitBoxHeight);
            if (interactionArea != null)
                gc.strokeRect(interactionArea.getMinX(), interactionArea.getMinY(), interactionArea.getWidth(), interactionArea.getHeight());
            gc.setStroke(Color.BLUE);
        }

         */
    }

    private void drawDebugFrame(GraphicsContext gc)
    {
        gc.strokeRect(positionX + hitBoxOffsetX, positionY + hitBoxOffsetY, hitBoxWidth, hitBoxHeight);
        if (interactionArea != null)
            gc.strokeRect(interactionArea.getMinX(), interactionArea.getMinY(), interactionArea.getWidth(), interactionArea.getHeight());
        gc.setStroke(Color.BLUE);
    }

    public void renderSimple(GraphicsContext gc)
    {
        gc.drawImage(baseimage, positionX, positionY);
    }

    public void renderAnimated(GraphicsContext gc, Long now)
    {
        String methodName = "renderAnimated() ";
        int frameJump = (int) Math.floor((now - lastFrame) / (1000000000 / fps)); //Determine how many frames we need to advance to maintain frame rate independence

        //Do a bunch of math to determine where the viewport needs to be positioned on the sprite sheet
        if (frameJump >= 1 && !(isAtLastFrame() && animationEnds))
        {
            lastFrame = now;

            int addRows = (int) Math.floor((float) frameJump / (float) cols);
            int frameAdd = frameJump - (addRows * cols);

            if (currentCol + frameAdd >= cols)//column finished, move no next row
            {
                currentRow += addRows + 1;
                currentCol = frameAdd - (cols - currentCol);
            }
            else//add FrameJump To current row
            {
                currentRow += addRows;
                currentCol += frameAdd;
            }
            currentRow = (currentRow >= rows) ? currentRow - ((int) Math.floor((float) currentRow / rows) * rows) : currentRow;

            //The last row may or may not contain the full number of columns
            if (isAtLastFrame())//if last frame considering rows
            {
                currentRow = 0;
                currentCol = Math.abs(currentCol - (totalFrames - (int) (Math.floor((float) totalFrames / cols) * cols)));
            }
        }

        gc.drawImage(baseimage, currentCol * frameWidth, currentRow * frameHeight, frameWidth, frameHeight, positionX, positionY, frameWidth, frameHeight); //(img, srcX, srcY, srcWidht, srcHeight, TargetX, TargetY, TargetWidht, TargetHeight)

    }

    private boolean isAtLastFrame()
    {
        return (currentRow * cols) + currentCol >= totalFrames - 1;
    }

    public Rectangle2D getBoundary()
    {
        return new Rectangle2D(positionX + hitBoxOffsetX, positionY + hitBoxOffsetY, hitBoxWidth, hitBoxHeight);
    }


    public String toString()
    {
        return name + " Position: [" + positionX + "," + positionY + "]"
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

    public void setImage(String filename) throws IllegalArgumentException
    {
        Image i = new Image("/res/img/" + filename + ".png");
        baseimage = i;
        basewidth = i.getWidth();
        baseheight = i.getHeight();
        currentCol = currentRow = 0;
    }

    public void setImage(String filename, double fps, int totalFrames, int cols, int row, int frameWidth, int frameHeight)
    {
        if (totalFrames > 1)
            setAnimated(true);
        else
            setAnimated(false);
        setImage(filename);
        this.fps = fps;
        this.totalFrames = totalFrames;
        this.cols = cols;
        this.rows = row;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    public void setHitBox(double hitBoxOffsetX, double hitBoxOffsetY, double hitBoxWidth, double hitBoxHeight)
    {
        this.hitBoxOffsetX = hitBoxOffsetX;
        this.hitBoxOffsetY = hitBoxOffsetY;
        this.hitBoxWidth = hitBoxWidth;
        this.hitBoxHeight = hitBoxHeight;
    }

    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
    }

    public Boolean getAnimated()
    {
        return animated;
    }

    public void setAnimated(Boolean animated)
    {
        lastFrame = GameWindow.getSingleton().getRenderTime(); //To set frameJump to first image, if Animation starts later with interaction
        this.animated = animated;
    }

    public void setInteract(Boolean interact)
    {
        this.interact = interact;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getHitBoxOffsetX()
    {
        return hitBoxOffsetX;
    }

    public double getHitBoxOffsetY()
    {
        return hitBoxOffsetY;
    }

    public double getHitBoxWidth()
    {
        return hitBoxWidth;
    }

    public double getHitBoxHeight()
    {
        return hitBoxHeight;
    }

    public String getLightningSpriteName()
    {
        return lightningSpriteName;
    }

    public void setLightningSpriteName(String lightningSpriteName)
    {
        this.lightningSpriteName = lightningSpriteName;
    }

    public void setAnimationEnds(Boolean animationEnds)
    {
        this.animationEnds = animationEnds;
    }
}
