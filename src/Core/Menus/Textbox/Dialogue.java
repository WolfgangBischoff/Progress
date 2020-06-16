package Core.Menus.Textbox;

import java.util.ArrayList;
import java.util.List;

public class Dialogue
{
    private static final String CLASSNAME = "Dialogue-";
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

    public Option getOption(String optionMsg)
    {
        for (Option option : options)
            if (option.optionMessage.equals(optionMsg))
                return option;
        return null;
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

        @Override
        public String toString()
        {
            return "Option{" +
                    "nextDialogue='" + nextDialogue + '\'' +
                    ", optionMessage='" + optionMessage + '\'' +
                    '}';
        }
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

    @Override
    public String toString()
    {
        return "Dialogue{" +
                "type='" + type + '\'' +
                ", nextDialogue='" + nextDialogue + '\'' +
                ", spriteStatus='" + spriteStatus + '\'' +
                ", sensorStatus='" + sensorStatus + '\'' +
                ", messages=" + messages +
                ", options=" + options +
                '}';
    }
}
