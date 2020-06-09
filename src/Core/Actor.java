package Core;


import Core.Enums.ActorConditionType;
import Core.Enums.ActorTag;
import Core.Enums.Direction;
import Core.Enums.TriggerType;
import Core.Menus.MyersBriggsPersonality;
import Core.Menus.PersonalityContainer;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static Core.Config.*;
import static Core.Enums.ActorTag.*;

public class Actor
{
    private static final String CLASSNAME = "Actor-";
    private static final Set<String> actorDefinitionKeywords = new HashSet<>();
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

    final List<Sprite> spriteList = new ArrayList<>();
    List<ActorCondition> conditions = new ArrayList<>();
    final Map<String, String> statusTransitions = new HashMap<>();
    final Map<String, List<SpriteData>> spriteDataMap = new HashMap<>();
    String generalStatus;
    String compoundStatus = "default";
    String dialogueFileName = null;
    String dialogueStatusID = "none";
    private String collectable_type;
    String textbox_analysis_group_name = "none";
    StageMonitor stageMonitor;
    List<String> memberActorGroups = new ArrayList<>();
    Inventory inventory;
    Map<String, SensorStatus> sensorStatusMap = new HashMap<>();
    SensorStatus sensorStatus;
    public Set<ActorTag> tags = new HashSet<>();

    PersonalityContainer personalityContainer;

