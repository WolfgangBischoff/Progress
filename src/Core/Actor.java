package Core;


import Core.Enums.Direction;
import Core.Enums.TriggerType;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static Core.Config.*;

public class Actor
{
    private static final String CLASSNAME = "Actor-";
    String actorFileName;
    String actorInGameName;
    private Direction direction;
    private double velocityX;
    private double velocityY;
    private double speed = 50;
    private Long lastInteraction = 0L;
    private double interactionAreaWidth = 8;
    private double interactionAreaDistance = 30;
    private double interactionAreaOffsetX = 0;
    private double interactionAreaOffsetY = 0;


    TriggerType onInteraction = TriggerType.NOTHING;
    TriggerType onInRange = TriggerType.NOTHING;
    TriggerType onUpdate = TriggerType.NOTHING;
    TriggerType onIntersection = TriggerType.NOTHING;
    TriggerType onMonitorSignal = null;
    TriggerType onTextBoxSignal = TriggerType.NOTHING;
    static final Set<String> actorDefinitionKeywords = new HashSet<>();
    String onInteractionToStatus = Config.KEYWORD_transition;
    String onUpdateToStatus = Config.KEYWORD_transition;
    String onInRangeToStatus = Config.KEYWORD_transition;
    String onIntersectionToStatus = Config.KEYWORD_transition;
    //String onMonitorSignalToStatus = Config.KEYWORD_transition;
    final List<Sprite> spriteList = new ArrayList<>();
    final Map<String, String> statusTransitions = new HashMap<>();
    final Map<String, List<SpriteData>> spriteDataMap = new HashMap<>();
    String generalStatus;
    String compoundStatus = "default";
    String dialogueFileName = "descriptions";
    String dialogueStatusID = "none";
    private String collectable_type;
    String textbox_analysis_group_name = "none";

    StageMonitor stageMonitor;
    List<String> memberActorGroups = new ArrayList<>();

    Inventory inventory;

    public Actor(String actorFileName, String actorInGameName, String initGeneralStatus, Direction direction)
    {
        inventory = new Inventory(this);

        this.actorFileName = actorFileName;
        this.actorInGameName = actorInGameName;
        this.generalStatus = initGeneralStatus.toLowerCase();
        this.direction = direction;
        List<String[]> actordata;
        Path path = Paths.get(ACTOR_DIRECTORY_PATH + actorFileName + CSV_POSTFIX);

        if(actorDefinitionKeywords.isEmpty()) //To avoid adding for each actor
        {
            actorDefinitionKeywords.add(KEYWORD_onInteraction);
            actorDefinitionKeywords.add(KEYWORD_onInRange);
            actorDefinitionKeywords.add(KEYWORD_onUpdate);
            actorDefinitionKeywords.add(KEYWORD_onIntersection);
            actorDefinitionKeywords.add(KEYWORD_onMonitor);
            actorDefinitionKeywords.add(KEYWORD_transition);
            actorDefinitionKeywords.add(KEYWORD_interactionArea);
            actorDefinitionKeywords.add(KEYWORD_dialogueFile);
            actorDefinitionKeywords.add(KEYWORD_onTextBox);
            actorDefinitionKeywords.add(KEYWORD_text_box_analysis_group);
            actorDefinitionKeywords.add(KEYWORD_collectable_type);
        }


        if (Files.exists(path))
        {
            actordata = Utilities.readAllLineFromTxt(ACTOR_DIRECTORY_PATH + actorFileName + CSV_POSTFIX);
            for (String[] linedata : actordata)
            {
                if (checkForKeywords(linedata))
                    continue;

                //Collect Actor Sprite Data
                try
                {
                    SpriteData data = SpriteData.tileDefinition(linedata);
                    data.animationDuration = Double.parseDouble(linedata[SpriteData.animationDurationIdx]);
                    data.velocity = Integer.parseInt(linedata[SpriteData.velocityIdx]);
                    data.dialogueID = linedata[SpriteData.dialogueIDIdx];
                    data.animationEnds = Boolean.parseBoolean(linedata[SpriteData.animationEndsIdx]);
                    String statusName = linedata[0].toLowerCase();
                    if (!spriteDataMap.containsKey(statusName))
                        spriteDataMap.put(statusName, new ArrayList<>());
                    spriteDataMap.get(statusName).add(data);
                } catch (IndexOutOfBoundsException e)
                {
                    throw new IndexOutOfBoundsException(e.getMessage() + "\n in Actorfile: " + actorFileName);
                }
            }
        }
        else throw new RuntimeException("Actordata not found: " + actorFileName);
    }

