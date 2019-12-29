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
    Canvas worldCanvas;
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
        worldCanvas.setWidth(Config.GAMEWINDOWWIDTH);
        worldCanvas.setHeight(Config.GAMEWINDOWHEIGTH);
        center = new WorldView("test", worldCanvas.getGraphicsContext2D());
        buttonToMenu.setFocusTraversable(false); //Disable Space triggering
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
    public void update(Long currentNanoTime)
    {
        center.update(currentNanoTime);
    }

    @Override
    public void render(Long currentNanoTime)
    {
        center.render(currentNanoTime);
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
