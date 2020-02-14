package Core;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.ArrayList;


public class GameWindow extends Stage
{
    public static ArrayList<String> input = new ArrayList<String>();
    private static GameWindow singleton;
    private Stage gameStage;
    private Scene gameScene;
    GUIController currentView;
    private static long currentNanoRenderTimeGameWindow = 0l;

    public static ArrayList<String> getInput()
    {
        return input;
    }

    private GameWindow()
    {
        gameStage = new Stage();
    }

    public static GameWindow getSingleton()
    {
        if (singleton == null)
            singleton = new GameWindow();
        return singleton;
    }

    public void createNextScene(GUIController controller)
    {
        this.currentView = controller;
        gameScene = new Scene(controller.load(), Config.GAMEWINDOWWIDTH, Config.GAMEWINDOWHEIGTH);
        //input
        gameScene.setOnKeyPressed(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                        String code = e.getCode().toString();
                        if (!input.contains(code))
                            input.add(code);
                    }
                });
        gameScene.setOnKeyReleased(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                        String code = e.getCode().toString();
                        input.remove(code);
                    }
                });
        gameStage.setScene(gameScene);
    }

    public void update(Long elapsedTime)
    {
        currentView.update(elapsedTime);
    }

    public void render(Long currentNanoTime)
    {
        currentNanoRenderTimeGameWindow = currentNanoTime; //To get Time somewhere else when needed
        currentView.render(currentNanoTime);
    }


    public double getScreenWidth()
    {
        return gameStage.getScene().getWidth();
    }

    public double getScreenHeight()
    {
        return gameStage.getScene().getHeight();
    }

    public void showWindow()
    {
        gameStage.show();
    }

    public long getRenderTime()
    {
        return currentNanoRenderTimeGameWindow;
    }

}