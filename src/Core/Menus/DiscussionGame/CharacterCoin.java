package Core.Menus.DiscussionGame;

import Core.Menus.MyersBriggsCharacteristic;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;


public class CharacterCoin
{
    Circle collisionCircle;
    Image image;
    MyersBriggsCharacteristic characteristic;
    //Trait??
    Point2D startPosition;
    int collisionRadius;
    int speed;
    String movementType;
    int time_ms;

    public CharacterCoin(String imagepath, MyersBriggsCharacteristic characteristic, Point2D startPosition, int collisionRadius, int speed, String movementType, int time)
    {
        this.collisionRadius = collisionRadius;
        this.image = new Image(imagepath);
        this.characteristic = characteristic;
        this.startPosition = startPosition;
        this.speed = speed;
        this.movementType = movementType;
        collisionCircle = new Circle(startPosition.getX(),startPosition.getY(),collisionRadius);
        this.time_ms = time;
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
