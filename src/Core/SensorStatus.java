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

    public static String getCLASSNAME()
    {
        return CLASSNAME;
    }

    public String getStatusName()
    {
        return statusName;
    }

    public TriggerType getOnInteraction_TriggerSprite()
    {
        return onInteraction_TriggerSprite;
    }

    public TriggerType getOnInRange_TriggerSprite()
    {
        return onInRange_TriggerSprite;
    }

    public TriggerType getOnUpdate_TriggerSprite()
    {
        return onUpdate_TriggerSprite;
    }

    public TriggerType getOnIntersection_TriggerSprite()
    {
        return onIntersection_TriggerSprite;
    }

    public TriggerType getOnMonitorSignal_TriggerSprite()
    {
        return onMonitorSignal_TriggerSprite;
    }

    public TriggerType getOnTextBoxSignal_SpriteTrigger()
    {
        return onTextBoxSignal_SpriteTrigger;
    }

    public String getOnInteractionToStatusSprite()
    {
        return onInteractionToStatusSprite;
    }

    public String getOnUpdateToStatusSprite()
    {
        return onUpdateToStatusSprite;
    }

    public String getOnInRangeToStatusSprite()
    {
        return onInRangeToStatusSprite;
    }

    public String getOnIntersectionToStatusSprite()
    {
        return onIntersectionToStatusSprite;
    }

    public TriggerType getOnInteraction_TriggerSensor()
    {
        return onInteraction_TriggerSensor;
    }

    public TriggerType getOnInRange_TriggerSensor()
    {
        return onInRange_TriggerSensor;
    }

    public TriggerType getOnUpdate_TriggerSensor()
    {
        return onUpdate_TriggerSensor;
    }

    public TriggerType getOnIntersection_TriggerSensor()
    {
        return onIntersection_TriggerSensor;
    }

    public TriggerType getOnMonitor_TriggerSensor()
    {
        return onMonitor_TriggerSensor;
    }

    public TriggerType getOnTextBox_TriggerSensor()
    {
        return onTextBox_TriggerSensor;
    }

    public String getOnInteraction_StatusSensor()
    {
        return onInteraction_StatusSensor;
    }

    public String getOnUpdate_StatusSensor()
    {
        return onUpdate_StatusSensor;
    }

    public String getOnInRangeToStatusSensorStatus()
    {
        return onInRangeToStatusSensorStatus;
    }

    public String getOnIntersection_StatusSensor()
    {
        return onIntersection_StatusSensor;
    }
}
