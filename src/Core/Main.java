package Core;

import GuiController.MainMenuController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage)
    {
        //Game Window
        GameWindow gameWindowController = GameWindow.getSingleton();
        MainMenuController mainMenuController = new MainMenuController();
        gameWindowController.setTitle("Game Window");
        gameWindowController.createNextScene(mainMenuController);
        gameWindowController.showWindow();


        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                gameWindowController.update(currentNanoTime);
                gameWindowController.render(currentNanoTime);
            }
        }.start();
    }

    //For some reason ok
//    public static void main(String[] args) {
//        launch();
//    }
}
