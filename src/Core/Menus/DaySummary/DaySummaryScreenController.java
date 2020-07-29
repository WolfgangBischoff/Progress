package Core.Menus.DaySummary;

import Core.GameVariables;
import Core.GameWindow;
import Core.WorldView;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

import static Core.Config.*;

public class DaySummaryScreenController
{
    private static final String CLASSNAME = "DaySummaryScreenController ";
    private static final int WIDTH = DAY_SUMMARY_WIDTH;
    private static final int HEIGHT = DAY_SUMMARY_HEIGHT;

    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private WritableImage writableImage;
    private Integer highlightedElement;
    private List<String> interfaceElements_list = new ArrayList<>();
    private Point2D mousePosRelativeToDiscussionOverlay;
    private DaySummary daySummary;

    //MAM Information
    private int mamInfoWidth = 300;
    private int mamInfoHeight = 300;
    private int mamInfo_x = WIDTH - mamInfoWidth - 50;
    private int mamInfo_y = 50;
    Rectangle2D mamInformationArea = new Rectangle2D(mamInfo_x, mamInfo_y, mamInfoWidth, mamInfoHeight);

    //Close Button
    private String CLOSE_BUTTON_ID = "Close";
    private int closeButton_x = 700;
    private int closeButton_y = 500;
    private int closeButton_width = 150;
    private int closeButton_height = 50;
    Rectangle2D closeButton = new Rectangle2D(closeButton_x, closeButton_y, closeButton_width, closeButton_height);

    public DaySummaryScreenController(DaySummary daySummary)
    {
        canvas = new Canvas(WIDTH, HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        highlightedElement = 0;
        this.daySummary = daySummary;
    }

    private void draw() throws NullPointerException
    {
        String methodName = "draw() ";
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);
        Color background = Color.rgb(60, 90, 85);
        double hue = background.getHue();
        double sat = background.getSaturation();
        double brig = background.getBrightness();
        Color marking = Color.hsb(hue, sat - 0.2, brig + 0.2);
        Color font = Color.hsb(hue, sat + 0.15, brig + 0.4);
        interfaceElements_list.clear();
        graphicsContext.setTextBaseline(VPos.CENTER);
        graphicsContext.setTextAlign(TextAlignment.CENTER);

        //Background
        graphicsContext.setGlobalAlpha(0.8);
        graphicsContext.setFill(background);
        int backgroundOffsetX = 16, backgroundOffsetY = 10;
        graphicsContext.fillRect(backgroundOffsetX, backgroundOffsetY, WIDTH - backgroundOffsetX * 2, HEIGHT - backgroundOffsetY * 2);
        graphicsContext.setGlobalAlpha(1);

        //MaM message
        if (daySummary.isHasInterrogation())
            graphicsContext.setFill(Color.RED);
        else
            graphicsContext.setFill(Color.GREEN);
        graphicsContext.fillRect(mamInformationArea.getMinX(), mamInformationArea.getMinY(), mamInformationArea.getWidth(), mamInformationArea.getHeight());
        String stolenItemsString = GameVariables.getStolenCollectibles().toString();
        graphicsContext.setFill(font);
        graphicsContext.fillText(stolenItemsString, mamInformationArea.getMinX(), mamInformationArea.getMinY());

        //Close button
        graphicsContext.setFill(marking);
        interfaceElements_list.add(CLOSE_BUTTON_ID);
        if (highlightedElement == interfaceElements_list.indexOf(CLOSE_BUTTON_ID))
            graphicsContext.fillRect(closeButton.getMinX(), closeButton.getMinY(), closeButton.getWidth(), closeButton.getHeight());
        graphicsContext.setFill(font);
        graphicsContext.fillText(CLOSE_BUTTON_ID, closeButton.getMinX() + closeButton.getWidth() / 2, closeButton.getMinY() + closeButton.getHeight() / 2);

        SnapshotParameters transparency = new SnapshotParameters();
        transparency.setFill(Color.TRANSPARENT);
        writableImage = canvas.snapshot(transparency, null);

    }

