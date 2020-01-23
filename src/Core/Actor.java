package Core;


import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.Status.*;

public class Actor implements PropertyChangeListener
{
    String className = "Actor ";
    String onAction = "nothing";
    String onInRange = "nothing";
    Status status;
    Map<Status, List<SpriteData>> spriteDataList = new HashMap<>();
    //Map<Status, SpriteData> spriteData = new HashMap<>();
    List<Sprite> spriteList = new ArrayList<>();
    //Sprite sprite;


    public Actor(String spritename, Status initStatus)
    {
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

                //Collect ActorData
                Status status = Status.getStatus(linedata[0]);
                SpriteData data = SpriteData.tileDefinition(linedata);
                data.animationDuration = Integer.parseInt(linedata[SpriteData.animationDurationIdx]);
                //spriteData.put(status, data);

                if (!spriteDataList.containsKey(status))
                    spriteDataList.put(status, new ArrayList<>());
                spriteDataList.get(status).add(data);

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
        List<SpriteData> targetSpriteData = spriteDataList.get(status);
        //For all Sprites of the actor update to new Status
        for (int i = 0; i < spriteList.size(); i++)
        {
            SpriteData ts = targetSpriteData.get(i);
            Sprite toChange = spriteList.get(i);
            toChange.setImage(ts.spriteName, ts.fps, ts.totalFrames, ts.cols, ts.rows, ts.frameWidth, ts.frameHeight);
            toChange.setBlocker(ts.blocking);
            toChange.setLightningSpriteName(ts.lightningSprite);

            //System.out.println(className + methodName + " animated: " + toChange.getAnimated() + " heightLayer: " + ts.heightLayer);
            changeLayer(toChange, ts.heightLayer);
        }

    }

    public void onInRange(Sprite otherSprite)
    {
        String methodName = "onInRange() ";

        //TODO from config
        if(spriteList.get(0).getName().equals("bulkhead"))
            onInRange = "statusChangeTimer";

        if(onInRange.equals("statusChangeTimer"))
        {
            //changeStatus();
            status = OFF;
            System.out.println(className + methodName + spriteList.get(0).getName());
            //changeSprites();

            //status = ANIMATION;
            changeSprites();
            List<SpriteData> targetSpriteData = spriteDataList.get(status);
            int animationDuration = targetSpriteData.get(0).animationDuration;
            PauseTransition delay = new PauseTransition(Duration.millis(animationDuration * 1000));
            delay.setOnFinished(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent t)
                {
                    System.out.println(className + methodName + spriteList.get(0).getName() + " reset");
                    status = ON;
                    changeSprites();
                }
            });
            delay.play();
        }
    }

    public void act()
    {
        String methodName = "act(): ";

        if (onAction == "nothing")
            return;

        if (onAction.equals("statusChange"))
        {
            changeStatus();
            changeSprites();
        }

        if (onAction.equals("animation"))
        {
            status = ANIMATION;
            changeSprites();
            List<SpriteData> targetSpriteData = spriteDataList.get(status);
            int animationDuration = targetSpriteData.get(0).animationDuration;
            PauseTransition delay = new PauseTransition(Duration.millis(animationDuration * 1000));
            delay.setOnFinished(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent t)
                {
                    status = DEFAULT;
                    changeSprites();
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
                //  ", sprite=" + sprite.getName() +
                '}';
    }

    private void changeStatus()
    {
        if (status == ON)
            status = OFF;
        else if (status == OFF)
            status = ON;

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        String methodName = "propertyChange() ";
        Status oldStatus = status;
        if (evt.getPropertyName() == "direction" || evt.getPropertyName() == "velocity")
        {
            Sprite firstSprite = spriteList.get(0);//First Sprite of Sprite list
            //String statusString = sprite.getDirection().toString();
            String statusString = firstSprite.getDirection().toString();

            for (int i = 0; i < spriteList.size(); i++)
            {
               //System.out.println(className + methodName + spriteList.get(i).getName() + " " + i);
            }

            if (firstSprite.isMoving())
                statusString = statusString + "moving";
            status = Status.getStatus(statusString);
        }

        if (status != oldStatus)
        {
            changeSprites();
        }
    }

    public void addSprite(Sprite sprite)
    {
        spriteList.add(sprite);
        sprite.addToListener(this);
    }

    /*
    public void setSprite(Sprite sprite)
    {
        //this.sprite = sprite;
        this.sprite = sprite;
        sprite.addToListener(this);
    }
    */
}
