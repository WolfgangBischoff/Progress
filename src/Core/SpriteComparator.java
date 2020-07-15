package Core;

import java.util.Comparator;

public class SpriteComparator implements Comparator<Sprite>
{
    final static String CLASSNAME = "SpriteComparator-";
    @Override
    public int compare(Sprite object1, Sprite object2)
    {
        String methodName = "compare()";

        //if both blocking use hitbox to compare who is in front (projector)
        if(object1.getIsBlocker() && object2.getIsBlocker())
            return Double.compare(object1.positionY + object1.getHitBoxOffsetY(), object2.positionY + object2.getHitBoxOffsetY());

        //if(object1.getActor() != null && object2.getActor() != null && object1.getActor().getActorInGameName().equals("Revolutionary") && object2.getActor().getActorInGameName().equals("shower"))
         //   System.out.println(CLASSNAME + methodName + "Player: " +  object1.positionY + " " +  object2.positionY);

        //positive if object1 > object2
        return Double.compare(object1.positionY, object2.positionY);
    }
}
