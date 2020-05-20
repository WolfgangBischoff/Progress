package Core.Menus;

import Core.Actor;
import Core.GameWindow;
import Core.Utilities;
import Core.WorldView;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.Config.*;

public class DiscussionController
{
    private static final String CLASSNAME = "DiscussionController-";
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private WritableImage writableImage;
    private Actor player;
    Point2D mousePosRelativeToDiscussionOverlay;
    private Integer highlightedElement;
    private List<String> interfaceElements_list = new ArrayList<>();
    private ArgumentsSequence argumentsSequence;
    static Map<String, Map<String, Integer>> argumentsTraitsMatrix; //Argument -> Values per Trait

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
    int initTraitsOffsetX = 700;
    int initTraitsOffsetY = 200;
    int traitsYGap = 15;
    Font traitsFont = new Font(25);


    public DiscussionController()
    {
        canvas = new Canvas(DISCUSSION_WIDTH, DISCUSSION_HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        player = WorldView.getPlayer().getActor();
        highlightedElement = 0;


        rhetoricOptions_list.add("personal daily life");
        rhetoricOptions_list.add("society and gossip");
        rhetoricOptions_list.add("obserervations");
        rhetoricOptions_list.add("station news");
        rhetoricOptions_list.add("scientific study");
        rhetoricOptions_list.add("feelings of the people");
        rhetoricOptions_list.add("new trends");
        rhetoricOptions_list.add("good old earth");
        rhetoricOptions_list.add("coerce");

        personalityList.add("Conscientiousness");
        personalityList.add("Openness");
        personalityList.add("Agreeablness");
        personalityList.add("Neuroticism");
        personalityList.add("Extroversion");

        argumentsSequence = new ArgumentsSequence();
        if(argumentsTraitsMatrix == null)
            readBigFiveMatrix();
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

        //Background
        graphicsContext.setGlobalAlpha(0.8);
        graphicsContext.setFill(background);
        int backgroundOffsetX = 16, backgroundOffsetY = 10;
        graphicsContext.fillRect(backgroundOffsetX, backgroundOffsetY, INVENTORY_WIDTH - backgroundOffsetX * 2, INVENTORY_HEIGHT - backgroundOffsetY * 2);
        graphicsContext.setGlobalAlpha(1);

        //Rhetoric button
        graphicsContext.setFill(marking);
        interfaceElements_list.add("rhetoric");
        if (highlightedElement == interfaceElements_list.indexOf("rhetoric"))
            graphicsContext.fillRect(rhetoric_Button.getMinX(), rhetoric_Button.getMinY(), rhetoric_Button.getWidth(), rhetoric_Button.getHeight());
        graphicsContext.setFill(font);
        graphicsContext.fillText("Rhetoric", rhetoric_Button.getMinX(), rhetoric_Button.getMinY() + graphicsContext.getFont().getSize());

        //Rhetoric Options
        int optionsOffsetX = initOptionsOffsetX;
        int optionsOffsetY = initOptionsOffsetY;
        graphicsContext.setFont(optionsFont);
        double fontsize = graphicsContext.getFont().getSize();
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
                    Math.round(optionsOffsetY + fontsize)
            );
            optionsOffsetY += fontsize + optionsYGap;
        }

        //Other Person Known Traits
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
        Point2D discussionOverlayPosition = WorldView.getDiscussionOverlayPosition();
        Rectangle2D discussionPosRelativeToWorldview = new Rectangle2D(discussionOverlayPosition.getX(), discussionOverlayPosition.getY(), DISCUSSION_WIDTH, DISCUSSION_HEIGHT);

        //Calculate Mouse Position relative to Discussion
        if (discussionPosRelativeToWorldview.contains(mousePosition))
            mousePosRelativeToDiscussionOverlay = new Point2D(mousePosition.getX() - discussionOverlayPosition.getX(), mousePosition.getY() - discussionOverlayPosition.getY());
        else mousePosRelativeToDiscussionOverlay = null;

        Integer hoveredElement = null;
        //Check if hovered over Rhetoric Button
        if (rhetoric_Button.contains(mousePosRelativeToDiscussionOverlay))
            hoveredElement = interfaceElements_list.indexOf("rhetoric");

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

        //Translate interface element highlight to argument name
        Integer argumentIdx = null;
        if(Utilities.tryParseInt(interfaceElements_list.get(highlightedElement)))//is list item
            //argumentIdx = highlightedElement;
            argumentIdx = Integer.parseInt(interfaceElements_list.get(highlightedElement));
        if(argumentIdx != null)
        {
            String argument = rhetoricOptions_list.get(argumentIdx);
            argumentsSequence.addArgument(argument);
        }

        //System.out.println(CLASSNAME + methodName + interfaceElements_list.get(highlightedElement));

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

    private static void readBigFiveMatrix()
    {
        argumentsTraitsMatrix = new HashMap<>();
        List<String[]> textfile = Utilities.readAllLineFromTxt("src/res/argumentBigfiveMatrix.csv");
        for (int lineIdx = 0; lineIdx < textfile.size(); lineIdx++)
        {
            String[] line = textfile.get(lineIdx);
            String argument = null;
            for (int elementIdx = 0; elementIdx < line.length; elementIdx++)
            {
                //System.out.print(line[elementIdx] + "\t\t\t\t\t\t\t\t\t");

                //First line
                if (lineIdx == 0 && elementIdx > 0)
                    argumentsTraitsMatrix.put(line[elementIdx], new HashMap<String, Integer>());

                    //Content lines
                else if (lineIdx > 0)
                {
                    //Argument name
                    if (elementIdx == 0)
                        argument = line[0];
                    else
                    {
                        String belongsToTrait = textfile.get(0)[elementIdx];
                        Integer value = Integer.parseInt(line[elementIdx]);
                        Map<String, Integer> traitMap = argumentsTraitsMatrix.get(belongsToTrait);
                        traitMap.put(argument, value);
                    }
                }
            }
            //System.out.println();
        }

        for(Map.Entry<String, Map<String, Integer>> trait : argumentsTraitsMatrix.entrySet())
        {
            String traitName = trait.getKey();
            Map<String, Integer> values = trait.getValue();
            //System.out.println(CLASSNAME + traitName + values.toString());
        }

    }
}
