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
    final static int hitboxOffsetXIdx = 11;
    final static int hitboxOffsetYIdx = 12;
    final static int hitboxWidthIdx = 13;
    final static int hitboxHeightIdx = 14;
    final static int lightningSpriteNameIdx = 15;
    final static int animationDurationIdx = 16;
    final static int velocityIdx = 17;
    final static int dialogueIDIdx = 18;
    final static int animationEndsIdx = 19;



    public String name, spriteName, lightningSprite, dialogueID;
    public Boolean blocking, animationEnds = false;
    public Integer totalFrames, cols, rows, frameWidth, frameHeight, heightLayer, hitboxOffsetX, hitboxOffsetY, hitboxWidth, hitboxHeight, velocity;//, animationDuration;
    public Double animationDuration, fps;


    public SpriteData(String name, String spriteName, Boolean blocking, Double fps, Integer totalFrames, Integer cols, Integer rows, Integer frameWidth, Integer frameHeight, Integer heightLayer, Integer hitboxOffsetX, Integer hitboxOffsetY, Integer hitboxWidth, Integer hitboxHeight, String lightningSprite)
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
        this.heightLayer = heightLayer;
        this.hitboxOffsetX = hitboxOffsetX;
        this.hitboxOffsetY = hitboxOffsetY;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
        this.lightningSprite = lightningSprite;
    }

    public static SpriteData tileDefinition(String[] lineData) throws IndexOutOfBoundsException
    {
        try
        {
            Boolean blocking = Boolean.parseBoolean(lineData[blockingIdx]);
            Integer priority = Integer.parseInt(lineData[layerIdx]);
            Double fps = Double.parseDouble(lineData[fpsIdx]);
            Integer totalFrames = Integer.parseInt(lineData[totalFramesIdx]);
            Integer cols = Integer.parseInt(lineData[colsIdx]);
            Integer rows = Integer.parseInt(lineData[rowsIdx]);
            Integer frameWidth = Integer.parseInt(lineData[frameWidthIdx]);
            Integer frameHeight = Integer.parseInt(lineData[frameHeightIdx]);
            Integer hitboxOffsetX = Integer.parseInt(lineData[hitboxOffsetXIdx]);
            Integer hitboxOffsetY = Integer.parseInt(lineData[hitboxOffsetYIdx]);
            Integer hitboxWidth = Integer.parseInt(lineData[hitboxWidthIdx]);
            Integer hitboxHeight = Integer.parseInt(lineData[hitboxHeightIdx]);
            String lightningSprite = lineData[lightningSpriteNameIdx];

            return new SpriteData(lineData[nameIdx], lineData[spriteNameIdx], blocking, fps, totalFrames, cols, rows, frameWidth, frameHeight, priority, hitboxOffsetX, hitboxOffsetY, hitboxWidth, hitboxHeight, lightningSprite);
        }
        catch (IndexOutOfBoundsException e)
        {
            StringBuilder stringBuilder = new StringBuilder();
            for(String s : lineData)
            {
                stringBuilder.append(s);
                stringBuilder.append(", ");
            }
            throw new IndexOutOfBoundsException("\nTile Definition failed with line: " + stringBuilder.toString());
        }

    }

    @Override
    public String toString()
    {
        return "SpriteData{" +
                ", spriteName='" + spriteName + '\'' +
                ", fps=" + fps +
                ", totalFrames=" + totalFrames +
                ", animationDuration=" + animationDuration +
                '}';
    }
}
