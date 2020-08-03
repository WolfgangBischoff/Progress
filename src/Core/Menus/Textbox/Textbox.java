package Core.Menus.Textbox;

import Core.*;
import Core.Menus.DiscussionGame.DiscussionGame;
import Core.Menus.PersonalityScreenController;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import static Core.Config.*;

public class Textbox
{
    private static final String CLASSNAME = "Textbox-";
    private static double TEXT_BOX_WIDTH = TEXTBOX_WIDTH;
    private static double TEXT_BOX_HEIGHT = TEXTBOX_HEIGHT;
    Canvas textboxCanvas = new Canvas(TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT);
    GraphicsContext textboxGc = textboxCanvas.getGraphicsContext2D();
    WritableImage textboxImage;
    Dialogue readDialogue;
    Element dialogueFileRoot;
    int messageIdx = 0;
    int backgroundOffsetX = 16;
    int backgroundOffsetYDecorationTop = 10;
    int backgroundOffsetYTalkIcon = 50;
    int backgroundOffsetYDecorationBtm = 10;
    Color background = Color.rgb(60, 90, 85);
    final int firstLineOffsetY = backgroundOffsetYDecorationTop + backgroundOffsetYTalkIcon + 20;
    final int xOffsetTextLine = 40;
    final int maxDigitsInLine = 38;
    String nextDialogueID = null;
    List<String> lineSplitMessage;
    Integer markedOption = 0;
    Actor actorOfDialogue;
    Point2D mousePosRelativeToTextboxOverlay = null;

    //TalkIcon
    int talkIconWidth = 280;
    int talkIconHeight = 100;
    Rectangle2D talkIcon = new Rectangle2D(TEXT_BOX_WIDTH - talkIconWidth, 0, talkIconWidth, talkIconHeight);
    boolean isTalkIconHovered = false;

    Image cornerTopLeft;
    Image cornerBtmRight;
    Image characterButton;

    public Textbox()
    {
        cornerTopLeft = new Image(IMAGE_DIRECTORY_PATH + "txtbox/textboxTL.png");
        cornerBtmRight = new Image(IMAGE_DIRECTORY_PATH + "txtbox/textboxBL.png");
        characterButton = new Image("Core/Menus/Textbox/characterMenuButtonTR.png");
    }

    public void startConversation(Actor actorParam)
    {
        String methodName = "readDialogue() ";
        actorOfDialogue = actorParam;
        dialogueFileRoot = Utilities.readXMLFile(DIALOGUE_FILE_PATH + actorOfDialogue.getDialogueFileName() + ".xml");
        readDialogue = readDialogue(actorOfDialogue.getDialogueStatusID());
        drawTextbox();

        if (actorOfDialogue.getPersonalityContainer() != null)
            actorOfDialogue.getPersonalityContainer().increaseNumberOfInteractions(1);
    }

    //For Discussion if File is already read, Discussions send next Dialogue
    public Dialogue readDialogue(String dialogueIdentifier)
    {
        return readDialogue(dialogueIdentifier, dialogueFileRoot);
    }

