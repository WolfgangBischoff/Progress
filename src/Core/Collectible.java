package Core;

import Core.Enums.CollectableType;

public class Collectible
{
    String name;
    CollectableType type;

    public Collectible(String name, CollectableType type)
    {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "{" + name + " " + type + "}";
    }


}
