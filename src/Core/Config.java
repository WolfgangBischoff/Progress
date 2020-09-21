package Core;

import javafx.geometry.Point2D;

public class Config
{
    //General
    public static final Boolean DEBUG_ACTORS = false;
    public static final Boolean DEBUG_BLOCKER = false;
    public static final Boolean DEBUG_NO_WALL = false;
    public static final Boolean DEBUG_MOUSE_ANALYSIS = true;
    public static final String FIRST_LEVEL = "transporter";
    public static final double GAME_WINDOW_WIDTH = 1440;
    public static final double GAME_WINDOW_HEIGHT = 900;
    public static final int CAMERA_WIDTH = 1200;
    public static final int CAMERA_HEIGHT = 800;
    public static final int TEXTBOX_WIDTH = 800;
    public static final int TEXTBOX_HEIGHT = 400;

    public static final int PERSONALITY_WIDTH = 900;
    public static final int PERSONALITY_HEIGHT = 600;
    public static final Point2D PERSONALITY_POSITION = new Point2D(CAMERA_WIDTH / 2f - PERSONALITY_WIDTH / 2.0, CAMERA_HEIGHT / 2.0 - PERSONALITY_HEIGHT / 2.0); //Centered

    public static final int INVENTORY_WIDTH = 550;
    public static final int INVENTORY_HEIGHT = 600;
    public static Point2D INVENTORY_POSITION = new Point2D(64, CAMERA_HEIGHT / 2.0 - INVENTORY_HEIGHT / 2.0);
    public static Point2D EXCHANGE_INVENTORY_POSITION = new Point2D(64 + INVENTORY_WIDTH, CAMERA_HEIGHT / 2.0 - INVENTORY_HEIGHT / 2.0);

    public static final int DISCUSSION_WIDTH = 900;
    public static final int DISCUSSION_HEIGHT = 600;
    public static Point2D DISCUSSION_POSITION = new Point2D(CAMERA_WIDTH / 2f - DISCUSSION_WIDTH / 2.0, CAMERA_HEIGHT / 2.0 - DISCUSSION_HEIGHT / 2.0);

    public static final int DAY_SUMMARY_WIDTH = 900;
    public static final int DAY_SUMMARY_HEIGHT = 600;
    public static Point2D DAY_SUMMARY_POSITION = new Point2D(CAMERA_WIDTH / 2f - DAY_SUMMARY_WIDTH / 2.0, CAMERA_HEIGHT / 2.0 - DAY_SUMMARY_HEIGHT / 2.0); //Centereda

    public static final int MAM_BAR_WIDTH = 300;
    public static final int MAM_BAR_HEIGHT = 50;
    public static final Point2D MAM_BAR_POSITION = new Point2D(CAMERA_WIDTH - MAM_BAR_WIDTH - 50, 30); //Right Upper Edge
    public static final Point2D MONEY_POSITION = new Point2D(CAMERA_WIDTH - MAM_BAR_WIDTH - 50, 80);
    public static final Point2D HUNGER_BAR_POSITION = new Point2D(CAMERA_WIDTH - MAM_BAR_WIDTH - 50, 130);
    public static final Point2D BOARD_TIME_POSITION = new Point2D(20, 30);
    public static final int BOARD_TIME_WIDTH = 150;
    public static final int BOARD_TIME_HEIGHT = 70;

    //Keyboard
    public static final String KEYBOARD_INVENTORY = "TAB";
    public static final String KEYBOARD_INTERACT = "E";

    //Gameplay
    public static final float TIME_BETWEEN_INTERACTIONS = 0.5f;
    public static final float TIME_BETWEEN_DIALOGUE = 0.2f;
    public static final float RUMBLE_GRADE = 8;
    public static final float RUMBLE_GRADE_DECREASE = 0.1f;
    public static final float RUMBLE_MAX_DURATION = 1.3f;
    public static final int DAY_STARTTIME = 60 * 7;
    public static final int DAY_FORCED_ENDTIME = 60 * 24;
    public static final int MAX_HUNGER = 100;
    public static final int INIT_HUNGER = 50;
    public static final int INIT_HEALTH = 5;
    public static final int MAX_HEALTH = 5;
    public static final int INIT_MONEY = 5;

