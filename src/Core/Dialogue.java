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

    public void addOption(String optionMessage)
    {
        options.add(new Option(optionMessage));
    }

    public List<String> getOptionMessages()
    {
        List<String> optionMessages = new ArrayList<>();
        for(Option option : options)
        {
            System.out.println(classname + option.optionMessage);
            optionMessages.add(option.optionMessage);
        }
        return optionMessages;
    }

    class Option
    {
        Option(String optionMessage)
        {
            this.optionMessage = optionMessage;
        }
        String nextDialogue;
        String optionMessage;
    }
}
