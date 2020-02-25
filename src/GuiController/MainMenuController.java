package GuiController;

import Core.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;


public class MainMenuController implements GUIController
{
    FXMLLoader loader;
    GraphicsContext gc;

    @FXML
    private ImageView leftBar, rightBar;


    public MainMenuController()
    {
        loader = new FXMLLoader(getClass().getResource("/fxml/mainMenu.fxml"));
        loader.setController(this);
    }

    @FXML
    private void initialize()
    {
        leftBar.setImage(new Image("res/img/säulePlakat.png"));
        rightBar.setImage(new Image("res/img/säuleCommEco.png"));
    }

    @Override
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


    @FXML
    private void startAsCiv(ActionEvent event)
    {
        WorldView worldView = new WorldView("test");
        GameWindow.getSingleton().createNextScene(worldView);

        //PlayerViewC playerViewC = new PlayerViewC();
        //GameWindow.getSingleton().createNextScene(playerViewC);
    }

    @Override
    public void update(Long currentNanoTime)
    {

    }

    @Override
    public void render(Long currentNanoTime)
    {

    }

}
