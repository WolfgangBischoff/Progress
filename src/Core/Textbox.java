package Core;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Core.Config.*;

public class Textbox
{
    private static final String CLASSNAME = "Textbox-";
    private double TEXT_BOX_WIDTH = TEXTBOX_WIDTH;
    private double TEXT_BOX_HEIGHT = TEXTBOX_HEIGHT;
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

    public Textbox()
    {
        cornerTopLeft = new Image(IMAGE_DIRECTORY_PATH + "txtbox/textboxTL.png");
        cornerBtmRight = new Image(IMAGE_DIRECTORY_PATH + "txtbox/textboxBL.png");
    }

    public void startConversation(Actor actorParam)
    {
        String methodName = "readDialogue() ";
        actorOfDialogue = actorParam;

        try
        {
            dialogueFileRoot = readFile(actorOfDialogue.dialogueFileName);
            readDialogue = readDialogue(actorOfDialogue.dialogueStatusID, dialogueFileRoot);
            drawTextbox();
        }
        catch (NullPointerException e)
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\nDialogueFileName: " + actorOfDialogue.dialogueFileName);
            if (readDialogue == null)
                stringBuilder.append("\nLoadedDialogue = null");
            else
            {
                stringBuilder.append("\nLoaded Dialogue type: " + readDialogue.type);
                stringBuilder.append("\nLoaded Dialogue nextDialogue: " + readDialogue.nextDialogue);
                stringBuilder.append("\nLoaded Dialogue messages: " + readDialogue.messages);
                stringBuilder.append("\nLoaded Dialogue options: " + readDialogue.options);
            }
            throw new NullPointerException(stringBuilder.toString());
        }
    }

    private Element readFile(String fileIdentifier)
    {
        Element rootElement;
        //https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = null;
        String path = DIALOGUE_FILE_PATH + fileIdentifier + ".xml";
        try
        {
            builder = factory.newDocumentBuilder();
            File file = new File(path);
            Document doc = builder.parse(file);
            //System.out.println(CLASSNAME + "Doc: " + doc.getDocumentElement());
            return doc.getDocumentElement();
        }
        catch (ParserConfigurationException | SAXException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println("Cannot find: " + path);
        }

        throw new RuntimeException("Cannot find dialogue file: " + path);
    }

    private Dialogue readDialogue(String dialogueIdentifier, Element xmlRoot)
    {
        String methodName = "readDialogue() ";
        boolean debug = true;
        Dialogue readDialogue = new Dialogue();
        NodeList dialogues = xmlRoot.getElementsByTagName(DIALOGUE_TAG);
        for (int i = 0; i < dialogues.getLength(); i++) //iterate dialogues of file
        {
            //found dialogue with ID
            if (((Element) dialogues.item(i)).getAttribute(ID_TAG).equals(dialogueIdentifier))
            {
                Element currentDialogue = ((Element) dialogues.item(i));
                String dialogueType = currentDialogue.getAttribute(TYPE_TAG);
                NodeList xmlLines = currentDialogue.getElementsByTagName(LINE_TAG);
                readDialogue.setSpriteStatus(currentDialogue.getAttribute(ACTOR_STATUS_TAG));
                readDialogue.setSensorStatus(currentDialogue.getAttribute(SENSOR_STATUS_TAG));

                //check for type normal and decision
                readDialogue.type = dialogueType;
                if (dialogueType.equals(DECISION_KEYWORD))
                {
                    NodeList nextDialogueData = currentDialogue.getElementsByTagName(NEXT_DIALOGUE_TAG);
                    for (int optionsIdx = 0; optionsIdx < xmlLines.getLength(); optionsIdx++)
                    {
                        //Add options to message
                        String option = xmlLines.item(optionsIdx).getTextContent();
                        String nextDialogue = null;
                        if (nextDialogueData.item(optionsIdx) != null)
                            nextDialogue = nextDialogueData.item(optionsIdx).getTextContent();
                        readDialogue.addOption(option, nextDialogue);
                    }
                }
                else
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
        if (readDialogue == null)
            throw new RuntimeException("No dialogue found with ID: " + dialogueIdentifier);

        //Sensor Status Changes once per Dialogue
        if (readDialogue.getSensorStatus() != null)
            actorOfDialogue.setSensorStatus(readDialogue.getSensorStatus());
        if (readDialogue.getSpriteStatus() != null)
        {
            changeActorStatus(readDialogue.getSpriteStatus());
        }

        return readDialogue;
    }

    public void processKey(ArrayList<String> input, Long currentNanoTime)
    {
        String methodName = "processKey() ";
        int maxMarkedOptionIdx = lineSplitMessage.size() - 1;
        int newMarkedOption = markedOption;
        double elapsedTimeSinceLastInteraction = (currentNanoTime - WorldView.getPlayer().actor.getLastInteraction()) / 1000000000.0;
        if (!(elapsedTimeSinceLastInteraction > TIME_BETWEEN_DIALOGUE))
            return;

        if (input.contains("E") || input.contains("ENTER") || input.contains("SPACE"))
        {
            nextMessage(currentNanoTime);
            WorldView.getPlayer().actor.setLastInteraction(currentNanoTime);
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
            WorldView.getPlayer().actor.setLastInteraction(currentNanoTime);
            drawTextbox();
        }
    }

    public void processMouse(Point2D mousePosition, boolean isMouseClicked)
    {
        String methodName = "processMouse(Point2D, boolean) ";
        Point2D textboxPosition = WorldView.textBoxPosition;
        Rectangle2D textboxPosRelativeToWorldview = new Rectangle2D(textboxPosition.getX(), textboxPosition.getY(), TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT);

        if (textboxPosRelativeToWorldview.contains(mousePosition))
        {
            //Calculate Mouse Position relative to Discussion
            mousePosRelativeToTextboxOverlay = new Point2D(mousePosition.getX() - textboxPosition.getX(), mousePosition.getY() - textboxPosition.getY());
            //System.out.println(classname + methodName + "Mouse: " + mousePosition.getX() + " " + mousePosition.getY());
        }
        else mousePosRelativeToTextboxOverlay = null;

        if (actorOfDialogue.personalityContainer != null && talkIcon.contains(mousePosRelativeToTextboxOverlay))
        {
            isTalkIconHovered = true;
            //System.out.println(CLASSNAME + methodName + "talkIcon hovered");
        }
        else
            isTalkIconHovered = false;

        //Check if hovered on Option
        int offsetYTmp = firstLineOffsetY;
        if (readDialogue.type.equals(DECISION_KEYWORD) && GameWindow.getSingleton().mouseMoved)
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
            GameWindow.getSingleton().mouseMoved = false;
        }

        if (isMouseClicked)
        {
            if (isTalkIconHovered)
            {
                System.out.println(CLASSNAME + methodName + "openTalkMenu");
                WorldView.setIsPersonalityScreenActive(isTalkIconHovered);
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
            Element analysisDialogueFileObserved = readFile(actor.dialogueFileName);
            Dialogue analysisMessageObserved = readDialogue("analysis-" + actor.dialogueStatusID, analysisDialogueFileObserved);
            readDialogue.messages.add(actor.actorInGameName + analysisMessageObserved.messages.get(0));
        }
    }

    private boolean hasNextMessage()
    {
        return readDialogue.messages.size() > messageIdx + 1;
    }

    public void nextMessage(Long currentNanoTime)
    {
        String methodName = "nextMessage(Long) ";
        Actor playerActor = WorldView.getPlayer().actor;

        if (readDialogue.type.equals(DECISION_KEYWORD))
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
            WorldView.isTextBoxActive = false;
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
        /*int backgroundOffsetX = 16;
        int backgroundOffsetYDecorationTop = 10;
        int backgroundOffsetYTalkIcon = 50;
        int backgroundOffsetYDecorationBtm = 10;
        Color background = Color.rgb(60, 90, 85);

         */
        double hue = background.getHue();
        double sat = background.getSaturation();
        double brig = background.getBrightness();
        Color marking = Color.hsb(hue, sat - 0.2, brig + 0.2);
        Color font = Color.hsb(hue, sat + 0.15, brig + 0.4);

        textboxGc.clearRect(0, 0, TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT);

        //testBackground
        textboxGc.setFill(Color.RED);
        textboxGc.fillRect(0, 0, TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT);

        //Background
        textboxGc.setFill(background);
        textboxGc.setGlobalAlpha(0.9);
        //textboxGc.fillRect(backgroundOffsetX, backgroundOffsetYTop, TEXT_BOX_WIDTH - backgroundOffsetX * 2, TEXT_BOX_HEIGHT - backgroundOffsetYTop * 2);
        textboxGc.fillRect(backgroundOffsetX, backgroundOffsetYDecorationTop + backgroundOffsetYTalkIcon, TEXT_BOX_WIDTH - backgroundOffsetX * 2, TEXT_BOX_HEIGHT - backgroundOffsetYDecorationTop - backgroundOffsetYTalkIcon - backgroundOffsetYDecorationBtm);

        if (markedOption != null && readDialogue.type.equals(DECISION_KEYWORD))
        {
            textboxGc.setFill(marking);
            textboxGc.fillRect(xOffsetTextLine, firstLineOffsetY + markedOption * textboxGc.getFont().getSize() + 5, TEXT_BOX_WIDTH - 100, textboxGc.getFont().getSize());
        }

        //Decoration of textfield
        textboxGc.setGlobalAlpha(1);
        //textboxGc.drawImage(cornerTopLeft, 0, 0);
        textboxGc.drawImage(cornerTopLeft, 0, backgroundOffsetYTalkIcon);
        textboxGc.drawImage(cornerBtmRight, TEXT_BOX_WIDTH - cornerBtmRight.getWidth(), TEXT_BOX_HEIGHT - cornerBtmRight.getHeight());

        textboxGc.setFont(new Font("Verdana", 30));
        textboxGc.setFill(font);
        textboxGc.setTextAlign(TextAlignment.LEFT);
        textboxGc.setTextBaseline(VPos.TOP);

        int yOffsetTextLine = firstLineOffsetY;
        try
        {
            if (readDialogue.type.equals(DECISION_KEYWORD))
            {
                lineSplitMessage = readDialogue.getOptionMessages();
            }
            else
            {
                String nextMessage = readDialogue.messages.get(messageIdx);
                lineSplitMessage = wrapText(nextMessage);
            }

            for (int lineIdx = 0; lineIdx < lineSplitMessage.size(); lineIdx++)
            {
                textboxGc.fillText(
                        lineSplitMessage.get(lineIdx),
                        Math.round(xOffsetTextLine),
                        Math.round(yOffsetTextLine)
                );
                yOffsetTextLine += textboxGc.getFont().getSize();
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            System.out.println(CLASSNAME + "IndexOutOfBoundsException " + e.getMessage());
        }

        //Dialogue Game Icon
        if (actorOfDialogue.personalityContainer != null)
        {
            textboxGc.fillRect(talkIcon.getMinX(), talkIcon.getMinY(), talkIcon.getWidth(), talkIcon.getHeight());
        }

        SnapshotParameters transparency = new SnapshotParameters();
        transparency.setFill(Color.TRANSPARENT);
        textboxImage = textboxCanvas.snapshot(transparency, null);
    }

    private void changeActorStatus(String toGeneralStatus)
    {
        String methodName = "changeActorStatus(String) ";
        if (!actorOfDialogue.generalStatus.equals(toGeneralStatus))
            actorOfDialogue.onTextboxSignal(toGeneralStatus);
    }

    private List<String> wrapText(String longMessage)
    {
        String methodName = "wrapText() ";
        List<String> wrapped = new ArrayList<>();
        String[] words = longMessage.split(" ");

        //for(String s : words)
        //System.out.println(classname + methodName + s);

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

    public double getTEXT_BOX_WIDTH()
    {
        return TEXT_BOX_WIDTH;
    }

    public double getTEXT_BOX_HEIGHT()
    {
        return TEXT_BOX_HEIGHT;
    }
}
