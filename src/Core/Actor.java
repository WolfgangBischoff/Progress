package Core;


import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.Status.*;

public class Actor
{
    String onAction = "nothing";
    Status status = ON;
    Map<Status, String> statusImages = new HashMap<>();
    Sprite sprite;

/*
 public Actor(Sprite sprite, Status initStatus)
    {
        this.sprite = sprite;
        this.status = initStatus;
        List<String[]> actordata;
        Path path = Paths.get("src/res/actorData/" + sprite.getName() + ".csv");
        if (Files.exists(path))
        {
            actordata = Utilities.readAllLineFromTxt("src/res/actorData/" + sprite.getName() + ".csv");
            for (String[] linedate : actordata)
            {
                if (linedate[0].equals("action"))
                {
                    onAction = linedate[1];
                    continue;
                }

                Status status = Status.getStatus(linedate[0]);
                String sprName = linedate[1];
                statusImages.put(status, sprName);
            }
        }
    }
 */
    public Actor(Sprite sprite, Status initStatus)
    {
        this.sprite = sprite;
        this.status = initStatus;
        List<String[]> actordata;
        Path path = Paths.get("src/res/actorData/" + sprite.getName() + ".csv");
        if (Files.exists(path))
        {
            actordata = Utilities.readAllLineFromTxt("src/res/actorData/" + sprite.getName() + ".csv");
            for (String[] linedate : actordata)
            {
                if (linedate[0].equals("action"))
                {
                    onAction = linedate[1];
                    continue;
                }

                Status status = Status.getStatus(linedate[0]);
                String sprName = linedate[1];
                statusImages.put(status, sprName);
            }
        }
        else throw new RuntimeException("Actordata not found: " + sprite.getName());
    }

    static public String[] readSpriteData(String actorname, Status status)
    {
        Path path = Paths.get("src/res/actorData/" + actorname + ".csv");
        List<String[]> actordata;
        if (Files.exists(path))
        {
            actordata = Utilities.readAllLineFromTxt("src/res/actorData/" + actorname + ".csv");
            for (String[] linedate : actordata)
            {
                if (linedate[0].equals("action"))
                {
                    continue;
                }

                Status linestatus = Status.getStatus(linedate[0]);
                if(linestatus.equals(status))
                    return linedate;
            }
            throw  new RuntimeException("Actordata: Status not found");
        }
        else throw new RuntimeException("Actordata not found: " + actorname);
    }

    public void act()
    {
        if (onAction == "nothing")
            return;

        if (onAction.equals("statusChange"))
        {
            changeStatus();
            sprite.setImage(statusImages.get(status));
        }

        if(onAction.equals("animation"))
        {
            status = ANIMATION;
            //sprite.setImage(statusImages.get(status));
            sprite.setImage(statusImages.get(status), 12,3,3,1, 32,    32);
            PauseTransition delay = new PauseTransition(Duration.millis(2000));
            delay.setOnFinished(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent t) {
                    status = DEFAULT;
                    //sprite.setImage(statusImages.get(status));
                    sprite.setImage(statusImages.get(status), 1,1,0,0, 0,    0);
                }
            });
            delay.play();
        }

    }

    @Override
    public String toString()
    {
        return "Actor{" +
                "onAction='" + onAction + '\'' +
                ", status=" + status +
                ", statusImages=" + statusImages +
                ", sprite=" + sprite.getName() +
                '}';
    }

    private void changeStatus()
    {
        if (status == ON)
            status = OFF;
        else
            status = ON;
    }

}
