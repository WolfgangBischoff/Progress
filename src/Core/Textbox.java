package Core;

import Core.Enums.TriggerType;
import com.sun.webkit.network.Util;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.WriteAbortedException;
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
    double TEXTBOX_WIDTH = CAMERA_WIDTH / 1.5;
    double TEXTBOX_HEIGHT = Config.CAMERA_HEIGTH / 3;
    Canvas textboxCanvas = new Canvas(TEXTBOX_WIDTH, TEXTBOX_HEIGHT);
    GraphicsContext textboxGc = textboxCanvas.getGraphicsContext2D();

    WritableImage textboxImage;
    List<String> messages;
    int msgIdx =0;

    public void readDialogue(String fileID, String dialogueID)
    {
        String fileIdentifier = "descriptions";
        String dialogueIdentifier = "test";

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

        nextMessage();
    }

    private void nextMessage()
    {
        String methodName = "nextMessage() ";
        //System.out.println(classname + methodName + messages.toString());

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
        if(messages.size() > msgIdx+1)
            return true;
        else return false;
    }

    public void nextMessage(Long currentNanoTime)
    {
        String methodName = "nextMessage(Long) ";
        Sprite player = WorldView.getPlayer();
        double elapsedTimeSinceLastInteraction = (currentNanoTime - player.lastInteraction) / 1000000000.0;

        if(elapsedTimeSinceLastInteraction > 1)
        {
            if(hasNextMessage())
            {
                msgIdx++;
                System.out.println(classname + methodName + msgIdx);
                nextMessage();
            }
            else
            {
                System.out.println(classname + methodName + "No further Messages");
                ((WorldView)(GameWindow.getSingleton().currentView)).isTextBoxActive = false;
                msgIdx = 0;
            }
            player.lastInteraction = currentNanoTime;
        }

    }

    public WritableImage showMessage()
    {
        return textboxImage;
    }
}
