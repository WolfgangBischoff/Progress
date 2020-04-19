package Core;

import Core.Enums.CollectableType;

import java.util.ArrayList;
import java.util.List;

public class Inventory
{
    private static final String CLASSNAME = "Inventory/";
    List<Collectible> itemsList = new ArrayList<>();
    Actor owner;

    public Inventory(Actor owner)
    {
        this.owner = owner;
    }

    public void addItem(String itemName, String collectableTypeString)
    {
        String methodName = "addItem(String, String) ";
        boolean debug = false;
        CollectableType collectableType = CollectableType.getType(collectableTypeString);
        Collectible collectable = new Collectible(itemName, collectableType);
        itemsList.add(collectable);
        if (debug)
            System.out.println(CLASSNAME + methodName + owner.actorInGameName + " collected " + collectable);
    }
}
