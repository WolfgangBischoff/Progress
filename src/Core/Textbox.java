package Core;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

import static Core.Config.CAMERA_HEIGTH;
import static Core.Config.CAMERA_WIDTH;

public class Textbox
{
    String classname = "Textbox ";
    private double TEXTBOX_WIDTH = CAMERA_WIDTH / 1.5;
    private double TEXTBOX_HEIGHT = CAMERA_HEIGTH / 3.0;
    Canvas textboxCanvas = new Canvas(TEXTBOX_WIDTH, TEXTBOX_HEIGHT);
    GraphicsContext textboxGc = textboxCanvas.getGraphicsContext2D();

    WritableImage textboxImage;
    //List<String> messages;
    Dialogue loadedDialogue;
    int msgIdx = 0;

    Element root;
    String nextDialogueID = null;

    List<String> lineSplittedMessage;
    int yOffsetTextLine = 10;
    int maxDigitsInLine = 40;

    public void readDialogue(String fileIdentifier, String dialogueIdentifier)
    {
        String methodName = "readDialogue() ";

        readFile(fileIdentifier);
        readDialogue(dialogueIdentifier);
        nextMessage();
    }

    private void readFile(String fileIdentifier)
    {
        //https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
            File file = new File("src/res/texts/" + fileIdentifier + ".xml");
            Document doc = builder.parse(file);
            root = doc.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e)
        {
            e.printStackTrace();
        }
    }

    private void readDialogue(String dialogueIdentifier)
    {
        String methodName = "readDialogue() ";
        //messages = new ArrayList<>();
        loadedDialogue = new Dialogue();
        NodeList dialogues = root.getElementsByTagName("dialogue");
        for (int i = 0; i < dialogues.getLength(); i++) //iterate dialogues of file
        {
            //found doilague with ID
            if (((Element) dialogues.item(i)).getAttribute("id").equals(dialogueIdentifier))
            {
                Element currentDialogue = ((Element) dialogues.item(i));

                //TODO check for status change

                //check for type normal and decision
                String dialogueType = currentDialogue.getAttribute("type");
                loadedDialogue.type = dialogueType;
                if(dialogueType.equals("decision"))
                {

                    NodeList options = currentDialogue.getElementsByTagName("line");
                    //StringBuilder optionsShow = new StringBuilder();
                    for (int optionsIdx = 0; optionsIdx < options.getLength(); optionsIdx++)
                    {
                        //Add options to message
                        String option = options.item(optionsIdx).getTextContent();
                        //optionsShow.append(option).append("\n");

                        loadedDialogue.addOption(option);
                    }
                    //messages.add(optionsShow.toString());
                }
                else
                {

                    NodeList lines = currentDialogue.getElementsByTagName("line");
                    for (int j = 0; j < lines.getLength(); j++) //add lines
                    {
                        String message = lines.item(j).getTextContent();
                        //messages.add(message);

                        loadedDialogue.messages.add(message);
                    }
                }

                //Check for further dialogues
                NodeList nextDialogueIdList = currentDialogue.getElementsByTagName("nextDialogue");
                if (nextDialogueIdList.getLength() > 0) {
                    nextDialogueID = nextDialogueIdList.item(0).getTextContent();
                    loadedDialogue.nextDialogue = nextDialogueIdList.item(0).getTextContent();
                }
                else {
                    nextDialogueID = null;
                    loadedDialogue.nextDialogue = null;
                }

                break;
            }
        }
    }



    public void onClick()
    {
        System.out.println(classname + "received Click");
    }

    public boolean intersectsRelativeToWorldView(Point2D clickRelativeToWorldView)
    {
        String methodName = "intersectsRelativeToWorldView(Point2D) ";
        Point2D textboxPosition = WorldView.textboxPosition;
        Rectangle2D positionRelativeToWorldView = new Rectangle2D(textboxPosition.getX(), textboxPosition.getY(), TEXTBOX_WIDTH, TEXTBOX_HEIGHT);

        int yOffsetTextLine = this.yOffsetTextLine;
        for(int checkedLineIdx = 0; checkedLineIdx <lineSplittedMessage.size(); checkedLineIdx++)
        {
            Rectangle2D positionOptionRelativeToWorldView = new Rectangle2D(textboxPosition.getX(), textboxPosition.getY() + yOffsetTextLine, TEXTBOX_WIDTH, textboxGc.getFont().getSize());
            yOffsetTextLine+= textboxGc.getFont().getSize();
            if(positionOptionRelativeToWorldView.contains(clickRelativeToWorldView))
                System.out.println(classname + methodName + "detected click on option: " + checkedLineIdx + " Answers: " + lineSplittedMessage.size());
        }



        return positionRelativeToWorldView.contains(clickRelativeToWorldView);
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
        //else throw new RuntimeException("Actordata not found: " + path.toString());


        if (msgs.isEmpty())
            System.out.println(classname + methodName + "No messages found with ID: " + dialogueIdentifier + " in " + path.toString());
        return msgs;
    }

    public void groupAnalysis(List<Actor> actorsList, String fileIdentifier, String dialogueIdentifier)
    {

        String methodName = "groupAnalysis() ";
        readDialogue(fileIdentifier, dialogueIdentifier);
        for (Actor actor : actorsList)
        {
            List<String> msgs = readMessage(actor.dialogueFileName, "analysis-" + actor.dialogueStatusID);
            //messages.addAll(msgs);
        }
    }


    private boolean hasNextMessage()
    {
        //return messages.size() > msgIdx + 1;
        return loadedDialogue.messages.size() > msgIdx + 1;
    }



    public void nextMessage(Long currentNanoTime)
    {
        String methodName = "nextMessage(Long) ";
        Actor playerActor = WorldView.getPlayer().actor;
        double elapsedTimeSinceLastInteraction = (currentNanoTime - playerActor.lastInteraction) / 1000000000.0;

        if (elapsedTimeSinceLastInteraction > 0.5)
        {
            if (hasNextMessage())
            {
                msgIdx++;
                nextMessage();
            }
            else if (nextDialogueID != null)
            {
                msgIdx = 0;
                readDialogue(nextDialogueID);
                nextMessage();
            }
            else
            {
                ((WorldView) (GameWindow.getSingleton().currentView)).isTextBoxActive = false;
                msgIdx = 0;
            }
            playerActor.lastInteraction = currentNanoTime;
        }

    }

    private void nextMessage()
    {
        String methodName = "nextMessage() ";
        textboxGc.setFill(Color.CADETBLUE);
        textboxGc.fillRect(0, 0, TEXTBOX_WIDTH, TEXTBOX_HEIGHT);
        textboxGc.setFill(Color.BLACK);
        textboxGc.setFont(new Font("Arial", 30));
        textboxGc.setTextAlign(TextAlignment.LEFT);
        textboxGc.setTextBaseline(VPos.TOP);


        int yOffsetTextLine = this.yOffsetTextLine;
        try
        {
            //String nextMessage = messages.get(msgIdx);
            System.out.println(classname + methodName + loadedDialogue.type);
            if(loadedDialogue.type.equals("decision"))
            {
                lineSplittedMessage = loadedDialogue.getOptionMessages();
            }
            else
            {
                String nextMessage = loadedDialogue.messages.get(msgIdx);
                lineSplittedMessage = wrapText(nextMessage);
            }

            for(int lineIdx = 0; lineIdx < lineSplittedMessage.size(); lineIdx++)
            {
                textboxGc.fillText(
                        lineSplittedMessage.get(lineIdx),
                        Math.round(10),
                        Math.round(yOffsetTextLine)
                );
                yOffsetTextLine+= textboxGc.getFont().getSize();
            }


        } catch (IndexOutOfBoundsException e)
        {
            System.out.println("IndexOutOfBoundsException " + e.getMessage());
        }
        textboxImage = textboxCanvas.snapshot(new SnapshotParameters(), null);
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
    for(int wordIdx = 0; wordIdx<words.length; wordIdx++)
    {
        if(numberDigits + words[wordIdx].length() > maxDigitsInLine)
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
}
