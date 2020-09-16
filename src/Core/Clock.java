package Core;

import Core.WorldView.WorldViewController;
import Core.WorldView.WorldViewStatus;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.text.DecimalFormat;

import static Core.Config.DAY_FORCED_ENDTIME;

public class Clock
{
    private static String CLASSNAME = "Clock ";
    IntegerProperty time = new SimpleIntegerProperty(Config.DAY_STARTTIME);

    Long lastTimeIncremented;

    public Clock(Long initRealTime)
    {
        this.lastTimeIncremented = initRealTime;
    }

    public void tryIncrementTime(Long currentNanoTime)
    {
        String methodName = "tryIncrementTime() ";
        double elapsedTimeSinceLastIncrement = (currentNanoTime - lastTimeIncremented) / 1000000000.0;
        if (elapsedTimeSinceLastIncrement > 1 && WorldViewController.getWorldViewStatus() == WorldViewStatus.WORLD)
        {
            time.set(time.getValue() + 1);
            lastTimeIncremented = currentNanoTime;
            //System.out.println(CLASSNAME + methodName +  time + " " + getFormattedTime());
        }

        if(time.getValue() > DAY_FORCED_ENDTIME)
        {
            System.out.println(CLASSNAME + methodName + "Player falls asleep");
            time.set(DAY_FORCED_ENDTIME);
        }
    }

    public void reset()
    {
        time.setValue(Config.DAY_STARTTIME);
    }

    public GameTime getFormattedTime()
    {
        int tick = time.getValue();
        int hours = tick / 60;
        tick -= hours * 60;
        int fiveMinutes = tick / 5;
        return new GameTime(fiveMinutes, hours);
    }

    class GameTime{
        int minutes;
        Integer hour;

        public GameTime(int minutes, int hour)
        {
            this.minutes = minutes * 5;
            this.hour = hour;
        }

        @Override
        public String toString()
        {
            DecimalFormat formatter = new DecimalFormat("00");
            String hourFormatted = formatter.format(hour);
            String minutesFormatted = formatter.format(minutes);
            return hourFormatted + ":" + minutesFormatted;
        }
    }

    public int getTime()
    {
        return time.get();
    }

    public IntegerProperty timeProperty()
    {
        return time;
    }
}
