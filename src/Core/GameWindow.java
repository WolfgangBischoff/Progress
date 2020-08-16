package Core;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;


public class GameWindow extends Stage
{
    public static ArrayList<String> input = new ArrayList<>();
    private static final String CLASSNAME = "GameWindow-";
    private static GameWindow singleton;
    private static long currentNanoRenderTimeGameWindow = 0L;
    private Stage gameStage;
    private Scene gameScene;
    GUIController currentView;
    boolean mouseClicked = false;
    Point2D mousePosition = new Point2D(0,0); //To avoid NullPointerException of mouse was not moved at first
    boolean mouseMoved;

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
        String methodName = "createNextScene()";
        this.currentView = controller;
        gameScene = new Scene(controller.load(), Config.GAME_WINDOW_WIDTH, Config.GAME_WINDOW_HEIGHT);
        //input
        gameScene.setOnKeyPressed(
                e -> {
                    String code = e.getCode().toString();
                    //System.out.println(CLASSNAME + methodName + " pushed: " + code);
                    if (!input.contains(code))
                        input.add(code);
                });
        gameScene.setOnKeyReleased(
                e -> {
                    String code = e.getCode().toString();
                    input.remove(code);
                });
        gameScene.setOnMouseClicked(event -> {
            mouseClicked = true;
        });
        gameScene.setOnMouseMoved(event -> {
            //System.out.println(className + methodName + "Mouse moved");
            mouseMoved = true;
            mousePosition = new Point2D(event.getX(), event.getY());
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

    public static ArrayList<String> getInput()
    {
        return input;
    }

    public GUIController getCurrentView()
    {
        return currentView;
    }

    public boolean isMouseClicked()
    {
        return mouseClicked;
    }

    public Point2D getMousePosition()
    {
        return mousePosition;
    }

    public boolean isMouseMoved()
    {
        return mouseMoved;
    }

    public void setMouseMoved(boolean mouseMoved)
    {
        this.mouseMoved = mouseMoved;
    }

    public static String getCLASSNAME()
    {
        return CLASSNAME;
    }

    public static long getCurrentNanoRenderTimeGameWindow()
    {
        return currentNanoRenderTimeGameWindow;
    }

    public Stage getGameStage()
    {
        return gameStage;
    }

    public Scene getGameScene()
    {
        return gameScene;
    }

    public static void setInput(ArrayList<String> input)
    {
        GameWindow.input = input;
    }

    public static void setSingleton(GameWindow singleton)
    {
        GameWindow.singleton = singleton;
    }

    public static void setCurrentNanoRenderTimeGameWindow(long currentNanoRenderTimeGameWindow)
    {
        GameWindow.currentNanoRenderTimeGameWindow = currentNanoRenderTimeGameWindow;
    }

    public void setGameStage(Stage gameStage)
    {
        this.gameStage = gameStage;
    }

    public void setGameScene(Scene gameScene)
    {
        this.gameScene = gameScene;
    }

    public void setCurrentView(GUIController currentView)
    {
        this.currentView = currentView;
    }

    public void setMouseClicked(boolean mouseClicked)
    {
        this.mouseClicked = mouseClicked;
    }

    public void setMousePosition(Point2D mousePosition)
    {
        this.mousePosition = mousePosition;
    }
}