    //If Dialogue is not current file (for analysis)
    private Dialogue readDialogue(String dialogueIdentifier, Element xmlRoot)
    {
        String methodName = "readDialogue() ";
        boolean debug = true;
        boolean dialogueFound = false;
        Dialogue readDialogue = new Dialogue();
        NodeList dialogues = xmlRoot.getElementsByTagName(DIALOGUE_TAG);
        for (int i = 0; i < dialogues.getLength(); i++) //iterate dialogues of file
        {
            //found dialogue with ID
            if (((Element) dialogues.item(i)).getAttribute(ID_TAG).equals(dialogueIdentifier))
            {
                dialogueFound = true;
                Element currentDialogue = ((Element) dialogues.item(i));
                String dialogueType = currentDialogue.getAttribute(TYPE_TAG);
                NodeList xmlLines = currentDialogue.getElementsByTagName(LINE_TAG);
                readDialogue.setSpriteStatus(currentDialogue.getAttribute(ACTOR_STATUS_TAG));
                readDialogue.setSensorStatus(currentDialogue.getAttribute(SENSOR_STATUS_TAG));

                //check for type normal and decision
                readDialogue.type = dialogueType;
                //Decision
                if (dialogueType.equals(decision_TYPE_ATTRIBUTE))
                {
                    //For all options
                    NodeList optionData = currentDialogue.getElementsByTagName(OPTION_TAG);
                    for (int optionsIdx = 0; optionsIdx < optionData.getLength(); optionsIdx++)
                    {
                        Node optionNode = optionData.item(optionsIdx);
                        NodeList optionChildNodes = optionNode.getChildNodes();
                        String nextDialogue = null;
                        String visibleLine = null;
                        //Check all elements for relevant data
                        for (int j = 0; j < optionChildNodes.getLength(); j++)
                        {
                            Node node = optionChildNodes.item(j);
                            if (node.getNodeName().equals(NEXT_DIALOGUE_TAG))
                                nextDialogue = node.getTextContent();
                            else if (node.getNodeName().equals(LINE_TAG))
                                visibleLine = node.getTextContent();
                        }
                        readDialogue.addOption(visibleLine, nextDialogue);
                    }
                }
                //Discussion Type
                else if (dialogueType.equals(discussion_TYPE_ATTRIBUTE))
                {
                    String discussionGameName = currentDialogue.getAttribute(game_ATTRIBUTE);
                    String successNextMsg = currentDialogue.getAttribute(success_ATTRIBUTE);
                    String defeatNextMsg = currentDialogue.getAttribute(defeat_ATTRIBUTE);
                    readDialogue.addOption(success_ATTRIBUTE, successNextMsg);
                    readDialogue.addOption(defeat_ATTRIBUTE, defeatNextMsg);
                    WorldView.setDiscussionGame(new DiscussionGame(discussionGameName, actorOfDialogue));
                }
                else if(dialogueType.equals(levelchange_TYPE_ATTRIBUTE))
                {
                    String levelname = currentDialogue.getAttribute(level_ATTRIBUTE);
                    String spawnId = currentDialogue.getAttribute(spawnID_ATTRIBUTE);
                    WorldView.getSingleton().saveStage();
                    WorldView.getSingleton().loadStage(levelname, spawnId);
                }
                else if(dialogueType.equals(dayChange_TYPE_ATTRIBUTE))
                {
                    WorldView.setIsDaySummaryActive(true);
                }
                else
                //Normal Textbox
                {
                    for (int messageIdx = 0; messageIdx < xmlLines.getLength(); messageIdx++) //add lines
                    {
                        String message = xmlLines.item(messageIdx).getTextContent();
                        readDialogue.messages.add(message);//Without formatting the message
                    }
                }

                //Check for further dialogues
                NodeList nextDialogueIdList = currentDialogue.getElementsByTagName(NEXT_DIALOGUE_TAG);
                if (nextDialogueIdList.getLength() > 0)
                {
                    nextDialogueID = nextDialogueIdList.item(0).getTextContent();
                    readDialogue.nextDialogue = nextDialogueIdList.item(0).getTextContent();
                }
                else
                {
                    nextDialogueID = null;
                    readDialogue.nextDialogue = null;
                }
                break;
            }
        }
        //Sensor Status Changes once per Dialogue
        if (readDialogue.getSensorStatus() != null)
            actorOfDialogue.setSensorStatus(readDialogue.getSensorStatus());
        if (readDialogue.getSpriteStatus() != null)
        {
            changeActorStatus(readDialogue.getSpriteStatus());
        }

        if (!dialogueFound)
            throw new NullPointerException("Dialogue not found: " + actorOfDialogue.getDialogueFileName() + ": " + dialogueIdentifier);

        return readDialogue;
    }

    public void processKey(ArrayList<String> input, Long currentNanoTime)
    {
        String methodName = "processKey() ";
        int maxMarkedOptionIdx = lineSplitMessage.size() - 1;
        int newMarkedOption = markedOption;
        double elapsedTimeSinceLastInteraction = (currentNanoTime - WorldView.getPlayer().getActor().getLastInteraction()) / 1000000000.0;
        if (!(elapsedTimeSinceLastInteraction > TIME_BETWEEN_DIALOGUE))
            return;

        if (input.contains("E") || input.contains("ENTER") || input.contains("SPACE"))
        {
            nextMessage(currentNanoTime);
            WorldView.getPlayer().getActor().setLastInteraction(currentNanoTime);
            return;
        }
        if (input.contains("W") || input.contains("UP"))
            newMarkedOption--;
        if (input.contains("S") || input.contains("DOWN"))
            newMarkedOption++;

        if (newMarkedOption < 0)
            newMarkedOption = maxMarkedOptionIdx;
        if (newMarkedOption > maxMarkedOptionIdx)
            newMarkedOption = 0;

        if (markedOption != newMarkedOption)
        {
            markedOption = newMarkedOption;
            WorldView.getPlayer().getActor().setLastInteraction(currentNanoTime);
            drawTextbox();
        }
    }