    public Actor(String actorFileName, String actorInGameName, String initGeneralStatus, String initSensorStatus, Direction direction)
    {
        String methodName = "Constructor ";
        inventory = new Inventory(this);

        this.actorFileName = actorFileName;
        this.actorInGameName = actorInGameName;
        this.generalStatus = initGeneralStatus.toLowerCase();
        this.direction = direction;
        List<String[]> actordata;
        Path path = Paths.get(ACTOR_DIRECTORY_PATH + actorFileName + CSV_POSTFIX);

        if (actorDefinitionKeywords.isEmpty()) //To avoid adding for each actor
        {
            actorDefinitionKeywords.add(KEYWORD_transition);
            actorDefinitionKeywords.add(KEYWORD_interactionArea);
            actorDefinitionKeywords.add(KEYWORD_dialogueFile);
            actorDefinitionKeywords.add(KEYWORD_text_box_analysis_group);
            actorDefinitionKeywords.add(KEYWORD_collectable_type);
            actorDefinitionKeywords.add(KEYWORD_sensorStatus);
            actorDefinitionKeywords.add(KEYWORD_actor_tags);
            actorDefinitionKeywords.add(KEYWORD_condition);
            actorDefinitionKeywords.add(KEYWORD_personality);
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
                }
                catch (IndexOutOfBoundsException e)
                {
                    throw new IndexOutOfBoundsException(e.getMessage() + "\n in Actorfile: " + actorFileName);
                }
            }
        }
        else throw new RuntimeException("Actordata not found: " + actorFileName);

        sensorStatus = sensorStatusMap.get(initSensorStatus);

        if (sensorStatus == null)
            System.out.println(CLASSNAME + methodName + actorInGameName + " no sensor found: " + initSensorStatus);
    }

    private boolean checkForKeywords(String[] linedata)
    {
        int keywordIdx = 0;
        String possibleKeyword = linedata[keywordIdx];
        String keyword;

        if (actorDefinitionKeywords.contains(possibleKeyword))
            keyword = possibleKeyword;
        else
            return false;

        switch (keyword)
        {
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
            case KEYWORD_sensorStatus:
                sensorStatusMap.put(linedata[1], readSensorData(linedata));
                break;
            case KEYWORD_actor_tags:
                tags.addAll(readTagData(linedata));
                break;
            case KEYWORD_condition:
                conditions.add(readCondition(linedata));
                break;
            case KEYWORD_personality:
                personalityContainer = readPersonality(linedata);
                break;
            default:
                throw new RuntimeException("Keyword unknown: " + keyword);
        }

        return true;
    }

    private PersonalityContainer readPersonality(String[] linedata)
    {
        String methodName = "readPersonality() ";
        boolean debug = false;

        if (debug)
            System.out.println(CLASSNAME + methodName + Arrays.toString(linedata));

        int personalityIdx = 1;
        int initCooperationValueIdx = 2;
        int trait_threshold_paramsIdx = 3;
        PersonalityContainer readContainer = new PersonalityContainer();
        MyersBriggsPersonality myersBriggsPersonality = MyersBriggsPersonality.getPersonality(linedata[personalityIdx]);
        Integer initCooperationValue = Integer.parseInt(linedata[initCooperationValueIdx]);
        readContainer.myersBriggsPersonality = myersBriggsPersonality;
        readContainer.cooperation = initCooperationValue;
        for (int i = trait_threshold_paramsIdx; i < linedata.length; i += 2)
        {
            Integer threshold = Integer.parseInt(linedata[i + 1]);
            readContainer.traitsThresholds.put(linedata[i], threshold);
        }

        if (debug)
            System.out.println(CLASSNAME + methodName + readContainer);
        return readContainer;
    }

    private ActorCondition readCondition(String[] linedata)
    {
        String methodName = "readCondition() ";
        boolean debug = false;
        //#condition; if sprite-status ;if sensor-status ;type ;true-sprite-status ;true-sensor-status ;false-sprite-status ;false-sensor-status	;params
        int spriteStatusConditionIdx = 1;
        int sensorStatusConditionIdx = 2;
        int actorConditionTypeIdx = 3;
        int trueSpriteStatusIdx = 4;
        int trueSensorStatusIdx = 5;
        int falseSpriteStatusIdx = 6;
        int falseSensorStatusIdx = 7;
        int paramsIdx = 8;
        ActorCondition actorCondition = new ActorCondition();
        actorCondition.spriteStatusCondition = linedata[spriteStatusConditionIdx];
        actorCondition.sensorStatusCondition = linedata[sensorStatusConditionIdx];
        actorCondition.actorConditionType = ActorConditionType.getConditionFromValue(linedata[actorConditionTypeIdx]);
        actorCondition.trueSpriteStatus = linedata[trueSpriteStatusIdx];
        actorCondition.trueSensorStatus = linedata[trueSensorStatusIdx];
        actorCondition.falseSpriteStatus = linedata[falseSpriteStatusIdx];
        actorCondition.falseSensorStatus = linedata[falseSensorStatusIdx];
        for (int i = paramsIdx; i < linedata.length; i++)
            actorCondition.params.add(linedata[i]);

        if (debug)
            System.out.println(CLASSNAME + methodName + actorCondition);
        return actorCondition;
    }

    private Set<ActorTag> readTagData(String[] linedata)
    {
        Set<ActorTag> tagDataSet = new HashSet<>();
        int startIdxTags = 1;
        for (int i = startIdxTags; i < linedata.length; i++)
        {
            tagDataSet.add(ActorTag.getType(linedata[i]));
        }
        return tagDataSet;
    }

    private SensorStatus readSensorData(String[] lineData) throws ArrayIndexOutOfBoundsException
    {
        String methodName = "readSensorData(String[]) ";
        int sensorDataNameIdx = 1;

        int onInteractionIdx = 2;
        int onInteractionToStatusIdx = 3;
        int onInteraction_TriggerSensorIdx = 4;
        int onInteraction_StatusSensorIdx = 5;

        int onInRangeIdx = 6;
        int onInRangeToStatusIdx = 7;
        int onInRange_TriggerSensorIdx = 8;
        int onInRange_StatusSensorIdx = 9;

        int onIntersectionIdx = 10;
        int onIntersectionToStatusIdx = 11;
        int onIntersection_TriggerSensorIdx = 12;
        int onIntersection_StatusSensorIdx = 13;

        int onUpdateIdx = 14;
        int onUpdateToStatusIdx = 15;
        int onUpdate_TriggerSensorIdx = 16;
        int onUpdate_StatusSensorIdx = 17;

        int onMonitorIdx = 18;
        int onMonitor_TriggerSensorIdx = 19;

        int onTextBoxIdx = 20;
        int onTextBox_TriggerSensorIdx = 21;

        SensorStatus sensorStatus = new SensorStatus(lineData[sensorDataNameIdx]);
        try
        {
            sensorStatus.onInteraction = TriggerType.getStatus(lineData[onInteractionIdx]);
            sensorStatus.onInteractionToStatus = lineData[onInteractionToStatusIdx];
            sensorStatus.onInteraction_TriggerSensor = TriggerType.getStatus(lineData[onInteraction_TriggerSensorIdx]);
            sensorStatus.onInteraction_StatusSensor = lineData[onInteraction_StatusSensorIdx];

            sensorStatus.onInRange = TriggerType.getStatus(lineData[onInRangeIdx]);
            sensorStatus.onInRangeToStatus = lineData[onInRangeToStatusIdx];
            sensorStatus.onInRange_TriggerSensor = TriggerType.getStatus(lineData[onInRange_TriggerSensorIdx]);
            sensorStatus.onInRangeToStatusSensorStatus = lineData[onInRange_StatusSensorIdx];

            sensorStatus.onIntersection = TriggerType.getStatus(lineData[onIntersectionIdx]);
            sensorStatus.onIntersectionToStatus = lineData[onIntersectionToStatusIdx];
            sensorStatus.onIntersection_TriggerSensor = TriggerType.getStatus(lineData[onIntersection_TriggerSensorIdx]);
            sensorStatus.onIntersection_StatusSensor = lineData[onIntersection_StatusSensorIdx];

            sensorStatus.onUpdate = TriggerType.getStatus(lineData[onUpdateIdx]);
            sensorStatus.onUpdateToStatus = lineData[onUpdateToStatusIdx];
            sensorStatus.onUpdate_TriggerSensor = TriggerType.getStatus(lineData[onUpdate_TriggerSensorIdx]);
            sensorStatus.onUpdate_StatusSensor = lineData[onUpdate_StatusSensorIdx];

            sensorStatus.onMonitorSignal = TriggerType.getStatus(lineData[onMonitorIdx]);
            sensorStatus.onMonitor_TriggerSensor = TriggerType.getStatus(lineData[onMonitor_TriggerSensorIdx]);

            sensorStatus.onTextBoxSignal_SpriteTrigger = TriggerType.getStatus(lineData[onTextBoxIdx]);
            sensorStatus.onTextBox_TriggerSensor = TriggerType.getStatus(lineData[onTextBox_TriggerSensorIdx]);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException(actorInGameName + "\n" + e.getMessage());
        }


        return sensorStatus;
    }

    public void onUpdate(Long currentNanoTime)
    {
        //No lastInteraction time update, just resets if not used. like a automatic door
        String methodName = "onUpdate(Long) ";
        double elapsedTimeSinceLastInteraction = (currentNanoTime - lastInteraction) / 1000000000.0;
        if (elapsedTimeSinceLastInteraction > TIME_BETWEEN_INTERACTIONS)
        {
            //Sprite
            if (sensorStatus.onUpdate != TriggerType.NOTHING && !sensorStatus.onUpdateToStatus.equals(generalStatus))
                evaluateTriggerType(sensorStatus.onUpdate, sensorStatus.onUpdateToStatus, null);

            //SensorStatus
            if (sensorStatus.onUpdate_TriggerSensor != TriggerType.NOTHING && !sensorStatus.onUpdate_StatusSensor.equals(sensorStatus.statusName))
                setSensorStatus(sensorStatus.onUpdate_StatusSensor);
        }
    }

    private void updateStatusFromConditions(Sprite activeSprite)
    {
        String methodName = "updateStatusFromConditions()";
        boolean debug = false;
        for (ActorCondition condition : conditions)
        {
            if //check pre-condition
            (
                    (generalStatus.equals(condition.spriteStatusCondition) || condition.spriteStatusCondition.equals("*"))
                            &&
                            (sensorStatus.statusName.equals(condition.sensorStatusCondition) || condition.sensorStatusCondition.equals("*"))
            )
            {
                if (condition.evaluate(activeSprite.actor, this))
                //condition met
                {
                    if (!condition.trueSpriteStatus.equals("*"))
                    {
                        generalStatus = condition.trueSpriteStatus;
                        updateCompoundStatus();
                    }
                    if (!condition.trueSensorStatus.equals("*"))
                        setSensorStatus(condition.trueSensorStatus);

                    if (debug)
                        System.out.println(CLASSNAME + methodName + " condition met " + generalStatus + " " + sensorStatus.statusName);
                }
                else
                //condition not met
                {
                    if (!condition.falseSensorStatus.equals("*"))
                    {
                        generalStatus = condition.falseSpriteStatus;
                        updateCompoundStatus();
                    }
                    if (!condition.falseSensorStatus.equals("*"))
                        setSensorStatus(condition.falseSensorStatus);

                    if (debug)
                        System.out.println(CLASSNAME + methodName + "Not met " + generalStatus + " " + sensorStatus.statusName);
                }
            }
        }
    }

    public void onInteraction(Sprite activeSprite, Long currentNanoTime)
    {
        String methodName = "onInteraction(Sprite, Long) ";
        double elapsedTimeSinceLastInteraction = (currentNanoTime - lastInteraction) / 1000000000.0;

        if (elapsedTimeSinceLastInteraction > TIME_BETWEEN_INTERACTIONS)
        {
            updateStatusFromConditions(activeSprite);

            //react
            evaluateTriggerType(sensorStatus.onInteraction, sensorStatus.onInteractionToStatus, activeSprite.actor);
            setLastInteraction(currentNanoTime);
        }
    }

    public void onMonitorSignal(String newCompoundStatus)
    {
        String methodName = "onMonitorSignal() ";
        boolean debug = false;
        if (sensorStatus.onMonitorSignal == null)
            System.out.println(CLASSNAME + methodName + "OnMonitorSignal not set");

        if (debug)
            System.out.println(CLASSNAME + methodName + actorInGameName + " set to " + newCompoundStatus);
        evaluateTriggerType(sensorStatus.onMonitorSignal, newCompoundStatus, null);
    }

    public void onTextboxSignal(String newCompoundStatus)
    {
        String methodName = "onTextboxSignal() ";
        //System.out.println(CLASSNAME + methodName + sensorStatus.statusName + " " + generalStatus + " " + newCompoundStatus + " " + sensorStatus.onTextBoxSignal_SpriteTrigger);
        evaluateTriggerType(sensorStatus.onTextBoxSignal_SpriteTrigger, newCompoundStatus, null);
    }

    public void onIntersection(Sprite detectedSprite, Long currentNanoTime)
    {
        String methodName = "onIntersection() ";
        boolean debug = false;
        double elapsedTimeSinceLastInteraction = (currentNanoTime - lastInteraction) / 1000000000.0;

        //Check if detection is relevant
        boolean actorRelevant = true;
        if (detectedSprite.actor == null ||
                (
                        (tags.contains(AUTOMATED_DOOR) && !detectedSprite.actor.tags.contains(AUTOMATED_DOOR_DETECTABLE)) //is door and other detectable
                                || tags.contains(BECOME_TRANSPARENT) && !detectedSprite.actor.tags.contains(AUTOMATED_DOOR_DETECTABLE) //for roof
                                || tags.contains(DETECTS_PLAYER) && !detectedSprite.actor.tags.contains(PLAYER) //for trigger
                )
        )
            actorRelevant = false;

        //trigger
        if (elapsedTimeSinceLastInteraction > TIME_BETWEEN_INTERACTIONS && actorRelevant)
        {
            if (debug)
                System.out.println(CLASSNAME + methodName + actorFileName + " onIntersection " + detectedSprite.getName());

            //Sprite Status
            if (sensorStatus.onIntersection != TriggerType.NOTHING)
            {
                evaluateTriggerType(sensorStatus.onIntersection, sensorStatus.onIntersectionToStatus, detectedSprite.actor);
            }

            //SensorStatus
            if (sensorStatus.onIntersection_TriggerSensor != TriggerType.NOTHING) // && !sensorStatus.onInteraction_StatusSensor.equals(sensorStatus.statusName)
                setSensorStatus(sensorStatus.onIntersection_StatusSensor);
            setLastInteraction(currentNanoTime);
        }
    }

    public void onInRange(Sprite detectedSprite, Long currentNanoTime)
    {
        String methodName = "onInRange(Sprite, Long) ";
        boolean debug = false;
        double elapsedTimeSinceLastInteraction = (currentNanoTime - lastInteraction) / 1000000000.0;

        //TODO general lookup
        if (
                (tags.contains(AUTOMATED_DOOR) && !detectedSprite.actor.tags.contains(AUTOMATED_DOOR_DETECTABLE))
                        || tags.contains(BECOME_TRANSPARENT) && !detectedSprite.actor.tags.contains(AUTOMATED_DOOR_DETECTABLE)
        )
            return;

        if (elapsedTimeSinceLastInteraction > TIME_BETWEEN_INTERACTIONS)
        {
            if (debug)
                System.out.println(CLASSNAME + methodName + actorFileName + " onInRange " + detectedSprite.getName());
            evaluateTriggerType(sensorStatus.onInRange, sensorStatus.onInRangeToStatus, detectedSprite.actor);
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
        {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, List<SpriteData>> entry : spriteDataMap.entrySet())
                stringBuilder.append("\t").append(entry.getKey()).append("\n");
            throw new RuntimeException(compoundStatus + " not found in \n" + stringBuilder.toString());
        }

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
        boolean debug = false;

        //Do lookup (status is toggled from definition of actorfile)
        if (targetStatusField.equals(Config.KEYWORD_transition))
            transitionGeneralStatus();
        else
            //Status is set directly
            generalStatus = targetStatusField.toLowerCase();

        String influencedOfGroup = stageMonitor.isDependentOnGroup(memberActorGroups);
        if (influencedOfGroup != null)
        {
            //Check if status is valid dependent on influencing system
            generalStatus = stageMonitor.checkIfStatusIsValid(generalStatus, influencedOfGroup);
        }

        if (debug)
            System.out.println(CLASSNAME + methodName + actorInGameName + " changed to status: " + generalStatus);

        updateCompoundStatus();
    }

    //React on outside sensor
    private void evaluateTriggerType(TriggerType triggerType, String targetStatusField, Actor activeSprite)
    {
        String methodName = "evaluateTriggerType() ";
        switch (triggerType)
        {
            case NOTHING:
                System.out.println(CLASSNAME + methodName + actorInGameName + " triggered without trigger type");
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
                collect(activeSprite);
                break;
        }
    }

    private void collect(Actor collectingActor)
    {
        String methodName = "collect(String) ";
        Image usedImage = spriteList.get(0).baseimage;
        collectingActor.inventory.addItem(generalStatus, collectable_type, actorInGameName, usedImage);
        WorldView.toRemove.addAll(spriteList);
    }

    public void setSensorStatus(String sensorStatusString)
    {
        String methodName = "setSensorStatus(String) ";
        boolean debug = false;

        if (sensorStatusMap.get(sensorStatusString) == null)
            throw new RuntimeException("Sensor Status not defined: " + sensorStatusString + " at actor " + actorFileName);

        if (debug)
            System.out.println(CLASSNAME + methodName + "set sensor from " + sensorStatus.statusName + " to " + sensorStatusString);
        if (sensorStatusMap.get(sensorStatusString) != sensorStatus)
            this.sensorStatus = sensorStatusMap.get(sensorStatusString);
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
            if (sensorStatus.onInteraction.equals(TriggerType.TEXTBOX_ANALYSIS))
            {
                String analyzedGroupName = null;
                List<Actor> analyzedGroup = null;
                try
                {
                    analyzedGroupName = textbox_analysis_group_name;//set in actor file
                    analyzedGroup = stageMonitor.groupIdToActorGroupMap.get(analyzedGroupName).getSystemMembers();
                    WorldView.textbox.groupAnalysis(analyzedGroup, this);
                }
                catch (NullPointerException e)
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
        boolean debug = true;
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
            if (actorFileName.equals("desinfection"))
                System.out.println(CLASSNAME + methodName + " set sprite status from " + oldCompoundStatus + " to " + compoundStatus);
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

    public PersonalityContainer getPersonalityContainer()
    {
        return personalityContainer;
    }

    public String getActorFileName()
    {
        return actorFileName;
    }

    public static String getCLASSNAME()
    {
        return CLASSNAME;
    }

    public static Set<String> getActorDefinitionKeywords()
    {
        return actorDefinitionKeywords;
    }

    public String getActorInGameName()
    {
        return actorInGameName;
    }

    public List<Sprite> getSpriteList()
    {
        return spriteList;
    }

    public List<ActorCondition> getConditions()
    {
        return conditions;
    }

    public Map<String, String> getStatusTransitions()
    {
        return statusTransitions;
    }

    public Map<String, List<SpriteData>> getSpriteDataMap()
    {
        return spriteDataMap;
    }

    public String getGeneralStatus()
    {
        return generalStatus;
    }

    public String getCompoundStatus()
    {
        return compoundStatus;
    }

    public String getDialogueFileName()
    {
        return dialogueFileName;
    }

    public String getDialogueStatusID()
    {
        return dialogueStatusID;
    }

    public String getCollectable_type()
    {
        return collectable_type;
    }

    public String getTextbox_analysis_group_name()
    {
        return textbox_analysis_group_name;
    }

    public StageMonitor getStageMonitor()
    {
        return stageMonitor;
    }

    public List<String> getMemberActorGroups()
    {
        return memberActorGroups;
    }

    public Inventory getInventory()
    {
        return inventory;
    }

    public Map<String, SensorStatus> getSensorStatusMap()
    {
        return sensorStatusMap;
    }

    public SensorStatus getSensorStatus()
    {
        return sensorStatus;
    }

    public Set<ActorTag> getTags()
    {
        return tags;
    }
}
