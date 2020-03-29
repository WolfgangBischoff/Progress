package Core;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static Core.Config.*;

public class Textbox
{
    String classname = "Textbox/";
    private double TEXTBOX_WIDTH = CAMERA_WIDTH / 1.5;
    private double TEXTBOX_HEIGHT = CAMERA_HEIGTH / 3.0;
    Canvas textboxCanvas = new Canvas(TEXTBOX_WIDTH, TEXTBOX_HEIGHT);
    GraphicsContext textboxGc = textboxCanvas.getGraphicsContext2D();
    WritableImage textboxImage;
    Dialogue loadedDialogue;
    Element xmlRoot;
    int messageIdx = 0;
    final int firstLineOffsetY = 20;
    final int xOffsetTextLine = 30;
    final int maxDigitsInLine = 40;
    String nextDialogueID = null;
    List<String> lineSplitMessage;
    Integer markedOption = 0;
    Actor actorOfDialogue;

    Image corner;

    public Textbox()
    {
        corner = new Image("res/img/txtbox/textboxCornerTL.png");
    }

    public void startConversation(Actor actorParam)
    {
        String methodName = "readDialogue() ";
        actorOfDialogue = actorParam;
        readFile(actorOfDialogue.dialogueFileName);
        readDialogue(actorOfDialogue.dialogueStatusID);
        drawTextbox();
    }

    private void readFile(String fileIdentifier)
    {
        //https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = null;
        String path = "src/res/texts/" + fileIdentifier + ".xml";
        try
        {
            builder = factory.newDocumentBuilder();
            File file = new File(path);
            Document doc = builder.parse(file);
            xmlRoot = doc.getDocumentElement();
        } catch (ParserConfigurationException | SAXException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            System.out.println("Cannot find: " + path);
        }
    }