    public void processMouse(Point2D mousePosition, boolean isMouseClicked)
    {
        String methodName = "processMouse(Point2D, boolean) ";
        Point2D textboxPosition = WorldView.getTextBoxPosition();
        Rectangle2D textboxPosRelativeToWorldview = new Rectangle2D(textboxPosition.getX(), textboxPosition.getY(), TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT);

        if (textboxPosRelativeToWorldview.contains(mousePosition))
        {
            //Calculate Mouse Position relative to Discussion
            mousePosRelativeToTextboxOverlay = new Point2D(mousePosition.getX() - textboxPosition.getX(), mousePosition.getY() - textboxPosition.getY());
        }
        else mousePosRelativeToTextboxOverlay = null;

        if (actorOfDialogue.getPersonalityContainer() != null && talkIcon.contains(mousePosRelativeToTextboxOverlay))
        {
            isTalkIconHovered = true;
        }
        else
            isTalkIconHovered = false;

        //Check if hovered on Option
        int offsetYTmp = firstLineOffsetY;
        if (readDialogue.type.equals(decision_TYPE_ATTRIBUTE) && GameWindow.getSingleton().isMouseMoved())
        {
            for (int checkedLineIdx = 0; checkedLineIdx < lineSplitMessage.size(); checkedLineIdx++)
            {
                Rectangle2D positionOptionRelativeToWorldView = new Rectangle2D(textboxPosition.getX(), textboxPosition.getY() + offsetYTmp, TEXT_BOX_WIDTH, textboxGc.getFont().getSize());
                offsetYTmp += textboxGc.getFont().getSize();
                //Hovers over Option
                if (positionOptionRelativeToWorldView.contains(mousePosition))
                {
                    if (markedOption != checkedLineIdx)
                    {
                        markedOption = checkedLineIdx;
                        drawTextbox();
                    }
                    break;
                }
            }
            GameWindow.getSingleton().setMouseMoved(false);
        }

        if (isMouseClicked)
        {
            if (isTalkIconHovered)
            {
                WorldView.setIsPersonalityScreenActive(true);
                WorldView.setPersonalityScreenController(new PersonalityScreenController(actorOfDialogue));
                WorldView.setIsTextBoxActive(false);
            }
            else
                nextMessage(GameWindow.getSingleton().getRenderTime());
        }
    }

    public void groupAnalysis(List<Actor> actorsList, Actor speakingActor)
    {
        String methodName = "groupAnalysis(List<Actor>, Actor) ";
        startConversation(speakingActor);
        for (Actor actor : actorsList)
        {
            Element analysisDialogueFileObserved = Utilities.readXMLFile(DIALOGUE_FILE_PATH + actor.getDialogueFileName() + ".xml");
            Dialogue analysisMessageObserved = readDialogue("analysis-" + actor.getDialogueStatusID(), analysisDialogueFileObserved);
            readDialogue.messages.add(actor.getActorInGameName() + analysisMessageObserved.messages.get(0));
        }
    }

    private boolean hasNextMessage()
    {
        return readDialogue.messages.size() > messageIdx + 1;
    }

    public void nextMessage(Long currentNanoTime)
    {
        String methodName = "nextMessage(Long) ";
        Actor playerActor = WorldView.getPlayer().getActor();

        if (readDialogue.type.equals(decision_TYPE_ATTRIBUTE))
        {
            nextDialogueID = readDialogue.options.get(markedOption).nextDialogue;
            markedOption = 0;
        }

        if (hasNextMessage())//More messages in this dialogue
        {
            messageIdx++;
            drawTextbox();
        }
        else if (nextDialogueID != null)//No more messages but nextDialogue defined
        {
            messageIdx = 0;
            readDialogue = readDialogue(nextDialogueID, dialogueFileRoot);
            drawTextbox();
        }
        else //End Textbox
        {
            WorldView.setIsTextBoxActive(false);
            messageIdx = 0;
        }
        playerActor.setLastInteraction(currentNanoTime);

        //for PC screen we want change after each click
        if (readDialogue.getSpriteStatus() != null)
        {
            changeActorStatus(readDialogue.getSpriteStatus());
        }


    }

