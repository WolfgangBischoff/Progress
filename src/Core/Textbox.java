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
    List<String> messages;
    int msgIdx = 0;

    Element root;
    String nextDialogueID = null;

    public void readDialogue(String fileIdentifier, String dialogueIdentifier)
    {
        String methodName = "readDialogue() ";
        //messages = readMessage(fileIdentifier, dialogueIdentifier);
        //nextMessage();

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
        messages = new ArrayList<>();
        NodeList dialogues = root.getElementsByTagName("dialogue");
        for (int i = 0; i < dialogues.getLength(); i++) //iterate dialogues of file
        {
            //found doilague with ID
            if (((Element) dialogues.item(i)).getAttribute("id").equals(dialogueIdentifier))
            {
                Element currentDialogue = ((Element) dialogues.item(i));

                //TODO check for status change

                //TODO check for type normal and decision
                String dialogueType = currentDialogue.getAttribute("type");
                if(dialogueType.equals("decision"))
                {
                    System.out.println(classname + methodName + "decision");
                    NodeList options = currentDialogue.getElementsByTagName("option");
                    for (int optionsIdx = 0; optionsIdx < options.getLength(); optionsIdx++)
                    {
                        //Add options to message
                        StringBuilder optionsShow = new StringBuilder();
                        //optionsShow.append(options.item(optionsIdx))
                        //String message = options.item(optionsIdx).getTextContent();
                        //messages.add(message);
                    }
                }
                else
                {
                    System.out.println(classname + methodName + "normal");
                }

                NodeList lines = currentDialogue.getElementsByTagName("line");
                for (int j = 0; j < lines.getLength(); j++) //add lines
                {
                    String message = lines.item(j).getTextContent();
                    messages.add(message);
                }

                //Check for further dialogues
                NodeList nextDialogueIdList = currentDialogue.getElementsByTagName("nextDialogue");
                if (nextDialogueIdList.getLength() > 0) {
                    nextDialogueID = nextDialogueIdList.item(0).getTextContent();
                }
                else {
                    nextDialogueID = null;
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
        Point2D textboxPosition = WorldView.textboxPosition;
        Rectangle2D positionRelativeToWorldView = new Rectangle2D(textboxPosition.getX(), textboxPosition.getY(), TEXTBOX_WIDTH, TEXTBOX_HEIGHT);
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
            messages.addAll(msgs);
        }
    }


    private boolean hasNextMessage()
    {
        return messages.size() > msgIdx + 1;
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
        textboxGc.setTextAlign(TextAlignment.CENTER);
        textboxGc.setTextBaseline(VPos.CENTER);
        try
        {
            String next = messages.get(msgIdx);

            textboxGc.fillText(
                    next,
                    Math.round(textboxCanvas.getWidth() / 2),
                    Math.round(textboxCanvas.getHeight() / 2)
            );
        } catch (IndexOutOfBoundsException e)
        {
            System.out.println("IndexOutOfBoundsException " + e.getMessage());
        }
        textboxImage = textboxCanvas.snapshot(new SnapshotParameters(), null);
    }

    public WritableImage showMessage()
    {
        return textboxImage;
    }
}
