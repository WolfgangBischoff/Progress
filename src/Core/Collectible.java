package Core;

import Core.Enums.CollectableType;
import javafx.scene.image.Image;

public class Collectible
{
    String ingameName;
    String name;
    Image image;
    CollectableType type;

    public Collectible(String name, CollectableType type, String nameGame)
    {
        this.name = name;
        this.type = type;
        this.ingameName = nameGame;
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

    public String getIngameName()
    {
        return ingameName;
    }
}
