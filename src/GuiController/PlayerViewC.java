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
    Pane root;
    FXMLLoader loader;
    GUIController center;

    public PlayerViewC()
    {
        loader = new FXMLLoader(getClass().getResource("/fxml/PlayerView.fxml"));
        loader.setController(this);
    }

    @FXML
    void initialize()
    {
        //center = new WorldView("test", root);
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

    public Pane load()
    {

        try
        {
            return loader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
