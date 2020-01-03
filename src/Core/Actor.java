package Core;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.Status.OFF;
import static Core.Status.ON;

public class Actor
{
    String onAction = "nothing";
    Status status = ON;
    Map<Status, String> statusImages = new HashMap<>();
    Sprite sprite;

    public Actor(Sprite sprite)
    {
        this.sprite = sprite;
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

            System.out.println("Actor: " + sprite.getName() + " " + statusImages);
        }
    }


    public void act()
    {
        //System.out.println("Actor: " + toString());
        if (onAction == "nothing")
            return;

        if (onAction.equals("statusChange"))
            changeStatus();

        sprite.setImage(statusImages.get(status));
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
