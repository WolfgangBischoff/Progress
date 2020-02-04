package Core;


import Core.Enums.Direction;
import Core.Enums.TriggerType;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static Core.Config.*;

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


    TriggerType onInteraction = TriggerType.NOTHING;
    TriggerType onInRange = TriggerType.NOTHING;
    TriggerType onUpdate = TriggerType.NOTHING;
    TriggerType onIntersection = TriggerType.NOTHING;
    static Set<String> actorDefinitionKeywords = new HashSet<>();
    String onUpdateToStatus = Config.KEYWORD_transition;
    String onInRangeToStatus = Config.KEYWORD_transition;
    String onIntersectionToStatus = Config.KEYWORD_transition;
    List<Sprite> spriteList = new ArrayList<>();
    Map<String, String> statusTransitions = new HashMap<>();
    Map<String, List<SpriteData>> spriteDataMap = new HashMap<>();
    String generalStatus;
    String compoundStatus = "default";
    String dialogueFileName = "descriptions";
    String dialogueStatusID = "none";


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
                actorDefinitionKeywords.add(KEYWORD_onAction);
                actorDefinitionKeywords.add(KEYWORD_onInRange);
                actorDefinitionKeywords.add(KEYWORD_onUpdate);
                actorDefinitionKeywords.add(KEYWORD_onIntersection);
                actorDefinitionKeywords.add(KEYWORD_transition);
                actorDefinitionKeywords.add(KEYWORD_interactionArea);
                actorDefinitionKeywords.add(KEYWORD_diologueFile);
                if(checkForKeywords(linedata))
                    continue;
                //Check for keywords
                /*
                if (linedata[0].equals(Config.KEYWORD_onAction))
                {
                    TriggerType triggerType = TriggerType.getStatus(linedata[1]);
                    onInteraction = triggerType;
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
                if(linedata[0].equals(Config.KEYWORD_diologueFile))
                {
                    dialogueFileName = linedata[1];
                    continue;
                }*/

                //Collect Actor Sprite Data
                SpriteData data = SpriteData.tileDefinition(linedata);
                data.animationDuration = Integer.parseInt(linedata[SpriteData.animationDurationIdx]);
                data.velocity = Integer.parseInt(linedata[SpriteData.velocityIdx]);
                data.dialogueID = linedata[SpriteData.dialogueIDIdx];

                String statusName = linedata[0].toLowerCase();
                if (!spriteDataMap.containsKey(statusName))
                    spriteDataMap.put(statusName, new ArrayList<>());
                spriteDataMap.get(statusName).add(data);

            }
        }
        else throw new RuntimeException("Actordata not found: " + actorname);
    }

    private boolean checkForKeywords(String[] linedata)
    {
        int keywordIdx = 0;
        int triggerTypeIdx = 1;
        int targetStatusIdx = 2;
        String possibleKeyword = linedata[keywordIdx];
        String keyword;
        if (actorDefinitionKeywords.contains(possibleKeyword))
        {
            keyword = possibleKeyword;
        }
        else
        {
            return false;
        }

        switch (keyword)
        {
            case KEYWORD_onAction:
                onInteraction = TriggerType.getStatus(linedata[triggerTypeIdx]);
                return true;
            case KEYWORD_onInRange:
                onInRange = TriggerType.getStatus(linedata[triggerTypeIdx]);
                onInRangeToStatus = linedata[targetStatusIdx];
                return true;
            case KEYWORD_onUpdate:
                onUpdate = TriggerType.getStatus(linedata[triggerTypeIdx]);
                onUpdateToStatus = linedata[targetStatusIdx];
                return true;
            case KEYWORD_onIntersection:
                onIntersection = TriggerType.getStatus(linedata[triggerTypeIdx]);
                onIntersectionToStatus = linedata[targetStatusIdx];
                return true;
            case KEYWORD_transition:
                statusTransitions.put(linedata[1], linedata[2]);// old/new status
                return true;
            case KEYWORD_interactionArea:
                Double areaDistance = Double.parseDouble(linedata[1]);
                Double areaWidth = Double.parseDouble(linedata[2]);
                Double offsetX = Double.parseDouble(linedata[3]);
                Double offsetY = Double.parseDouble(linedata[4]);
                interactionAreaDistance = areaDistance;
                interactionAreaWidth = areaWidth;
                interactionAreaOffsetX = offsetX;
                interactionAreaOffsetY = offsetY;
                return true;
            case KEYWORD_diologueFile:
                dialogueFileName = linedata[1];
                return true;
            default:
                throw new RuntimeException("Keyword unknown: " + keyword);
        }
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


            dialogueStatusID = ts.dialogueID;
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

    public void actOnInteraction()
    {
        String methodName = "actOnInteraction(): ";

        if (onInteraction == TriggerType.NOTHING)
            return;

        if (onInteraction == TriggerType.PERSISTENT)
            transitionGeneralStatus();

        if (onInteraction == TriggerType.TIMED)
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

        if (onInteraction == TriggerType.TEXTBOX)
        {
            activateTextbox();
        }
    }

    public void activateTextbox()
    {
        String methodName = "activateDialogue() ";
        GUIController gameWindow = GameWindow.getSingleton().currentView;

        if (gameWindow instanceof WorldView)
        {
            WorldView worldView = (WorldView) gameWindow;
            worldView.textbox.readDialogue(dialogueFileName, dialogueStatusID);//TODO actor defines txt file
            worldView.isTextBoxActive = true;
        }
        else
            System.out.println(className + methodName + " Game Window not instance of WorldView, cannot show Dialogue");
    }


    @Override
    public String toString()
    {
        return "Actor{" +
                "onInteraction='" + onInteraction + '\'' +
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
            System.out.println(className + methodName + "No status transition found for " +actorname + " " + generalStatus);

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


    public void addSprite(Sprite sprite)
    {
        spriteList.add(sprite);
        sprite.actor = this;
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
