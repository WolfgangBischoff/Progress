package Core;

public class Person
{
    String name = "PersonName";
    Sprite sprite = new Sprite();


    public Person()
    {
        sprite.setImage("/res/img/bee.png");
    }

    public Sprite getSprite()
    {
        return sprite;
    }
}
