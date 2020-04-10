package Core;

public class Config
{
    //General
    public static final Boolean DEBUG_BLOCKER = false;
    public static final Boolean DEBUG_ACTORS = false;
    public static final double GAME_WINDOW_WIDTH = 1440;
    public static final double GAME_WINDOW_HEIGHT = 900;
    public static final int CAMERA_WIDTH = 1200;
    public static final int CAMERA_HEIGHT = 800;

    //Gameplay
    public static final float TIME_BETWEEN_INTERACTIONS = 0.5f;
    public static final float TIME_BETWEEN_DIALOGUE = 0.2f;

    //Map file keywords
    public static final String MAPDEFINITION_EMPTY = "______";
    public static final String KEYWORD_NEW_LAYER = "layer:";
    public static final String KEYWORD_PASSIV_LAYER = "passivlayer:";
    public static final String KEYWORD_ACTORS = "actors:";
    public static final String KEYWORD_TILEDEF = "tiledefinition:";
    public static final String KEYWORD_WORLDSHADOW = "shadow:";
    public static final String KEYWORD_GROUPS = "actorgroups:";

    //Actorfile Keywords
    public static final String KEYWORD_onInteraction = "onInteraction";
    public static final String KEYWORD_onIntersection = "onIntersection";
    public static final String KEYWORD_onInRange = "onInRange";
    public static final String KEYWORD_onUpdate = "onUpdate";
    public static final String KEYWORD_onMonitor = "onMonitor";
    public static final String KEYWORD_onTextBox = "onTextBox";
    public static final String KEYWORD_transition = "transition";
    public static final String KEYWORD_interactionArea = "interactionArea";
    public static final String KEYWORD_dialogueFile = "dialogueFile";
    public static final String KEYWORD_text_box_analysis_group = "textbox_analysis_group";

    //DialogueFile Keywords
    public static final String DIALOGUE_TAG = "dialogue";
    public static final String ID_TAG = "id";
    public static final String TYPE_TAG = "type";
    public static final String ACTOR_STATUS_TAG = "actorstatus";
    public static final String DECISION_KEYWORD = "decision";
    public static final String LINE_TAG = "line";
    public static final String NEXT_DIALOGUE_TAG = "nextDialogue";

    //Paths
    public static final String CSV_POSTFIX = ".csv";
    public static final String DIALOGUE_FILE_PATH = "src/res/";
    public static final String STAGE_FILE_PATH = "src/res/level/";

}
