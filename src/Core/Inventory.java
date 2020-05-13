package Core;

import Core.Enums.CollectableType;
import javafx.scene.image.Image;


import java.util.ArrayList;
import java.util.List;

import static Core.Config.IMAGE_DIRECTORY_PATH;

public class Inventory
{
    private static final String CLASSNAME = "Inventory-";
    List<Collectible> itemsList = new ArrayList<>();
    Actor owner;

    public Inventory(Actor owner)
    {
        this.owner = owner;
    }

    public void addItem(String itemName, String collectableTypeString, String itemIngameName, Image image)
    {
        String methodName = "addItem(String, String) ";
        boolean debug = false;
        CollectableType collectableType = CollectableType.getType(collectableTypeString);
        Collectible collectible = new Collectible(itemName, collectableType, itemIngameName);
        collectible.image = image;
        itemsList.add(collectible);
        if (debug)
        {
            System.out.println(CLASSNAME + methodName + owner.actorInGameName + " collected " + collectible);
            System.out.println(CLASSNAME + methodName + " " + itemsList.toString());
        }
    }

    public void addItem(String itemName, String collectableTypeString, String itemIngameName)
    {
        Image defaultImage = new Image(IMAGE_DIRECTORY_PATH + "notfound_64_64");
        addItem(itemName, collectableTypeString, itemIngameName, defaultImage);
    }

    public boolean contains(Collectible toCheck)
    {
        return itemsList.contains(toCheck);
    }

    @Override
    public String toString()
    {
        return  owner.actorInGameName +
                " inv: " + itemsList.toString()
                ;
    }
}
