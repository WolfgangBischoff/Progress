package Core;

import GuiController.MainMenuController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage){

        //Game Window
        GameWindow gameWindowController = GameWindow.getSingleton();
        MainMenuController mainMenuController = new MainMenuController();
        gameWindowController.setTitle("Game Window");
        gameWindowController.createNextScene(mainMenuController);
        gameWindowController.showWindow();

        new AnimationTimer()
        {
            Long lastNanoTime = System.nanoTime() ;
            public void handle(long currentNanoTime)
            {
                // calculate time since last update.
                double elapsedTime = (currentNanoTime - lastNanoTime) / 1000000000.0;
                lastNanoTime = currentNanoTime;

                gameWindowController.update(elapsedTime);
                gameWindowController.render(elapsedTime);
            }
        }.start();




















/*
        theStage.setTitle( "Keyboard Example" );

        Group root = new Group();
        Scene theScene = new Scene( root );
        theStage.setScene( theScene );

        Canvas canvas = new Canvas( 512 - 64, 256 );
        root.getChildren().add( canvas );

        ArrayList<String> input = new ArrayList<String>();

        theScene.setOnKeyPressed(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                        String code = e.getCode().toString();

                        // only add once... prevent duplicates
                        if ( !input.contains(code) )
                            input.add( code );
                    }
                });

        theScene.setOnKeyReleased(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                        String code = e.getCode().toString();
                        input.remove( code );
                    }
                });

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image left = new Image( "res/img/pen.png" );
        Image leftG = new Image( "res/img/food.png" );

        Image right = new Image( "res/img/pen.png" );
        Image rightG = new Image( "res/img/food.png" );

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                // Clear the canvas
                gc.clearRect(0, 0, 512,512);

                if (input.contains("LEFT"))
                    gc.drawImage( leftG, 64, 64 );
                else
                    gc.drawImage( left, 64, 64 );

                if (input.contains("RIGHT"))
                    gc.drawImage( rightG, 256, 64 );
                else
                    gc.drawImage( right, 256, 64 );
            }
        }.start();

        theStage.show();
*/

















        /*
        theStage.setTitle( "Timeline Example" );

        Group root = new Group();
        Scene theScene = new Scene( root );
        theStage.setScene( theScene );

        Canvas canvas = new Canvas( 512, 512 );
        root.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image earth = new Image( "res/img/ufo_1.png" );
        Image sun   = new Image( "res/img/pen.png" );
        Image space = new Image( "res/img/space.png" );
        AnimatedImage ufo = new AnimatedImage();
        Image[] imageArray = new Image[3];
        for (int i = 0; i < 3; i++)
            imageArray[i] = new Image( "res/img/ufo_" + i + ".png" );
        ufo.frames = imageArray;
        ufo.duration = 0.100;

        Timeline gameLoop = new Timeline();
        gameLoop.setCycleCount( Timeline.INDEFINITE );

        final long timeStart = System.currentTimeMillis();

        KeyFrame kf = new KeyFrame(
                Duration.seconds(0.017),                // 60 FPS
                new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent ae)
                    {
                        double t = (System.currentTimeMillis() - timeStart) / 1000.0;

                        double x = 232 + 128 * Math.cos(t);
                        double y = 232 + 128 * Math.sin(t);

                        // Clear the canvas
                        gc.clearRect(0, 0, 512,512);

                        // background image clears canvas
                        gc.drawImage( space, 0, 0 );
                        gc.drawImage( earth, x, y );
                        gc.drawImage( sun, 196, 196 );
                        gc.drawImage( ufo.getFrame(t), 450, 25 );
                    }
                });

        gameLoop.getKeyFrames().add( kf );
        gameLoop.play();

        theStage.show();
        */


    }


    public static void main(String[] args) {
        launch(args);
    }
}
