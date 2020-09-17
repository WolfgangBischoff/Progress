package Core.Menus.Personality;

import Core.Actor;
import Core.GameWindow;
import Core.WorldView.WorldView;
import Core.WorldView.WorldViewController;
import Core.WorldView.WorldViewStatus;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

import static Core.Config.*;

public class PersonalityScreenController
{
    /*
With increasing cooperation value you find trais of the person, some traits are difficult to get, or just achievable by external world. Add Topics
  The "Traits" menu show special actions like "Join party", Sabatoage, Give info, open way
  Boost cooperation value with presents or actions
   */

    private static final String CLASSNAME = "PersonalityScreenController ";
    private static final String BACK_BUTTON_ID = "back";
    private static final int WIDTH = PERSONALITY_WIDTH;
    private static final int HEIGHT = PERSONALITY_HEIGHT;
    private static final Point2D SCREEN_POSITION = PERSONALITY_POSITION;
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private WritableImage writableImage;
    Point2D mousePosRelativeToDiscussionOverlay;
    private Integer highlightedElement;
    private List<String> interfaceElements_list = new ArrayList<>();

    private Actor otherPersonActor;
    private PersonalityContainer personalityContainer;
    Actor player;

    //Rhetoric Button
    int rhetoric_x = 50;
    int rhetoric_y = 100;
    int rhetoric_width = 150;
    int rhetoric_height = 60;
    Rectangle2D rhetoric_Button = new Rectangle2D(rhetoric_x, rhetoric_y, rhetoric_width, rhetoric_height);

    //Other Person Traits
    private List<String> personalityList = new ArrayList<>();
    int initTraitsOffsetX = 350;
    int initTraitsOffsetY = 200;
    int traitsYGap = 15;
    Font traitsFont = new Font(25);

    public PersonalityScreenController(Actor otherPersonActor)
    {
        canvas = new Canvas(DISCUSSION_WIDTH, DISCUSSION_HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        this.otherPersonActor = otherPersonActor;
        highlightedElement = 0;
        player = WorldView.getPlayer().getActor();
        init();
    }

    private void init()
    {
        if (otherPersonActor.getPersonalityContainer() == null)
            throw new RuntimeException("Personality not defined in actorfile: " + otherPersonActor.getActorFileName());
        personalityContainer = otherPersonActor.getPersonalityContainer();
        updateVisiblePersonality();
    }

    private void updateVisiblePersonality()
    {
        personalityList.clear();
        if (personalityContainer.getCooperation() >= THRESHOLD_PERSONALITY)
            personalityList.add(personalityContainer.myersBriggsPersonality.toString());
        else
            personalityList.add("Unknown");
        if (personalityContainer.getCooperation() >= THRESHOLD_MOTIVATION)
            personalityList.add(personalityContainer.getMotivation().toString());
        else
            personalityList.add("Unknown");
        if (personalityContainer.getCooperation() >= THRESHOLD_DECISION)
            personalityList.add(personalityContainer.getDecision().toString());
        else
            personalityList.add("Unknown");
        if (personalityContainer.getCooperation() >= THRESHOLD_FOCUS)
            personalityList.add(personalityContainer.getFocus().toString());
        else
            personalityList.add("Unknown");
        if (personalityContainer.getCooperation() >= THRESHOLD_LIFESTYLE)
            personalityList.add(personalityContainer.getLifestyle().toString());
        else
            personalityList.add("Unknown");
    }

    private void draw() throws NullPointerException
    {
        String methodName = "draw() ";
        player = WorldView.getPlayer().getActor(); //Just needed as long the player resets with stage load (so we have always new Player)
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);
        Color background = Color.rgb(60, 90, 85);
        double hue = background.getHue();
        double sat = background.getSaturation();
        double brig = background.getBrightness();
        Color marking = Color.hsb(hue, sat - 0.2, brig + 0.2);
        Color font = Color.hsb(hue, sat + 0.15, brig + 0.4);
        interfaceElements_list.clear(); //Filled with each draw() Maybe better if filled just if elements change

        graphicsContext.setTextAlign(TextAlignment.LEFT);
        graphicsContext.setTextBaseline(VPos.TOP);

        //Background
        graphicsContext.setGlobalAlpha(0.8);
        graphicsContext.setFill(background);
        int backgroundOffsetX = 16, backgroundOffsetY = 10;
        graphicsContext.fillRect(backgroundOffsetX, backgroundOffsetY, WIDTH - backgroundOffsetX * 2, HEIGHT - backgroundOffsetY * 2);
        graphicsContext.setGlobalAlpha(1);

        //Rhetoric button
        graphicsContext.setFill(marking);
        interfaceElements_list.add(BACK_BUTTON_ID);
        if (highlightedElement == interfaceElements_list.indexOf(BACK_BUTTON_ID))
            graphicsContext.fillRect(rhetoric_Button.getMinX(), rhetoric_Button.getMinY(), rhetoric_Button.getWidth(), rhetoric_Button.getHeight());
        graphicsContext.setFill(font);
        graphicsContext.fillText("Back", rhetoric_Button.getMinX(), rhetoric_Button.getMinY());

        //Other Person Known Traits
        double fontsize = graphicsContext.getFont().getSize();
        int traitsOffsetX = initTraitsOffsetX;
        int traitsOffsetY = initTraitsOffsetY;
        graphicsContext.setFont(traitsFont);
        for (int lineIdx = 0; lineIdx < personalityList.size(); lineIdx++)
        {
            graphicsContext.setFill(font);
            graphicsContext.fillText(
                    personalityList.get(lineIdx),
                    Math.round(traitsOffsetX),
                    Math.round(traitsOffsetY + fontsize)
            );
            traitsOffsetY += fontsize + traitsYGap;
        }


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
        Point2D overlayPosition = SCREEN_POSITION;
        Rectangle2D posRelativeToWorldview = new Rectangle2D(overlayPosition.getX(), overlayPosition.getY(), WIDTH, HEIGHT);

        //Calculate Mouse Position relative to Overlay
        if (posRelativeToWorldview.contains(mousePosition))
            mousePosRelativeToDiscussionOverlay = new Point2D(mousePosition.getX() - overlayPosition.getX(), mousePosition.getY() - overlayPosition.getY());
        else mousePosRelativeToDiscussionOverlay = null;

        Integer hoveredElement = null;
        //Check if hovered over Rhetoric Button
        if (rhetoric_Button.contains(mousePosRelativeToDiscussionOverlay))
            hoveredElement = interfaceElements_list.indexOf(BACK_BUTTON_ID);

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

        if(interfaceElements_list.get(highlightedElement).equals(BACK_BUTTON_ID))
        {
            //WorldView.setIsPersonalityScreenActive(false);
            //WorldView.setIsTextBoxActive(true);
            WorldViewController.setWorldViewStatus(WorldViewStatus.TEXTBOX);
        }

        updateVisiblePersonality();
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

}
