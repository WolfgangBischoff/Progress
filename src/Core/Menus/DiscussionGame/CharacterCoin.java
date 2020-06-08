package Core.Menus.DiscussionGame;

import Core.Menus.MyersBriggsCharacteristic;
import Core.Utilities;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import org.w3c.dom.Element;

public class CharacterCoin
{
    Circle collisionCircle;
    Image image;
    MyersBriggsCharacteristic characteristic;
    //Trait??
    Point2D startPosition;
    int collisionRadius;
    double speed;
    String movementType;
    int time_ms;

    //Jump
    double relativeJumpHeight = 0;

    public CharacterCoin(MyersBriggsCharacteristic characteristic, Point2D startPosition, int collisionRadius, int speed, String movementType, int time)
    {
        this.collisionRadius = collisionRadius;
        this.image = findImage(characteristic.toString());
        this.characteristic = characteristic;
        this.startPosition = startPosition;
        this.speed = speed;
        this.movementType = movementType;
        collisionCircle = new Circle(startPosition.getX(),startPosition.getY(),collisionRadius);
        this.time_ms = time;
    }

    public CharacterCoin(Element xmlNode)
    {
        this.characteristic = MyersBriggsCharacteristic.getType(xmlNode.getAttribute("characteristic"));
        int startX = Integer.parseInt(xmlNode.getAttribute("x"));
        int startY = Integer.parseInt(xmlNode.getAttribute("y"));
        this.collisionRadius = Integer.parseInt(xmlNode.getAttribute("radius"));
        this.startPosition = new Point2D(startX,startY);
        collisionCircle = new Circle(startPosition.getX(),startPosition.getY(),collisionRadius);
        this.speed = Integer.parseInt(xmlNode.getAttribute("speed"));
        this.movementType = xmlNode.getAttribute("movementType").toLowerCase();
        this.time_ms = Integer.parseInt(xmlNode.getAttribute("time"));
        this.image = findImage(characteristic.toString());

        if(movementType.equals("jump"))
        {
            this.relativeJumpHeight = Integer.parseInt(xmlNode.getAttribute("relative_jumpheight"));
        }
    }

    private Image findImage(String characteristicOrTrait)
    {
        switch (characteristicOrTrait.toLowerCase())
        {
            case "introversion":return Utilities.readImage("Core/Menus/DiscussionGame/introvert.png");
            case "extroversion":return Utilities.readImage("Core/Menus/DiscussionGame/extrovert.png");
            case "intuition":return Utilities.readImage("Core/Menus/DiscussionGame/intuition.png");
            case "sensing":return Utilities.readImage("Core/Menus/DiscussionGame/sensing.png");
            case "thinking":return Utilities.readImage("Core/Menus/DiscussionGame/thinking.png");
            case "feeling":return Utilities.readImage("Core/Menus/DiscussionGame/feeling.png");
            case "judging":return Utilities.readImage("Core/Menus/DiscussionGame/judging.png");
            case "perceiving":return Utilities.readImage("Core/Menus/DiscussionGame/perceiving.png");
            default: throw new RuntimeException("No path kown");
        }
    }



    @Override
    public String toString()
    {
        return
                "collisionCircle=" + collisionCircle +
                ", image=" + image +
                ", characteristic=" + characteristic +
                ", startPosition=" + startPosition +
                ", collisionRadius=" + collisionRadius +
                ", speed=" + speed +
                ", movementType='" + movementType + '\'' +
                '}';
    }
}
