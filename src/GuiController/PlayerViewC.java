package GuiController;

import Core.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
