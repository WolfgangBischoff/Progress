package Core;

import java.util.Comparator;

public class SpriteComparator implements Comparator<Sprite>
{
    final static String CLASSNAME = "SpriteComparator ";

    @Override
    public int compare(Sprite object1, Sprite object2)
    {
        String methodName = "compare() ";

        return Double.compare(object1.positionY + object1.getHitBoxOffsetY(), object2.positionY + object2.getHitBoxOffsetY());

        /*
        //if both blocking use hitbox to compare who is in front (projector)
        if (object1.getIsBlocker() && object2.getIsBlocker())
        {
            if (object1.getName().equals("player") || object2.getName().equals("player"))
            {
                System.out.println(CLASSNAME + methodName + object1.getName() + " 1 " + object2.getName());
            }
            return Double.compare(object1.positionY + object1.getHitBoxOffsetY(), object2.positionY + object2.getHitBoxOffsetY());
        }
        else
            System.out.println(CLASSNAME + methodName + object1.getName() + " 2 " + object2.getName());

        //positive if object1 > object2
        return Double.compare(object1.positionY, object2.positionY);

         */
    }
}