    private boolean checkForKeywords(String[] linedata)
    {
        int keywordIdx = 0;
        int triggerTypeIdx = 1;
        int targetStatusIdx = 2;
        String possibleKeyword = linedata[keywordIdx];
        String keyword;

        if (actorDefinitionKeywords.contains(possibleKeyword))
            keyword = possibleKeyword;
        else
            return false;

        switch (keyword)
        {
            case KEYWORD_onInteraction:
                onInteraction = TriggerType.getStatus(linedata[triggerTypeIdx]);
                onInteractionToStatus = linedata[targetStatusIdx];
                break;
            case KEYWORD_onInRange:
                onInRange = TriggerType.getStatus(linedata[triggerTypeIdx]);
                onInRangeToStatus = linedata[targetStatusIdx];
                break;
            case KEYWORD_onUpdate:
                onUpdate = TriggerType.getStatus(linedata[triggerTypeIdx]);
                onUpdateToStatus = linedata[targetStatusIdx];
                break;
            case KEYWORD_onIntersection:
                onIntersection = TriggerType.getStatus(linedata[triggerTypeIdx]);
                onIntersectionToStatus = linedata[targetStatusIdx];
                break;
            case KEYWORD_onMonitor:
                onMonitorSignal = TriggerType.getStatus(linedata[triggerTypeIdx]);
                //New status is set by StageMonitor
                break;
            case KEYWORD_onTextBox:
                onTextBoxSignal = TriggerType.getStatus(linedata[triggerTypeIdx]); //Target status provided by textfile
                break;
            case KEYWORD_transition:
                statusTransitions.put(linedata[1], linedata[2]);// old/new status
                break;
            case KEYWORD_interactionArea:
                double areaDistance = Double.parseDouble(linedata[1]);
                double areaWidth = Double.parseDouble(linedata[2]);
                double offsetX = Double.parseDouble(linedata[3]);
                double offsetY = Double.parseDouble(linedata[4]);
                interactionAreaDistance = areaDistance;
                interactionAreaWidth = areaWidth;
                interactionAreaOffsetX = offsetX;
                interactionAreaOffsetY = offsetY;
                break;
            case KEYWORD_dialogueFile:
                dialogueFileName = linedata[1];
                break;
            case KEYWORD_text_box_analysis_group:
                textbox_analysis_group_name = linedata[1];
                break;
            case KEYWORD_collectable_type:
                collectable_type = linedata[1];
                break;
            default:
                throw new RuntimeException("Keyword unknown: " + keyword);
        }
        return true;
    }

    public void onUpdate(Long currentNanoTime)
    {
        //No lastInteraction time update, just resets if not used. like a automatic door
        String methodName = "onUpdate() ";
        double elapsedTimeSinceLastInteraction = (currentNanoTime - lastInteraction) / 1000000000.0;
        if (elapsedTimeSinceLastInteraction > TIME_BETWEEN_INTERACTIONS)
        {
            evaluateTriggerType(onUpdate, onUpdateToStatus, null);
        }
    }

    public void onInteraction(Sprite activeSprite, Long currentNanoTime)
    {
        String methodName = "onInteraction() ";
        double elapsedTimeSinceLastInteraction = (currentNanoTime - lastInteraction) / 1000000000.0;

        if (elapsedTimeSinceLastInteraction > TIME_BETWEEN_INTERACTIONS)
        {
            evaluateTriggerType(onInteraction, onInteractionToStatus, activeSprite.actor);
            setLastInteraction(currentNanoTime);
        }
    }

