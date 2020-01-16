package Core;

public class SpriteData
{
    final static int tileCodeIdx = 0;
    final static int nameIdx = 1;
    final static int spriteNameIdx = 2;
    final static int blockingIdx = 3;
    final static int layerIdx = 4;
    final static int fpsIdx = 5;
    final static int totalFramesIdx = 6;
    final static int colsIdx = 7;
    final static int rowsIdx = 8;
    final static int frameWidthIdx = 9;
    final static int frameHeightIdx = 10;
    final static int velocityIdx = 11;
    final static int directionIdx = 12;
    final static int hitboxOffsetXIdx = 13;
    final static int hitboxOffsetYIdx = 14;
    final static int hitboxWidthIdx = 15;
    final static int hitboxHeightIdx = 16;
    final static int lightningSpriteNameIdx = 17;
    final static int animationDurationIdx = 18;


    public String name, spriteName, lightningSprite;
    public Boolean blocking;
    public Integer fps, totalFrames, cols, rows, frameWidth, frameHeight, velocity, direction, priority, hitboxOffsetX, hitboxOffsetY, hitboxWidth, hitboxHeight, animationDuration;
    public Status actorStatus = null;

    public void setActorStatus(Status actorStatus)
    {
        this.actorStatus = actorStatus;
    }

    public SpriteData(String name, String spriteName, Boolean blocking, Integer fps, Integer totalFrames, Integer cols, Integer rows, Integer frameWidth, Integer frameHeight, Integer velocity, Integer direction, Integer priority, Integer hitboxOffsetX, Integer hitboxOffsetY, Integer hitboxWidth, Integer hitboxHeight, String lightningSprite)
    {
        this.name = name;
        this.spriteName = spriteName;
        this.blocking = blocking;
        this.fps = fps;
        this.totalFrames = totalFrames;
        this.cols = cols;
        this.rows = rows;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.velocity = velocity;
        this.direction = direction;
        this.priority = priority;
        this.hitboxOffsetX = hitboxOffsetX;
        this.hitboxOffsetY = hitboxOffsetY;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
        this.lightningSprite = lightningSprite;
    }

    public static SpriteData tileDefinition(String[] lineData)
    {
        //System.out.println("TileDate/tileDefinition: " + lineData[0]);
        Boolean blocking = Boolean.parseBoolean(lineData[blockingIdx]);
        Integer priority = Integer.parseInt(lineData[layerIdx]);
        Integer fps = Integer.parseInt(lineData[fpsIdx]);
        Integer totalFrames = Integer.parseInt(lineData[totalFramesIdx]);
        Integer cols = Integer.parseInt(lineData[colsIdx]);
        Integer rows = Integer.parseInt(lineData[rowsIdx]);
        Integer frameWidth = Integer.parseInt(lineData[frameWidthIdx]);
        Integer frameHeight = Integer.parseInt(lineData[frameHeightIdx]);
        Integer velocity = Integer.parseInt(lineData[velocityIdx]);
        Integer direction = Integer.parseInt(lineData[directionIdx]);
        Integer hitboxOffsetX = Integer.parseInt(lineData[hitboxOffsetXIdx]);
        Integer hitboxOffsetY = Integer.parseInt(lineData[hitboxOffsetYIdx]);
        Integer hitboxWidth = Integer.parseInt(lineData[hitboxWidthIdx]);
        Integer hitboxHeight = Integer.parseInt(lineData[hitboxHeightIdx]);
        String lightningSprite = lineData[lightningSpriteNameIdx];

        SpriteData current = new SpriteData(lineData[nameIdx], lineData[spriteNameIdx], blocking, fps, totalFrames, cols, rows, frameWidth, frameHeight, velocity, direction, priority, hitboxOffsetX, hitboxOffsetY, hitboxWidth, hitboxHeight, lightningSprite);
        return current;
        //tileDataMap.put(lineData[tileCodeIdx], current);
    }

    @Override
    public String toString()
    {
        return "SpriteData{" +
                "name='" + name + '\'' +
                ", priority=" + priority +
                '}';
    }
}
