package Core.Menus;

import Core.Actor;
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
import java.util.List;

import static Core.Config.*;

public class Discussion
{
    private static final String CLASSNAME = "Discussion-";
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private WritableImage writableImage;
    private Actor player;

    private List<String> rhetoricSkills_list = new ArrayList<>();

    int rhetoric_x = 50;
    int rhetoric_y = 100;
    int rhetoric_width = 150;
    int rhetoric_height = 60;
    Rectangle2D rhetoric_Button = new Rectangle2D(rhetoric_x, rhetoric_y, rhetoric_width, rhetoric_height);

    public Discussion()
    {
        canvas = new Canvas(DISCUSSION_WIDTH, DISCUSSION_HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        player = WorldView.getPlayer().getActor();
        rhetoricSkills_list.add("admire"); //bewundern if ego high or naiv
        rhetoricSkills_list.add("boast"); //prahlen if ego low
        rhetoricSkills_list.add("underline well mean");//naiv
        rhetoricSkills_list.add("coerce"); //nötigen if ego weak
        rhetoricSkills_list.add("ask for help"); //if helpfull
        rhetoricSkills_list.add("joke"); //witzig
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

        //Background
        graphicsContext.setGlobalAlpha(0.8);
        graphicsContext.setFill(background);
        int backgroundOffsetX = 16, backgroundOffsetY = 10;
        graphicsContext.fillRect(backgroundOffsetX, backgroundOffsetY, INVENTORY_WIDTH - backgroundOffsetX * 2, INVENTORY_HEIGHT - backgroundOffsetY * 2);

        graphicsContext.setGlobalAlpha(1);

        //Rhetoric button
        graphicsContext.setFill(marking);
        graphicsContext.fillRect(rhetoric_Button.getMinX(), rhetoric_Button.getMinY(), rhetoric_Button.getWidth(), rhetoric_Button.getHeight());
        graphicsContext.setFill(font);
        graphicsContext.fillText("Rhetoric", rhetoric_Button.getMinX(), rhetoric_Button.getMinY() + graphicsContext.getFont().getSize());

        //Rethoric Options
        int xOffsetTextLine = 50;
        int yOffsetTextLine = 200;
        int yGap = 15;
        Font retfong = new Font(25);
        graphicsContext.setFont(retfong);
        double fontsize = graphicsContext.getFont().getSize();
        for (int lineIdx = 0; lineIdx < rhetoricSkills_list.size(); lineIdx++)
        {
            graphicsContext.setFill(marking);
            graphicsContext.fillRect(xOffsetTextLine -10, yOffsetTextLine, 300 +10, fontsize + 10);
            graphicsContext.setFill(font);
            graphicsContext.fillText(
                    rhetoricSkills_list.get(lineIdx),
                    Math.round(xOffsetTextLine),
                    Math.round(yOffsetTextLine + fontsize)
            );
            yOffsetTextLine += fontsize + yGap;
        }

        SnapshotParameters transparency = new SnapshotParameters();
        transparency.setFill(Color.TRANSPARENT);
        writableImage = canvas.snapshot(transparency, null);

    }

    public void processMouse(Point2D mousePosition, boolean isMouseClicked)
    {
        String methodName = "processMouse(Point2D, boolean) ";
        Point2D discussionOverlayPosition = WorldView.getDiscussionOverlayPosition();
        Rectangle2D discussionPosRelativeToWorldview = new Rectangle2D(discussionOverlayPosition.getX(), discussionOverlayPosition.getY(), DISCUSSION_WIDTH, DISCUSSION_HEIGHT);
        Rectangle2D rhetoric_buttonRelativeToWorldView = new Rectangle2D(discussionOverlayPosition.getX() + rhetoric_x, discussionOverlayPosition.getY() + rhetoric_y, rhetoric_width, rhetoric_height);
        if (discussionPosRelativeToWorldview.contains(mousePosition))
        {
            //System.out.println(CLASSNAME + methodName + "Mouse: " + mousePosition.getX() + " " + mousePosition.getY());
        }

        //Check if hovered over Button
        if (rhetoric_buttonRelativeToWorldView.contains(mousePosition))
        {
            System.out.println(CLASSNAME + methodName + "Rethorics Button hovered");
            if (isMouseClicked)
            {
                System.out.println(CLASSNAME + methodName + "Rethorics Button clicked");
            }
        }


        /*
        int offsetYTmp = firstLineOffsetY;
        if (readDialogue.type.equals(DECISION_KEYWORD) && GameWindow.getSingleton().mouseMoved)
        {
            for (int checkedLineIdx = 0; checkedLineIdx < lineSplitMessage.size(); checkedLineIdx++)
            {
                Rectangle2D positionOptionRelativeToWorldView = new Rectangle2D(discussionOverlayPosition.getX(), discussionOverlayPosition.getY() + offsetYTmp, TEXT_BOX_WIDTH, textboxGc.getFont().getSize());
                offsetYTmp += textboxGc.getFont().getSize();
                //Hovers over Option
                if (positionOptionRelativeToWorldView.contains(mousePosition))
                {
                    if (markedOption != checkedLineIdx)
                    {
                        markedOption = checkedLineIdx;
                        drawTextbox();
                    }
                    break;
                }
            }

            GameWindow.getSingleton().mouseMoved = false;
        }
 */
        if (isMouseClicked)
        {
            //nextMessage(GameWindow.getSingleton().getRenderTime());
        }
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