    public void onMonitorSignal(String newCompoundStatus)
    {
        String methodName = "onMonitorSignal() ";
        if (onMonitorSignal == null)
            System.out.println(CLASSNAME + methodName + "OnMonitorSignal not set");
        evaluateTriggerType(onMonitorSignal, newCompoundStatus, null);
    }

    public void onTextboxSignal(String newCompoundStatus)
    {
        String methodName = "onMonitorSignal() ";
        evaluateTriggerType(onTextBoxSignal, newCompoundStatus, null);
    }

    public void onIntersection(Sprite activeSprite, Long currentNanoTime)
    {
        String methodName = "onIntersection() ";
        double elapsedTimeSinceLastInteraction = (currentNanoTime - lastInteraction) / 1000000000.0;
        if (elapsedTimeSinceLastInteraction > TIME_BETWEEN_INTERACTIONS)
        {
            evaluateTriggerType(onIntersection, onIntersectionToStatus, activeSprite.actor);
            setLastInteraction(currentNanoTime);
        }
    }

    public void onInRange(Sprite activeSprite, Long currentNanoTime)
    {
        String methodName = "onInRange() ";
        double elapsedTimeSinceLastInteraction = (currentNanoTime - lastInteraction) / 1000000000.0;
        if (elapsedTimeSinceLastInteraction > TIME_BETWEEN_INTERACTIONS)
        {
            //System.out.println(classname + methodName + elapsedTimeSinceLastInteraction);
            evaluateTriggerType(onInRange, onInRangeToStatus, activeSprite.actor);
            setLastInteraction(currentNanoTime);
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
        List<SpriteData> targetSpriteData = spriteDataMap.get(compoundStatus.toLowerCase());

        if (targetSpriteData == null)
            System.out.println(CLASSNAME + methodName + compoundStatus + " not found in " + spriteDataMap);

        //For all Sprites of the actor onUpdate to new Status
        for (int i = 0; i < spriteList.size(); i++)
        {
            SpriteData ts = targetSpriteData.get(i);
            Sprite toChange = spriteList.get(i);
            toChange.setImage(ts.spriteName, ts.fps, ts.totalFrames, ts.cols, ts.rows, ts.frameWidth, ts.frameHeight);
            toChange.setBlocker(ts.blocking);
            toChange.setLightningSpriteName(ts.lightningSprite);
            toChange.setAnimationEnds(ts.animationEnds);
            changeLayer(toChange, ts.heightLayer);
            dialogueStatusID = ts.dialogueID;
        }

    }

    private void evaluateTargetStatus(String targetStatusField)
    {
        String methodName = "evaluateTargetStatus(String)";

        //Do lookup (status is toggled from definition of actorfile)
        if (targetStatusField.equals(Config.KEYWORD_transition))
            transitionGeneralStatus();
        else
            //Status is set directly
            generalStatus = targetStatusField.toLowerCase();

        String influencedOfGroup = stageMonitor.isDependentOnGroup(memberActorGroups);
        if(influencedOfGroup != null)
        {
            //Check if status is valid dependent on influencing system
            generalStatus = stageMonitor.checkIfStatusIsValid(generalStatus, influencedOfGroup);
        }


        updateCompoundStatus();
    }

    //React on outside sensor
    private void evaluateTriggerType(TriggerType triggerType, String targetStatusField, Actor activeActor)
    {
        switch (triggerType)
        {
            case NOTHING:
                return;
                //Status Changes
            case PERSISTENT:
                evaluateTargetStatus(targetStatusField);
                break;
            case PERSISTENT_TEXT:
                evaluateTargetStatus(targetStatusField);
                activateTextbox();
                break;
            case TIMED_TEXT:
                evaluateTargetStatus(targetStatusField);
                playTimedStatus();
                activateTextbox();
                break;
            case TIMED:
                evaluateTargetStatus(targetStatusField);
                playTimedStatus();
                break;
                //Activate Textbox
            case TEXTBOX:
            case TEXTBOX_ANALYSIS:
                activateTextbox();
                break;
                //Get Removed from Game
            case COLLECTABLE:
                collect(activeActor);
                break;
        }
    }

    private void collect(Actor collectingActor)
    {
        String methodName = "collect(String) ";
        collectingActor.inventory.addItem(generalStatus, collectable_type);
        WorldView.toRemove.addAll(spriteList);
    }

    private void playTimedStatus()
    {
        String methodName = "playTimedStatus() ";
        List<SpriteData> targetSpriteData = spriteDataMap.get(compoundStatus.toLowerCase());

        if (targetSpriteData == null)
            System.out.println(CLASSNAME + methodName + compoundStatus + " not found in " + spriteDataMap);

        double animationDuration = targetSpriteData.get(0).animationDuration;
        PauseTransition delay = new PauseTransition(Duration.millis(animationDuration * 1000));
        delay.setOnFinished(t ->
        {
            transitionGeneralStatus();
            updateCompoundStatus();
        });

        delay.play();
    }

    public void activateTextbox()
    {
        String methodName = "activateDialogue() ";
        GUIController gameWindow = GameWindow.getSingleton().currentView;

        if (gameWindow instanceof WorldView)
        {
            if (onInteraction.equals(TriggerType.TEXTBOX_ANALYSIS))
            {
                String analyzedGroupName = null;
                List<Actor> analyzedGroup = null;
                try
                {
                    analyzedGroupName = textbox_analysis_group_name;//set in actor file
                    analyzedGroup = stageMonitor.groupIdToActorGroupMap.get(analyzedGroupName).getSystemMembers();
                    WorldView.textbox.groupAnalysis(analyzedGroup, this);
                } catch (NullPointerException e)
                {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (stageMonitor == null)
                        stringBuilder.append("\nStageMonitor is null");
                    if (analyzedGroupName == null)
                        stringBuilder.append("\nAnalyzed group is null: " + memberActorGroups.get(0));
                    if (analyzedGroup == null)
                        stringBuilder.append("\nDependent group does not exist or is empty: " + analyzedGroupName);

                    throw new NullPointerException(stringBuilder.toString());
                }

            }
            else
                WorldView.textbox.startConversation(this);
            WorldView.isTextBoxActive = true;
        }
        else
            System.out.println(CLASSNAME + methodName + " Game Window not instance of WorldView, cannot show Dialogue");
    }

    private void transitionGeneralStatus()
    {
        String methodName = "transitionGeneralStatus() ";
        if (statusTransitions.containsKey(generalStatus))
        {
            generalStatus = statusTransitions.get(generalStatus);
        }
        else
            System.out.println(CLASSNAME + methodName + "No status transition found for " + actorFileName + " " + generalStatus);
    }

    void updateCompoundStatus()
    {
        String methodName = "updateCompoundStatus() ";
        String oldCompoundStatus = compoundStatus;
        String newStatusString = generalStatus;


        //GeneralStatus - [DIRECTION] - [MOVING]
        if (!(direction == Direction.UNDEFINED))
            newStatusString = newStatusString + "-" + direction.toString().toLowerCase();
        if (isMoving())
            newStatusString = newStatusString + "-" + "moving";
        compoundStatus = newStatusString;

        if (!(oldCompoundStatus.equals(compoundStatus)))
        {
            changeSprites();
        }

        //If is part of a group
        if (stageMonitor != null)
        {
            stageMonitor.sendSignalFrom(memberActorGroups);
        }
    }

    @Override
    public String toString()
    {
        return actorInGameName;
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
        return velocityX != 0 || velocityY != 0;
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

    public void setLastInteraction(Long value)
    {
        String methodName = "setLastInteraktion(Long)";
        boolean debugmode = false;
        lastInteraction = value;

        if (debugmode)
        {
            System.out.println(CLASSNAME + methodName + actorInGameName + " set last interaction.");
            for (StackTraceElement ste : Thread.currentThread().getStackTrace())
            {
                System.out.println(ste);
            }
        }


    }

    public Long getLastInteraction()
    {
        return lastInteraction;
    }
}
