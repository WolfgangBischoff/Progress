package GuiController;

import Core.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class PlayerViewC implements GUIController
{
    @FXML
    Canvas canvas;
    @FXML
    private Button buttonToMenu;

    FXMLLoader loader;
    GraphicsContext gc;
    Image background;
    Sprite player;
    GUIController center;

    public PlayerViewC()
    {
        loader = new FXMLLoader(getClass().getResource("/fxml/PlayerView.fxml"));
        loader.setController(this);
    }

    @FXML
    void initialize()
    {

        /*
        gc = canvas.getGraphicsContext2D();
        background = new Image("/res/img/background.jpg");
        Person person = new Person();
        player = person.getSprite();
        player.setPosition(200, 0);
*/
        //WorldView worldEnviroment = new WorldView("test");
        center = new WorldView("test", canvas.getGraphicsContext2D());

        buttonToMenu.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.out.println("To Menu");
            }
        });

    }

    @Override
    public void update(Double elapsedTime)
    {
        center.update(elapsedTime);
    }

    @Override
    public void render(Double elapsedTime)
    {
        center.render(elapsedTime);
    }

    @FXML
    private void openMenu()
    {

    }


    public Pane load()
    {

        try
        {
            return loader.load();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
