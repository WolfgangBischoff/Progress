package Core;

import java.util.Comparator;

public class SpriteComparator implements Comparator<Sprite>
{
    @Override
    public int compare(Sprite object1, Sprite object2)
    {
        //positive if object1 > object2
        return Double.compare(object1.positionY, object2.positionY);
    }
}
