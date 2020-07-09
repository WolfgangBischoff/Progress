package Core;

import Core.Enums.TriggerType;


public class SensorStatus
{
    private static final String CLASSNAME = "SensorStatus";
    String statusName;
    //For Sprite Changes
    TriggerType onInteraction_TriggerSprite = TriggerType.NOTHING;
    TriggerType onInRange_TriggerSprite = TriggerType.NOTHING;
    TriggerType onUpdate_TriggerSprite = TriggerType.NOTHING;
    TriggerType onIntersection_TriggerSprite = TriggerType.NOTHING;
    TriggerType onMonitorSignal_TriggerSprite = null;
    TriggerType onTextBoxSignal_SpriteTrigger = TriggerType.NOTHING;
    String onInteractionToStatusSprite = Config.KEYWORD_transition;
    String onUpdateToStatusSprite = Config.KEYWORD_transition;
    String onInRangeToStatusSprite = Config.KEYWORD_transition;
    String onIntersectionToStatusSprite = Config.KEYWORD_transition;
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
                + "\n\tonInteraction: " + onInteraction_TriggerSprite + " " + onInteractionToStatusSprite
                + "\n\tonInRange: " + onInRange_TriggerSprite + " " + onInRangeToStatusSprite
                + "\n\tonIntersection: " + onIntersection_TriggerSprite + " " + onIntersectionToStatusSprite
                + "\n\tonUpdate: " + onUpdate_TriggerSprite + " " + onUpdateToStatusSprite
                + "\n\tonTextBoxSignal: " + onTextBoxSignal_SpriteTrigger
                + "\n\tonMonitorSignal: " + onMonitorSignal_TriggerSprite
                ;
    }
}
