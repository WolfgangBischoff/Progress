package Core;

import java.util.ArrayList;
import java.util.List;

public class Dialogue
{
    String classname = "Dialogue";
    String type;
    List<String> messages = new ArrayList<>();
    String nextDialogue;

    List<Option> options = new ArrayList<>();

    public void addOption(String optionMessage, String nextDialogue)
    {
        options.add(new Option(optionMessage, nextDialogue));
    }

    public List<String> getOptionMessages()
    {
        List<String> optionMessages = new ArrayList<>();
        for(Option option : options)
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
}