    public void processKey(ArrayList<String> input, Long currentNanoTime)
    {
        String methodName = "processKey() ";

        int maxMarkedOptionIdx = interfaceElements_list.size() - 1;
        int newMarkedOption = highlightedElement;
        double elapsedTimeSinceLastInteraction = (currentNanoTime - WorldView.getPlayer().getActor().getLastInteraction()) / 1000000000.0;
        if (!(elapsedTimeSinceLastInteraction > TIME_BETWEEN_DIALOGUE))
            return;

        if (input.contains("E") || input.contains("ENTER") || input.contains("SPACE"))
        {
            activateHighlightedOption(currentNanoTime);
            return;
        }
        if (input.contains("W") || input.contains("UP"))
        {
            newMarkedOption--;
        }
        if (input.contains("S") || input.contains("DOWN"))
            newMarkedOption++;

        if (newMarkedOption < 0)
            newMarkedOption = maxMarkedOptionIdx;
        if (newMarkedOption > maxMarkedOptionIdx)
            newMarkedOption = 0;

        if (highlightedElement != newMarkedOption)
        {
            setHighlightedElement(newMarkedOption);
            WorldView.getPlayer().getActor().setLastInteraction(currentNanoTime);
            draw();
        }

    }

    public void processMouse(Point2D mousePosition, boolean isMouseClicked, Long currentNanoTime)
    {
        String methodName = "processMouse(Point2D, boolean) ";
        Point2D discussionOverlayPosition = WorldView.getPersonalityScreenPosition();
        Rectangle2D discussionPosRelativeToWorldview = new Rectangle2D(discussionOverlayPosition.getX(), discussionOverlayPosition.getY(), DISCUSSION_WIDTH, DISCUSSION_HEIGHT);

        //Calculate Mouse Position relative to Discussion
        if (discussionPosRelativeToWorldview.contains(mousePosition))
            mousePosRelativeToDiscussionOverlay = new Point2D(mousePosition.getX() - discussionOverlayPosition.getX(), mousePosition.getY() - discussionOverlayPosition.getY());
        else mousePosRelativeToDiscussionOverlay = null;

        Integer hoveredElement = null;
        //Check if hovered over Rhetoric Button
        if (closeButton.contains(mousePosRelativeToDiscussionOverlay))
            hoveredElement = interfaceElements_list.indexOf(CLOSE_BUTTON_ID);

        if (GameWindow.getSingleton().isMouseMoved() && hoveredElement != null)//Set highlight if mouse moved
        {
            setHighlightedElement(hoveredElement);
            GameWindow.getSingleton().setMouseMoved(false);
        }

        if (isMouseClicked && hoveredElement != null)//To prevent click of not hovered
        {
            activateHighlightedOption(currentNanoTime);
        }
    }

    private void activateHighlightedOption(Long currentNanoTime)
    {
        String methodName = "activateHighlightedOption(Long) ";
        if (highlightedElement == null)
        {
            System.out.println(CLASSNAME + methodName + "nothing highlighted");
            return;
        }

        if (interfaceElements_list.get(highlightedElement).equals(CLOSE_BUTTON_ID))
        {
            daySummary.endDay();
            //Same level loads but next day
            String levelname = WorldView.getSingleton().getLevelName();
            String spawnId = "bed";
            WorldView.getSingleton().loadStage(levelname, spawnId);
            WorldView.setIsDaySummaryActive(false);
        }

        WorldView.getPlayer().getActor().setLastInteraction(currentNanoTime);
    }

    public void setHighlightedElement(Integer highlightedElement)
    {
        String methodName = "setHighlightedElement() ";
        boolean debug = false;
        if (debug && !this.highlightedElement.equals(highlightedElement))
            System.out.println(CLASSNAME + methodName + highlightedElement + " " + interfaceElements_list.get(highlightedElement));
        this.highlightedElement = highlightedElement;
    }

    public WritableImage getWritableImage()
    {
        draw();
        return writableImage;
    }

    public static int getMenuWidth()
    {
        return WIDTH;
    }

    public static int getMenuHeight()
    {
        return HEIGHT;
    }

}