    //Map file keywords
    public static final String MAPDEFINITION_EMPTY = "______";
    public static final String MAPDEFINITION_NO_TILE = "__xx__";
    public static final String KEYWORD_NEW_LAYER = "layer:";
    public static final String KEYWORD_PASSIV_LAYER = "passivlayer:";
    public static final String KEYWORD_ACTORS = "actors:";
    public static final String KEYWORD_TILEDEF = "tiledefinition:";
    public static final String KEYWORD_WORLDSHADOW = "shadow:";
    public static final String KEYWORD_GROUPS = "actorgroups:";
    public static final String KEYWORD_SPAWNPOINTS = "spawnpoints:";
    public static final String KEYWORD_INCLUDE = "include:";
    public static final String KEYWORD_POSITION = "position:";

    public static final String INCLUDE_CONDITION_suspicion_lessequal = "suspicion_lessequal";
    public static final String INCLUDE_CONDITION_day_greaterequal = "day_greaterequal";

    //Actorfile Keywords
    public static final String KEYWORD_sensorStatus = "sensorStatus";
    public static final String KEYWORD_transition = "transition";
    public static final String KEYWORD_interactionArea = "interactionArea";
    public static final String KEYWORD_dialogueFile = "dialogueFile";
    public static final String KEYWORD_text_box_analysis_group = "textbox_analysis_group";
    public static final String KEYWORD_collectable_type = "collectible_data";//"collectable_type";
    public static final String CONTAINS_COLLECTIBLE_KEYWORD = "contains_collectible";
    public static final String KEYWORD_actor_tags = "tags";
    public static final String KEYWORD_condition = "condition";
    public static final String KEYWORD_personality = "personality";
    public static final String KEYWORD_suspicious_value = "suspicious_value";

    //DialogueFile Keywords
    public static final String DIALOGUE_TAG = "dialogue";
    public static final String ID_TAG = "id";
    public static final String TYPE_TAG = "type";
    public static final String ACTOR_STATUS_TAG = "spritestatus";
    public static final String SENSOR_STATUS_TAG = "sensorstatus";
    public static final String decision_TYPE_ATTRIBUTE = "decision";
    public static final String LINE_TAG = "line";
    public static final String NEXT_DIALOGUE_TAG = "nextDialogue";
    public static final String OPTION_TAG = "option";

    public static final String discussion_TYPE_ATTRIBUTE = "discussion";
    public static final String game_ATTRIBUTE = "game";
    public static final String success_ATTRIBUTE = "success";
    public static final String defeat_ATTRIBUTE = "defeat";
    public static final String levelchange_TYPE_ATTRIBUTE = "levelchange";
    public static final String level_ATTRIBUTE = "level";
    public static final String spawnID_ATTRIBUTE = "spawnID";
    public static final String dayChange_TYPE_ATTRIBUTE = "dayChange";
    public static final String TEXTBOX_ATTRIBUTE_GET_MONEY = "getMoney";
    public static final String TEXTBOX_ATTRIBUTE_AMOUNT = "amount";


    //Paths
    public static final String CSV_POSTFIX = ".csv";
    public static final String PNG_POSTFIX = ".png";
    public static final String DIALOGUE_FILE_PATH = "src/res/";
    public static final String STAGE_FILE_PATH = "src/res/level/";
    public static final String ACTOR_DIRECTORY_PATH = "src/res/actorData/";
    public static final String IMAGE_DIRECTORY_PATH = "/res/img/";

    //Discussion-Game
    public static final int THRESHOLD_MOTIVATION = 3;
    public static final int THRESHOLD_DECISION = 6;
    public static final int THRESHOLD_FOCUS = 9;
    public static final int THRESHOLD_LIFESTYLE = 12;
    public static final int THRESHOLD_PERSONALITY = 12;
    public static final String COIN_BEHAVIOR_MOVING = "moving";
    public static final String COIN_BEHAVIOR_JUMP = "jump";
    public static final String COIN_BEHAVIOR_SPIRAL = "spiral";
    public static final String COIN_BEHAVIOR_CIRCLE = "circle";
    public static final String COIN_TAG_ANGLE = "angle";
    public static final String COIN_TAG_INITSPEED = "initspeed";
    public static final int COIN_MAX_TIME = 15;
    public static final int DISCUSSION_THRESHOLD_WIN = 6;

    //Management-Attention-Meter MAM
    public static final int MAM_DAILY_DECREASE = 2;
    public static final int MAM_THRESHOLD_INTERROGATION = 6;

}
