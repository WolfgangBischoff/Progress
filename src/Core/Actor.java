package Core;

public class Actor
{
    public static Actor createActor(String type)
    {
        switch (type)
        {
            case "Player": return new Person();
            default: throw new RuntimeException("creator type unkonwn");
        }
    }

    public void interact(Sprite sprite)
    {
        System.out.println(sprite.getName() + " interacts with " + sprite.getName());
    }
}
