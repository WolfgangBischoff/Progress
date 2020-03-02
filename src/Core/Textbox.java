package Core;

import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
    int msgIdx =0;

    public void readDialogue(String fileIdentifier, String dialogueIdentifier)
    {
        String methodName = "readDialogue() ";
        messages = new ArrayList<>();
        List<String[]> fileData;
        Path path = Paths.get("src/res/texts/" + fileIdentifier + ".csv");

        if (Files.exists(path))
        {
            fileData = Utilities.readAllLineFromTxt(path.toString());
            for (String[] linedata : fileData)
            {
                if (linedata[0].equals(dialogueIdentifier))
                    messages.add(linedata[1]);
            }
        }
        else throw new RuntimeException("Actordata not found: " + path.toString());

        if(messages.isEmpty())
            System.out.println(classname + methodName + "No messages foung with ID: " + dialogueIdentifier);

        nextMessage();
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
        textboxGc.fillText(
                messages.get(msgIdx),
                Math.round(textboxCanvas.getWidth() / 2),
                Math.round(textboxCanvas.getHeight() / 2)
        );
        textboxImage = textboxCanvas.snapshot(new SnapshotParameters(), null);
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

        if(elapsedTimeSinceLastInteraction > 1)
        {
            if(hasNextMessage())
            {
                msgIdx++;
                nextMessage();
            }
            else
            {
                ((WorldView)(GameWindow.getSingleton().currentView)).isTextBoxActive = false;
                msgIdx = 0;
            }
            playerActor.lastInteraction = currentNanoTime;
        }

    }

    public WritableImage showMessage()
    {
        return textboxImage;
    }
}