    private void readDialogue(String dialogueIdentifier)
    {
        String methodName = "readDialogue() ";
        loadedDialogue = new Dialogue();
        NodeList dialogues = xmlRoot.getElementsByTagName(DIALOGUE_TAG);
        for (int i = 0; i < dialogues.getLength(); i++) //iterate dialogues of file
        {
            //found dialogue with ID
            if (((Element) dialogues.item(i)).getAttribute(ID_TAG).equals(dialogueIdentifier))
            {
                Element currentDialogue = ((Element) dialogues.item(i));
                String dialogueType = currentDialogue.getAttribute(TYPE_TAG);
                NodeList xmlLines = currentDialogue.getElementsByTagName(LINE_TAG);
                loadedDialogue.setActorStatus(currentDialogue.getAttribute(ACTOR_STATUS_TAG));

                //check for type normal and decision
                loadedDialogue.type = dialogueType;
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
                        loadedDialogue.addOption(option, nextDialogue);
                    }
                }
                else
                {
                    for (int messageIdx = 0; messageIdx < xmlLines.getLength(); messageIdx++) //add lines
                    {
                        String message = xmlLines.item(messageIdx).getTextContent();
                        loadedDialogue.messages.add(message);//Without formatting the message
                    }
                }

                //TODO ggf redundant
                //Check for further dialogues
                NodeList nextDialogueIdList = currentDialogue.getElementsByTagName(NEXT_DIALOGUE_TAG);
                if (nextDialogueIdList.getLength() > 0)
                {
                    nextDialogueID = nextDialogueIdList.item(0).getTextContent();
                    loadedDialogue.nextDialogue = nextDialogueIdList.item(0).getTextContent();
                }
                else
                {
                    nextDialogueID = null;
                    loadedDialogue.nextDialogue = null;
                }

                break;
            }
        }
    }

    public void processKey(ArrayList<String> input, Long currentNanoTime)
    {
        String methodName = "processKey() ";
        int maxMarkedOptionIdx = lineSplitMessage.size() - 1;
        int newMarkedOption = markedOption;
        double elapsedTimeSinceLastInteraction = (currentNanoTime - WorldView.getPlayer().actor.lastInteraction) / 1000000000.0;
        if (!(elapsedTimeSinceLastInteraction > TIME_BETWEEN_DIALOGUE))
            return;

        if (input.contains("E") || input.contains("ENTER") || input.contains("SPACE"))
        {
            nextMessage(currentNanoTime);
            WorldView.getPlayer().actor.lastInteraction = currentNanoTime;
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
            WorldView.getPlayer().actor.lastInteraction = currentNanoTime;
            drawTextbox();
        }
    }

    public void processMouse(Point2D mousePosition, boolean isMouseClicked)
    {
        String methodName = "processMouse(Point2D, boolean) ";
        Point2D textboxPosition = WorldView.textboxPosition;
        Rectangle2D textboxPosRelativeToWorldview = new Rectangle2D(textboxPosition.getX(), textboxPosition.getY(), TEXTBOX_WIDTH, TEXTBOX_HEIGHT);
        if (textboxPosRelativeToWorldview.contains(mousePosition))
        {
            //System.out.println(classname + methodName + "Mouse: " + mousePosition.getX() + " " + mousePosition.getY());
        }

        //Check if hovered on Option
        int offsetYTmp = firstLineOffsetY;
        if (loadedDialogue.type.equals(DECISION_KEYWORD) && GameWindow.getSingleton().mouseMoved)
        {
            for (int checkedLineIdx = 0; checkedLineIdx < lineSplitMessage.size(); checkedLineIdx++)
            {
                Rectangle2D positionOptionRelativeToWorldView = new Rectangle2D(textboxPosition.getX(), textboxPosition.getY() + offsetYTmp, TEXTBOX_WIDTH, textboxGc.getFont().getSize());
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
            nextMessage(GameWindow.getSingleton().getRenderTime());
        }
    }

    private List<String> readMessage(String fileIdentifier, String dialogueIdentifier)
    {
        String methodName = "readMessage() ";
        List<String> msgs = new ArrayList<>();
        List<String[]> fileData;
        Path path = Paths.get("src/res/texts/" + fileIdentifier + ".csv");

        if (Files.exists(path))
        {
            fileData = Utilities.readAllLineFromTxt(path.toString());
            for (String[] linedata : fileData)
            {
                {
                    if (linedata[0].equals(dialogueIdentifier))
                        msgs.add(linedata[1]);
                }
            }
        }

        if (msgs.isEmpty())
            System.out.println(classname + methodName + "No messages found with ID: " + dialogueIdentifier + " in " + path.toString());
        return msgs;
    }

    public void groupAnalysis(List<Actor> actorsList, Actor speakingActor)
    {

        String methodName = "groupAnalysis() ";
        //readDialogue(fileIdentifier, dialogueIdentifier);
        startConversation(speakingActor);
        for (Actor actor : actorsList)
        {
            List<String> msgs = readMessage(actor.dialogueFileName, "analysis-" + actor.dialogueStatusID);
            //messages.addAll(msgs);
        }
    }

    private boolean hasNextMessage()
    {
        return loadedDialogue.messages.size() > messageIdx + 1;
    }

    public void nextMessage(Long currentNanoTime)
    {
        String methodName = "nextMessage(Long) ";
        Actor playerActor = WorldView.getPlayer().actor;

        if (loadedDialogue.type.equals(DECISION_KEYWORD))
        {
            nextDialogueID = loadedDialogue.options.get(markedOption).nextDialogue;
            markedOption = 0;
        }

        if (hasNextMessage())
        {
            messageIdx++;
            drawTextbox();
        }
        else if (nextDialogueID != null)
        {
            messageIdx = 0;
            readDialogue(nextDialogueID);
            drawTextbox();
        }
        else
        {
            WorldView.isTextBoxActive = false;
            messageIdx = 0;
        }
        playerActor.lastInteraction = currentNanoTime;

        if(loadedDialogue.getActorStatus() != null)
            changeActorStatus(loadedDialogue.getActorStatus());
    }

    private void drawTextbox()
    {
        String methodName = "drawTextbox() ";
        textboxGc.clearRect(0, 0, TEXTBOX_WIDTH, TEXTBOX_HEIGHT);

        textboxGc.setFill(Color.DARKSLATEGREY);
        textboxGc.fillRect(0, 0, TEXTBOX_WIDTH, TEXTBOX_HEIGHT);

        //Decoration of textfield
        textboxGc.drawImage(corner, 0, 0);

        textboxGc.setFont(new Font("Verdana", 30));

        if (markedOption != null && loadedDialogue.type.equals(DECISION_KEYWORD))
        {
            textboxGc.setFill(Color.BISQUE);
            textboxGc.fillRect(xOffsetTextLine, firstLineOffsetY + markedOption * textboxGc.getFont().getSize(), TEXTBOX_WIDTH - 20, textboxGc.getFont().getSize());
        }

        //textboxGc.setFont(Font.font("Calibri", FontWeight.NORMAL, 30 ));
        //System.out.println(classname + methodName + textboxGc.getFont().toString());
        textboxGc.setFill(Color.BLACK);
        textboxGc.setTextAlign(TextAlignment.LEFT);
        textboxGc.setTextBaseline(VPos.TOP);

        int yOffsetTextLine = firstLineOffsetY;
        try
        {
            if (loadedDialogue.type.equals(DECISION_KEYWORD))
            {
                lineSplitMessage = loadedDialogue.getOptionMessages();
            }
            else
            {
                String nextMessage = loadedDialogue.messages.get(messageIdx);
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


        } catch (IndexOutOfBoundsException e)
        {
            System.out.println("IndexOutOfBoundsException " + e.getMessage());
        }
        textboxImage = textboxCanvas.snapshot(new SnapshotParameters(), null);

        //changeActorStatus();
    }

    private void changeActorStatus(String toGeneralStatus)
    {
        String methodName = "changeActorStatus(String) ";
        //System.out.println(classname + methodName + " triggered " + loadedDialogue.getActorStatus());
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

    public double getTEXTBOX_WIDTH()
    {
        return TEXTBOX_WIDTH;
    }

    public double getTEXTBOX_HEIGHT()
    {
        return TEXTBOX_HEIGHT;
    }
}
