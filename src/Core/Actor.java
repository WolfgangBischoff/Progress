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
    Status status;
    Map<Status, SpriteData> spriteData = new HashMap<>();
    Sprite sprite;

    public Actor(Sprite sprite, Status initStatus)
    {
        this.sprite = sprite;
        this.status = initStatus;
        List<String[]> actordata;
        Path path = Paths.get("src/res/actorData/" + sprite.getName() + ".csv");
        if (Files.exists(path))
        {
            actordata = Utilities.readAllLineFromTxt("src/res/actorData/" + sprite.getName() + ".csv");
            for (String[] linedata : actordata)
            {
                if (linedata[0].equals("action"))
                {
                    onAction = linedata[1];//Different to method readSpriteData
                    continue;
                }

                Status status = Status.getStatus(linedata[0]);
                SpriteData data = SpriteData.tileDefinition(linedata);
                data.animationDuration = Integer.parseInt(linedata[SpriteData.animationDurationIdx]);
                spriteData.put(status, data);
            }
        }
        else throw new RuntimeException("Actordata not found: " + sprite.getName());
    }

    public Actor(String spritename, Status initStatus)
    {
        //this.sprite = sprite;
        this.status = initStatus;
        List<String[]> actordata;
        Path path = Paths.get("src/res/actorData/" + spritename + ".csv");
        if (Files.exists(path))
        {
            actordata = Utilities.readAllLineFromTxt("src/res/actorData/" + spritename + ".csv");
            for (String[] linedata : actordata)
            {
                if (linedata[0].equals("action"))
                {
                    onAction = linedata[1];//Different to method readSpriteData
                    continue;
                }

                Status status = Status.getStatus(linedata[0]);
                SpriteData data = SpriteData.tileDefinition(linedata);
                data.animationDuration = Integer.parseInt(linedata[SpriteData.animationDurationIdx]);
                spriteData.put(status, data);
            }
        }
        else throw new RuntimeException("Actordata not found: " + spritename);
    }

    static public Map<Status, SpriteData> readSpriteData(String actorname)
    {
        Map<Status, SpriteData> spriteData = new HashMap<>();
        List<String[]> actordata;
        Path path = Paths.get("src/res/actorData/" + actorname + ".csv");
        if (Files.exists(path))
        {
            actordata = Utilities.readAllLineFromTxt("src/res/actorData/" + actorname + ".csv");
            for (String[] linedata : actordata)
            {
                if (linedata[0].equals("action"))
                    continue;

                Status status = Status.getStatus(linedata[0]);
                spriteData.put(status, SpriteData.tileDefinition(linedata));
            }
        }
        else throw new RuntimeException("Actordata not found: " + actorname);

        return spriteData;
    }

    static public SpriteData readSpriteData(String actorname, Status initStatus)
    {
        return readSpriteData(actorname).get(initStatus);
    }

    /*
    private void changeLayer(int targetLayer)
    {
        //List<Sprite> currentLayer;
        WorldView.bottomLayer.remove(sprite);
        WorldView.middleLayer.remove(sprite);
        WorldView.topLayer.remove(sprite);
        switch (targetLayer)
        {
            case 0: WorldView.bottomLayer.add(sprite);
            case 1: WorldView.middleLayer.add(sprite);
            case 2: WorldView.topLayer.add(sprite);
        }
    }*/

    public void act()
    {
        String methodName = "Actor/act(): ";

        if (onAction == "nothing")
            return;

        if (onAction.equals("statusChange"))
        {
            changeStatus();
            SpriteData ts = spriteData.get(status);
            System.out.print(methodName + ts.spriteName);
            sprite.setImage(ts.spriteName, ts.fps, ts.totalFrames, ts.cols, ts.rows, ts.frameWidth, ts.frameHeight);
            sprite.setBlocker(ts.blocking);
            //changeLayer(ts.priority);
        }

        if (onAction.equals("animation"))
        {
            status = ANIMATION;
            SpriteData ts = spriteData.get(status);
            sprite.setImage(ts.spriteName, ts.fps, ts.totalFrames, ts.cols, ts.rows, ts.frameWidth, ts.frameHeight);
            sprite.setBlocker(ts.blocking);
            PauseTransition delay = new PauseTransition(Duration.millis(ts.animationDuration * 1000));
            delay.setOnFinished(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent t)
                {
                    status = DEFAULT;
                    SpriteData ts = spriteData.get(status);
                    sprite.setImage(ts.spriteName, ts.fps, ts.totalFrames, ts.cols, ts.rows, ts.frameWidth, ts.frameHeight);
                    sprite.setBlocker(ts.blocking);
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
