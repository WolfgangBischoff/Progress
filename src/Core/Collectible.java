package Core;

import Core.Enums.CollectableType;

public class Collectible
{
    String nameGame;
    String name;
    CollectableType type;

    public Collectible(String name, CollectableType type, String nameGame)
    {
        this.name = name;
        this.type = type;
        this.nameGame = nameGame;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Collectible)) return false;
        Collectible that = (Collectible) o;
        return name.equals(that.name) &&
                type == that.type;
    }

    @Override
    public String toString()
    {
        return "{" + name + " " + type + "}";
    }


}
