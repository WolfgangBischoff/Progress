package Core;

import Core.Enums.CollectableType;
import javafx.scene.image.Image;

public class Collectible
{
    String ingameName;
    String name;
    Image image;
    CollectableType type;
    int baseValue;

    public Collectible(String name, CollectableType type, String nameGame, int baseValue)
    {
        this.name = name;
        this.type = type;
        this.ingameName = nameGame;
        this.baseValue = baseValue;
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
        return "{" + name + " " + type +  " value: " + baseValue + "}";
    }

    public String getIngameName()
    {
        return ingameName;
    }

    public String getName()
    {
        return name;
    }

    public Image getImage()
    {
        return image;
    }

    public CollectableType getType()
    {
        return type;
    }

    public int getBaseValue()
    {
        return baseValue;
    }
}
