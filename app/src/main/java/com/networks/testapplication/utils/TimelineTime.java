package com.networks.testapplication.utils;

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
}
