package Core.Menus;

import Core.Actor;
import Core.GameWindow;
import Core.Utilities;
import Core.WorldView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.Config.*;

public class PersonalityScreenController
{
    /*
With increasing cooperation value you find trais of the person, some traits are difficult to get, or just achievable by external world. Add Topics
  The "Traits" menu show special actions like "Join party", Sabatoage, Give info, open way
  Boost cooperation value with presents or actions
   */


    private static final String CLASSNAME = "PersonalityScreenController-";
    private static final String BACK_BUTTON_ID = "back";
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private WritableImage writableImage;
    Point2D mousePosRelativeToDiscussionOverlay;
    private Integer highlightedElement;
    private List<String> interfaceElements_list = new ArrayList<>();
    //private PersonalityChange argumentsSequence;
    static Map<String, Map<String, Integer>> argumentsTraitsMatrix; //Argument -> Values per Trait
    private Actor otherPersonActor;
    private PersonalityContainer personalityContainer;
    Actor player;

    //Rhetoric Button
    int rhetoric_x = 50;
    int rhetoric_y = 100;
    int rhetoric_width = 150;
    int rhetoric_height = 60;
    Rectangle2D rhetoric_Button = new Rectangle2D(rhetoric_x, rhetoric_y, rhetoric_width, rhetoric_height);

    //Rhetoric Options List
    private List<String> rhetoricOptions_list = new ArrayList<>();
    int initOptionsOffsetX = 50;
    int initOptionsOffsetY = 200;
    int optionsYGap = 15;
    Font optionsFont = new Font(25);

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
        //argumentsSequence = new PersonalityChange(personalityContainer.myersBriggsPersonality);
    }

    private void updateVisiblePersonality()
    {
        personalityList.clear();
        if (personalityContainer.getNumberOfInteractions() >= THRESHOLD_PERSONALITY)
            personalityList.add(personalityContainer.myersBriggsPersonality.toString());
        else
            personalityList.add("Unknown");
        if (personalityContainer.getNumberOfInteractions() >= THRESHOLD_MOTIVATION)
            personalityList.add(personalityContainer.getMotivation());
        else
            personalityList.add("Unknown");
        if (personalityContainer.getNumberOfInteractions() >= THRESHOLD_DECISION)
            personalityList.add(personalityContainer.getDecision());
        else
            personalityList.add("Unknown");
        if (personalityContainer.getNumberOfInteractions() >= THRESHOLD_FOCUS)
            personalityList.add(personalityContainer.getFocus());
        else
            personalityList.add("Unknown");
        if (personalityContainer.getNumberOfInteractions() >= THRESHOLD_LIFESTYLE)
            personalityList.add(personalityContainer.getLifestyle());
        else
            personalityList.add("Unknown");
    }

    private void draw() throws NullPointerException
    {
        String methodName = "draw() ";
        player = WorldView.getPlayer().getActor(); //Just needed as long the player resets with stage load (so we have always new Player)
        graphicsContext.clearRect(0, 0, DISCUSSION_WIDTH, DISCUSSION_HEIGHT);
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
        graphicsContext.fillRect(backgroundOffsetX, backgroundOffsetY, INVENTORY_WIDTH - backgroundOffsetX * 2, INVENTORY_HEIGHT - backgroundOffsetY * 2);
        graphicsContext.setGlobalAlpha(1);

        //Rhetoric button
        graphicsContext.setFill(marking);
        interfaceElements_list.add(BACK_BUTTON_ID);
        if (highlightedElement == interfaceElements_list.indexOf(BACK_BUTTON_ID))
            graphicsContext.fillRect(rhetoric_Button.getMinX(), rhetoric_Button.getMinY(), rhetoric_Button.getWidth(), rhetoric_Button.getHeight());
        graphicsContext.setFill(font);
        graphicsContext.fillText("Back", rhetoric_Button.getMinX(), rhetoric_Button.getMinY());


        /*
        //Rhetoric Options
        int optionsOffsetX = initOptionsOffsetX;
        int optionsOffsetY = initOptionsOffsetY;
        graphicsContext.setFont(optionsFont);

        for (int lineIdx = 0; lineIdx < rhetoricOptions_list.size(); lineIdx++)
        {
            graphicsContext.setFill(marking);
            interfaceElements_list.add("" + lineIdx);
            if (highlightedElement == interfaceElements_list.indexOf("" + lineIdx))
            {
                graphicsContext.fillRect(optionsOffsetX - 10, optionsOffsetY, 300 + 10, fontsize + 10);
            }
            graphicsContext.setFill(font);
            graphicsContext.fillText(
                    rhetoricOptions_list.get(lineIdx),
                    Math.round(optionsOffsetX),
                    Math.round(optionsOffsetY)
            );
            optionsOffsetY += fontsize + optionsYGap;
        }

         */

        //Other Person Known Traits
        double fontsize = graphicsContext.getFont().getSize();
        int traitsOffsetX = initTraitsOffsetX;
        int traitsOffsetY = initTraitsOffsetY;
        graphicsContext.setFont(traitsFont);
        fontsize = graphicsContext.getFont().getSize();
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
        Point2D discussionOverlayPosition = WorldView.getPersonalityScreenPosition();
        Rectangle2D discussionPosRelativeToWorldview = new Rectangle2D(discussionOverlayPosition.getX(), discussionOverlayPosition.getY(), DISCUSSION_WIDTH, DISCUSSION_HEIGHT);

        //Calculate Mouse Position relative to Discussion
        if (discussionPosRelativeToWorldview.contains(mousePosition))
            mousePosRelativeToDiscussionOverlay = new Point2D(mousePosition.getX() - discussionOverlayPosition.getX(), mousePosition.getY() - discussionOverlayPosition.getY());
        else mousePosRelativeToDiscussionOverlay = null;

        Integer hoveredElement = null;
        //Check if hovered over Rhetoric Button
        if (rhetoric_Button.contains(mousePosRelativeToDiscussionOverlay))
            hoveredElement = interfaceElements_list.indexOf(BACK_BUTTON_ID);

        //Check if hovered Rhetoric Options
        graphicsContext.setFont(optionsFont);
        int optionsOffsetX = initOptionsOffsetX;
        int optionsOffsetY = initOptionsOffsetY;
        double fontSize = graphicsContext.getFont().getSize();
        for (int lineIdx = 0; lineIdx < rhetoricOptions_list.size(); lineIdx++)
        {
            Rectangle2D optionArea = new Rectangle2D(optionsOffsetX - 10, optionsOffsetY, 300 + 10, fontSize + 10);
            if (optionArea.contains(mousePosRelativeToDiscussionOverlay))
            {
                hoveredElement = interfaceElements_list.indexOf("" + lineIdx);
            }
            optionsOffsetY += fontSize + optionsYGap;
        }

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
            WorldView.setIsPersonalityScreenActive(false);
        }

        /*
        //Translate interface element highlight to argument name
        Integer argumentIdx = null;
        if (Utilities.tryParseInt(interfaceElements_list.get(highlightedElement)))//is list item
            argumentIdx = Integer.parseInt(interfaceElements_list.get(highlightedElement));
        if (argumentIdx != null)
        {
            String argument = rhetoricOptions_list.get(argumentIdx);
            argumentsSequence.addArgument(argument);
            //personalityContainer.numberOfInteractions++;
        }

         */

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

    public static int getMenuWidth()
    {
        return INVENTORY_WIDTH;
    }

    public static int getMenuHeight()
    {
        return INVENTORY_HEIGHT;
    }

}
