package com.networks.testapplication.utils;

import androidx.annotation.Nullable;

public class TimelineTime {

    private int hour;
    private int minute;
    public TimelineTime(int hour, int minute){
        validateTime(hour, minute);
        this.hour= hour;
        this.minute = minute;
    }

    private void validateTime(int hour, int minute){
        if(hour<0 || hour>24){
            throw new IllegalArgumentException("Invalid hour:"+hour+"");
        }else if(minute<0 || minute>60){
            throw new IllegalArgumentException("Invalid minute:"+minute);
        }else if(hour>23 && minute>0){
            throw new IllegalArgumentException("Invalid time:"+ hour+":"+minute);
        }
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isBefore(TimelineTime time){
        if(time == null) return false;
        if(hour < time.hour){
            return true;
        }

        if(time.hour == hour && minute< time.minute){
            return true;
        }
        return false;
    }

    public boolean isAfter(TimelineTime time){

        if(time == null) return false;
        if(hour > time.hour){
            return true;
        }

        if(time.hour == hour && minute> time.minute){
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean equals = false;
        if(obj instanceof TimelineTime){
            equals = ((TimelineTime) obj).getHour() == hour && ((TimelineTime) obj).getMinute() == minute;
        }
        return equals;
    }
}
