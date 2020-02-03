package Core;


import Core.Enums.Direction;
import Core.Enums.TriggerType;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Actor// implements PropertyChangeListener
{
    String className = "Actor ";
    String actorname;
    private Direction direction;
    private double velocityX;
    private double velocityY;
    private double speed = 50;
    private double interactionAreaWidth = 8;
    private double interactionAreaDistance = 30;
    private double interactionAreaOffsetX = 0;
    private double interactionAreaOffsetY = 0;


    TriggerType onAction = TriggerType.NOTHING;
    TriggerType onInRange = TriggerType.NOTHING;
    TriggerType onUpdate = TriggerType.NOTHING;
    TriggerType onIntersection = TriggerType.NOTHING;
    String onUpdateToStatus = Config.KEYWORD_transition;
    String onInRangeToStatus = Config.KEYWORD_transition;
    String onIntersectionToStatus = Config.KEYWORD_transition;
    List<Sprite> spriteList = new ArrayList<>();
    Map<String, String> statusTransitions = new HashMap<>();
    Map<String, List<SpriteData>> spriteDataMap = new HashMap<>();
    String generalStatus;
    String compoundStatus = "default";
    String dialogueFileName = "descriptions";


    public Actor(String actorname, String initGeneralStatus, Direction direction)
    {
        this.actorname = actorname;
        this.generalStatus = initGeneralStatus.toLowerCase();
        this.direction = direction;
        List<String[]> actordata;
        Path path = Paths.get("src/res/actorData/" + actorname + ".csv");
        if (Files.exists(path))
        {
            actordata = Utilities.readAllLineFromTxt("src/res/actorData/" + actorname + ".csv");
            for (String[] linedata : actordata)
            {
                //Check for keywords
                if (linedata[0].equals(Config.KEYWORD_onAction))
                {
                    TriggerType triggerType = TriggerType.getStatus(linedata[1]);
                    onAction = triggerType;
                    continue;
                }
                if (linedata[0].equals(Config.KEYWORD_onInRange))
                {
                    TriggerType triggerType = TriggerType.getStatus(linedata[1]);
                    String targetGeneralStatus = linedata[2];
                    onInRangeToStatus = targetGeneralStatus;
                    onInRange = triggerType;
                    continue;
                }
                if (linedata[0].equals(Config.KEYWORD_onUpdate))
                {
                    TriggerType triggerType = TriggerType.getStatus(linedata[1]);
                    String targetGeneralStatus = linedata[2];
                    onUpdateToStatus = targetGeneralStatus;
                    onUpdate = triggerType;
                    continue;
                }
                if (linedata[0].equals(Config.KEYWORD_onIntersection))
                {
                    TriggerType triggerType = TriggerType.getStatus(linedata[1]);
                    String targetGeneralStatus = linedata[2];
                    onIntersectionToStatus = targetGeneralStatus;
                    onIntersection = triggerType;
                    continue;
                }
                if (linedata[0].equals(Config.KEYWORD_transition))
                {
                    statusTransitions.put(linedata[1], linedata[2]);
                    continue;
                }
                if(linedata[0].equals(Config.KEYWORD_interactionArea))
                {
                    Double areaDistance = Double.parseDouble(linedata[1]);
                    Double areaWidth = Double.parseDouble(linedata[2]);
                    Double offsetX = Double.parseDouble(linedata[3]);
                    Double offsetY = Double.parseDouble(linedata[4]);
                    interactionAreaDistance = areaDistance;
                    interactionAreaWidth = areaWidth;
                    interactionAreaOffsetX = offsetX;
                    interactionAreaOffsetY = offsetY;
                    continue;
                }

                //Collect Actor Sprite Data
                SpriteData data = SpriteData.tileDefinition(linedata);
                data.animationDuration = Integer.parseInt(linedata[SpriteData.animationDurationIdx]);
                data.velocity = Integer.parseInt(linedata[SpriteData.velocityIdx]);

                String statusName = linedata[0].toLowerCase();
                if (!spriteDataMap.containsKey(statusName))
                    spriteDataMap.put(statusName, new ArrayList<>());
                spriteDataMap.get(statusName).add(data);

            }
        }
        else throw new RuntimeException("Actordata not found: " + actorname);
    }

    public void update()
    {
        if (onUpdateToStatus.equals(Config.KEYWORD_transition))
            transitionGeneralStatus();
        {
            generalStatus = onUpdateToStatus;
            updateCompoundStatus();
        }
    }

    private void changeLayer(Sprite sprite, int targetLayer)
    {
        String methodName = "changeLayer() ";
        WorldView.bottomLayer.remove(sprite);
        WorldView.middleLayer.remove(sprite);
        WorldView.topLayer.remove(sprite);
        switch (targetLayer)
        {
            case 0:
                WorldView.bottomLayer.add(sprite);
                break;
            case 1:
                WorldView.middleLayer.add(sprite);
                break;
            case 2:
                WorldView.topLayer.add(sprite);
                break;
        }
    }


    private void changeSprites()
    {
        String methodName = "changeSprites() ";
        List<SpriteData> targetSpriteData = spriteDataMap.get(compoundStatus);

        if (targetSpriteData == null)
            System.out.println(className + methodName + compoundStatus + " not found in " + spriteDataMap);

        //For all Sprites of the actor update to new Status
        for (int i = 0; i < spriteList.size(); i++)
        {
            SpriteData ts = targetSpriteData.get(i);
            Sprite toChange = spriteList.get(i);
            toChange.setImage(ts.spriteName, ts.fps, ts.totalFrames, ts.cols, ts.rows, ts.frameWidth, ts.frameHeight);
            toChange.setBlocker(ts.blocking);
            toChange.setLightningSpriteName(ts.lightningSprite);
            changeLayer(toChange, ts.heightLayer);
        }

    }

    public void actOnIntersection(Sprite otherSprite)
    {
        String methodName = "actOnIntersection() ";

        if (onIntersectionToStatus.equals(Config.KEYWORD_transition))
            transitionGeneralStatus();
        else
        {
            generalStatus = onIntersectionToStatus;
            updateCompoundStatus();
        }

    }

    public void onInRange(Sprite otherSprite)
    {
        String methodName = "onInRange() ";
        if (onInRangeToStatus.equals("transition"))
            transitionGeneralStatus();
        else
        {
            generalStatus = onInRangeToStatus;
            updateCompoundStatus();
        }
        //System.out.println(className + methodName + generalStatus + " found " + otherSprite.getName());
    }

    public void act()
    {
        String methodName = "act(): ";

        if (onAction == TriggerType.NOTHING)
            return;

        if (onAction == TriggerType.PERSISTENT)
            transitionGeneralStatus();

        if (onAction == TriggerType.TIMED)
        {
            transitionGeneralStatus();
            List<SpriteData> targetSpriteData = spriteDataMap.get(compoundStatus);
            int animationDuration = targetSpriteData.get(0).animationDuration;
            PauseTransition delay = new PauseTransition(Duration.millis(animationDuration * 1000));
            delay.setOnFinished(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent t)
                {
                    transitionGeneralStatus();
                }
            });

            delay.play();
        }

        if (onAction == TriggerType.TEXTBOX)
        {
            activateTextbox();
        }
    }

    public void activateTextbox()
    {
        String methodName = "activateDialogue() ";
        GUIController gameWindow = GameWindow.getSingleton().currentView;

        if(gameWindow instanceof WorldView)
        {
            WorldView worldView = (WorldView)gameWindow;
            worldView.textbox.readDialogue(actorname, "trig");//TODO actor defines txt file
            worldView.isTextBoxActive = true;
        }
        else
            System.out.println(className + methodName + " Game Window not instance of WorldView, cannot show Dialogue");
    }


    @Override
    public String toString()
    {
        return "Actor{" +
                "onAction='" + onAction + '\'' +
                ", Generalstatus=" + generalStatus +
                ", transitions: " + statusTransitions.toString() +
                '}';
    }

    private void transitionGeneralStatus()
    {
        String methodName = "transitionGeneralStatus() ";
        if (statusTransitions.containsKey(generalStatus))
        {
            generalStatus = statusTransitions.get(generalStatus);
        }
        else
            System.out.print(className + methodName + "No status transition found: " + generalStatus);

        updateCompoundStatus();
    }

    void updateCompoundStatus()
    {
        String methodName = "updateCompoundStatus() ";
        String oldCompoundStatus = compoundStatus;
        String newStatusString = generalStatus;

        if (!(direction == Direction.UNDEFINED))
            newStatusString = newStatusString + "-" + direction.toString().toLowerCase();

        if (isMoving())
            newStatusString = newStatusString + "-" + "moving";

        compoundStatus = newStatusString;
        if (!(oldCompoundStatus.equals(compoundStatus)))
            changeSprites();
    }

    /*
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        String methodName = "propertyChange() ";
        updateCompoundStatus();
    }
    */

    public void addSprite(Sprite sprite)
    {
        spriteList.add(sprite);
        sprite.actor = this;
        //sprite.addToListener(this);
    }

    public Direction getDirection()
    {
        return direction;
    }

    public void setDirection(Direction direction)
    {
        this.direction = direction;
        updateCompoundStatus();
    }

    public void setVelocity(double x, double y)
    {
        velocityX = x;
        velocityY = y;
        updateCompoundStatus();
    }

    public boolean isMoving()
    {
        if (velocityX != 0 || velocityY != 0)
            return true;
        else
            return false;
    }

    public double getVelocityX()
    {
        return velocityX;
    }

    public double getVelocityY()
    {
        return velocityY;
    }

    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public double getSpeed()
    {
        return speed;
    }

    public double getInteractionAreaWidth()
    {
        return interactionAreaWidth;
    }

    public double getInteractionAreaDistance()
    {
        return interactionAreaDistance;
    }

    public double getInteractionAreaOffsetX()
    {
        return interactionAreaOffsetX;
    }

    public double getInteractionAreaOffsetY()
    {
        return interactionAreaOffsetY;
    }
}
