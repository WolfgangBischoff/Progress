package Core;

import Core.Enums.TriggerType;

import java.util.HashSet;
import java.util.Set;

public class SensorStatus
{
    private static final String CLASSNAME = "SensorStatus";
    String statusName;

    static final Set<String> actorDefinitionKeywords = new HashSet<>();
    TriggerType onInteraction = TriggerType.NOTHING;
    TriggerType onInRange = TriggerType.NOTHING;
    TriggerType onUpdate = TriggerType.NOTHING;
    TriggerType onIntersection = TriggerType.NOTHING;
    TriggerType onMonitorSignal = null;
    TriggerType onTextBoxSignal = TriggerType.NOTHING;
    String onInteractionToStatus = Config.KEYWORD_transition;
    String onUpdateToStatus = Config.KEYWORD_transition;
    String onInRangeToStatus = Config.KEYWORD_transition;
    String onIntersectionToStatus = Config.KEYWORD_transition;

    public SensorStatus(String statusName)
    {
    this.statusName = statusName;
    }

    @Override
    public String toString()
    {
        return statusName
                + "\n\tonInteraction: " + onInteraction + " " + onInteractionToStatus
                + "\n\tonInRange: " + onInRange + " " + onInRangeToStatus
                + "\n\tonIntersection: " + onIntersection + " " + onIntersectionToStatus
                + "\n\tonUpdate: " + onUpdate + " " + onUpdateToStatus
                + "\n\tonTextBoxSignal: " + onTextBoxSignal
                + "\n\tonMonitorSignal: " + onMonitorSignal
                ;
    }
}