    private void drawTextbox() throws NullPointerException
    {
        String methodName = "drawTextbox() ";
        boolean debug = false;
        double hue = background.getHue();
        double sat = background.getSaturation();
        double brig = background.getBrightness();
        Color marking = Color.hsb(hue, sat - 0.2, brig + 0.2);
        Color font = Color.hsb(hue, sat + 0.15, brig + 0.4);

        textboxGc.clearRect(0, 0, TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT);

        //testBackground
        if (debug)
        {
            textboxGc.setFill(Color.RED);
            textboxGc.fillRect(0, 0, TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT);
        }

        //Background
        textboxGc.setFill(background);
        textboxGc.setGlobalAlpha(0.9);
        textboxGc.fillRect(backgroundOffsetX, backgroundOffsetYDecorationTop + backgroundOffsetYTalkIcon, TEXT_BOX_WIDTH - backgroundOffsetX * 2, TEXT_BOX_HEIGHT - backgroundOffsetYDecorationTop - backgroundOffsetYTalkIcon - backgroundOffsetYDecorationBtm);

        textboxGc.setFont(new Font("Verdana", 30));
        textboxGc.setTextAlign(TextAlignment.LEFT);
        textboxGc.setTextBaseline(VPos.TOP);

        if (markedOption != null && readDialogue.type.equals(decision_TYPE_ATTRIBUTE))
        {
            textboxGc.setFill(marking);
            textboxGc.fillRect(xOffsetTextLine, firstLineOffsetY + markedOption * textboxGc.getFont().getSize() + 5, TEXT_BOX_WIDTH - 100, textboxGc.getFont().getSize());
        }

        //Decoration of textfield
        textboxGc.setGlobalAlpha(1);
        textboxGc.drawImage(cornerTopLeft, 0, backgroundOffsetYTalkIcon);
        textboxGc.drawImage(cornerBtmRight, TEXT_BOX_WIDTH - cornerBtmRight.getWidth(), TEXT_BOX_HEIGHT - cornerBtmRight.getHeight());

        int yOffsetTextLine = firstLineOffsetY;
        textboxGc.setFill(font);
        //Format Text
        if (readDialogue.type.equals(decision_TYPE_ATTRIBUTE))
        {
            lineSplitMessage = readDialogue.getOptionMessages();
        }
        else if (readDialogue.type.equals(discussion_TYPE_ATTRIBUTE))
        {
            WorldView.setIsDiscussionGameActive(true);
            lineSplitMessage = wrapText("Discussion ongoing");
        }
        else if(readDialogue.type.equals(levelchange_TYPE_ATTRIBUTE) || readDialogue.type.equals(dayChange_TYPE_ATTRIBUTE))
        {
            WorldView.setIsTextBoxActive(false);
            lineSplitMessage = wrapText("technical");
        }
        else
        {
            String nextMessage = readDialogue.messages.get(messageIdx);
            lineSplitMessage = wrapText(nextMessage);
        }

        //Draw lines
        for (int lineIdx = 0; lineIdx < lineSplitMessage.size(); lineIdx++)
        {
            textboxGc.fillText(
                    lineSplitMessage.get(lineIdx),
                    Math.round(xOffsetTextLine),
                    Math.round(yOffsetTextLine)
            );
            yOffsetTextLine += textboxGc.getFont().getSize();
        }


        //Character Info Button
        if (actorOfDialogue.getPersonalityContainer() != null)
        {
            textboxGc.drawImage(characterButton, talkIcon.getMinX(), talkIcon.getMinY());
        }

        SnapshotParameters transparency = new SnapshotParameters();
        transparency.setFill(Color.TRANSPARENT);
        textboxImage = textboxCanvas.snapshot(transparency, null);
    }

    public void setNextDialogueFromDiscussionResult(boolean hasWon)
    {
        if (hasWon)
            nextDialogueID = readDialogue.getOption(success_ATTRIBUTE).nextDialogue;
        else
            nextDialogueID = readDialogue.getOption(defeat_ATTRIBUTE).nextDialogue;
    }

    private void changeActorStatus(String toGeneralStatus)
    {
        String methodName = "changeActorStatus(String) ";
        if (!actorOfDialogue.getGeneralStatus().equals(toGeneralStatus))
            actorOfDialogue.onTextboxSignal(toGeneralStatus);
    }

    private List<String> wrapText(String longMessage)
    {
        String methodName = "wrapText() ";
        List<String> wrapped = new ArrayList<>();
        String[] words = longMessage.split(" ");

        int numberDigits = 0;
        StringBuilder lineBuilder = new StringBuilder();
        for (int wordIdx = 0; wordIdx < words.length; wordIdx++)
        {
            if (numberDigits + words[wordIdx].length() > maxDigitsInLine)
            {
                wrapped.add(lineBuilder.toString());
                lineBuilder = new StringBuilder();
                numberDigits = 0;
            }
            numberDigits += words[wordIdx].length();
            lineBuilder.append(words[wordIdx]).append(" ");
        }
        wrapped.add(lineBuilder.toString());

        return wrapped;
    }

    public WritableImage showMessage()
    {
        return textboxImage;
    }

    public static double getTEXT_BOX_WIDTH()
    {
        return TEXT_BOX_WIDTH;
    }

    public static double getTEXT_BOX_HEIGHT()
    {
        return TEXT_BOX_HEIGHT;
    }
}
