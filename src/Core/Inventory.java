package Core;

import Core.Enums.CollectableType;

import java.util.ArrayList;
import java.util.List;

public class Inventory
{
    private static final String CLASSNAME = "Inventory-";
    List<Collectible> itemsList = new ArrayList<>();
    Actor owner;

    public Inventory(Actor owner)
    {
        this.owner = owner;
    }

    public void addItem(String itemName, String collectableTypeString, String itemIngameName)
    {
        String methodName = "addItem(String, String) ";
        boolean debug = false;
        CollectableType collectableType = CollectableType.getType(collectableTypeString);
        Collectible collectible = new Collectible(itemName, collectableType, itemIngameName);
        itemsList.add(collectible);
        if (debug)
        {
            System.out.println(CLASSNAME + methodName + owner.actorInGameName + " collected " + collectible);
            System.out.println(CLASSNAME + methodName + " " + itemsList.toString());
        }
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
