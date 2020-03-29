package Core;

import java.util.ArrayList;
import java.util.List;

public class Dialogue
{
    String classname = "Dialogue";
    String type;
    String nextDialogue;
    private String actorStatus;
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

    public String getActorStatus()
    {
        return actorStatus;
    }

    public void setActorStatus(String actorStatus)
    {
        if (actorStatus.trim().equals(""))
            this.actorStatus = null;
        else
            this.actorStatus = actorStatus;
    }
}
