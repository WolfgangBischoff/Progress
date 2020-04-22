package Core;

import Core.Enums.TriggerType;


public class SensorStatus
{
    private static final String CLASSNAME = "SensorStatus";
    String statusName;
    //For Sprite Changes
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
    //For Sensor status changes
    TriggerType onInteraction_TriggerSensor = TriggerType.NOTHING;
    TriggerType onInRange_TriggerSensor = TriggerType.NOTHING;
    TriggerType onUpdate_TriggerSensor = TriggerType.NOTHING;
    TriggerType onIntersection_TriggerSensor = TriggerType.NOTHING;
    TriggerType onMonitor_TriggerSensor = null;
    TriggerType onTextBox_TriggerSensor = TriggerType.NOTHING;
    String onInteraction_StatusSensor = Config.KEYWORD_transition;
    String onUpdate_StatusSensor = Config.KEYWORD_transition;
    String onInRangeToStatusSensorStatus = Config.KEYWORD_transition;
    String onIntersection_StatusSensor = Config.KEYWORD_transition;

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
