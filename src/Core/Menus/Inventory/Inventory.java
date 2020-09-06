package Core.Menus.Inventory;

import Core.Actor;
import Core.Collectible;
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

    public void addItem(Collectible collectible)
    {
        String methodName = "addItem(String, String) ";
        boolean debug = false;
        itemsList.add(collectible);
        if (debug)
        {
            System.out.println(CLASSNAME + methodName + owner.getActorInGameName() + " collected " + collectible);
            System.out.println(CLASSNAME + methodName + " " + itemsList.toString());
        }
    }

    public void removeItem(Collectible collectible)
    {
        String methodName = "removeItem(String, String) ";
        boolean debug = false;
        itemsList.remove(collectible);
    }



    public boolean contains(Collectible toCheck)
    {
        return itemsList.contains(toCheck);
    }

    public void removeAll(List<Collectible> collectibles)
    {
        itemsList.removeAll(collectibles);
    }

    @Override
    public String toString()
    {
        return  owner.getActorInGameName() +
                " inv: " + itemsList.toString()
                ;
    }

    public List<Collectible> getItemsList()
    {
        return itemsList;
    }

    public int size()
    {
        return itemsList.size();
    }
}
