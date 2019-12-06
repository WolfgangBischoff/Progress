package GuiController;

import Core.*;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

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
        //gc = graphicsContext;
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
        PlayerViewC playerViewC = new PlayerViewC();
        //GameWindow.getSingleton().createNextScene(playerViewC.load());
        GameWindow.getSingleton().createNextScene(playerViewC);
    }

    @Override
    public void update(Double elapsedTime)
    {

    }

    @Override
    public void render(Double elapsedTime)
    {

    }

}
