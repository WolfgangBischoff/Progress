package Core;

import java.util.ArrayList;
import java.util.List;

public class Dialogue
{
    private static final String CLASSNAME = "Dialogue/";
    String type;
    String nextDialogue;
    private String spriteStatus;
    private String sensorStatus;
    List<String> messages = new ArrayList<>();
    List<Option> options = new ArrayList<>();

    public void addOption(String optionMessage, String nextDialogue)
    {
        options.add(new Option(optionMessage, nextDialogue));
    }

    public List<String> getOptionMessages()
    {
        List<String> optionMessages = new ArrayList<>();
        for (Option option : options)
        {
            optionMessages.add(option.optionMessage);
        }
        return optionMessages;
    }

    class Option
    {
        Option(String optionMessage, String nextDialogue)
        {
            this.optionMessage = optionMessage;
            this.nextDialogue = nextDialogue;
        }

        String nextDialogue;
        String optionMessage;
    }

    public String getSpriteStatus()
    {
        return spriteStatus;
    }

    public String getSensorStatus()
    {
        return sensorStatus;
    }

    public void setSpriteStatus(String spriteStatus)
    {
        //if (spriteStatus.trim().equals(""))
        if (spriteStatus.trim().isEmpty())
            this.spriteStatus = null;
        else
            this.spriteStatus = spriteStatus;
    }

    public void setSensorStatus(String sensorStatus)
    {
        String methodName = "setSensorStatus() ";
        if (sensorStatus.trim().isEmpty())
            this.sensorStatus = null;
        else
            this.sensorStatus = sensorStatus;
    }
}
