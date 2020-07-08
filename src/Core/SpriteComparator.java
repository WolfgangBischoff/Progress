package Core;

import java.util.Comparator;

public class SpriteComparator implements Comparator<Sprite>
{
    @Override
    public int compare(Sprite object1, Sprite object2)
    {
        //if both blocking use hitbox to compare who is in front (projector)
        if(object1.getIsBlocker() && object2.getIsBlocker())
            return Double.compare(object1.positionY + object1.getHitBoxOffsetY(), object2.positionY + object2.getHitBoxOffsetY());

        //positive if object1 > object2
        return Double.compare(object1.positionY, object2.positionY);
    }
}
