package com.networks.testapplication.utils;


import android.util.Range;

import java.util.ArrayList;
import java.util.List;

public class TimelineRange {


    private Range<Integer> range;
    private TimelineTime startTime;
    private TimelineTime endTime;

    public TimelineRange(TimelineTime startTime, TimelineTime endTime){
        this.startTime = startTime;
        this.endTime = endTime;

        int startMinute = startTime.getMinute();
        int endMinute = endTime.getMinute();
        int rangeLower = startTime.getHour()*100+ (startMinute<30? 0:50);
        int rangeUpper = endTime.getHour()*100+ (endMinute == 0?0:endMinute<=30?50:100);
        range = new Range<>(rangeLower, rangeUpper);

    }

    public Boolean containsFirst(int position){
        int positionRangeValue = position*100;
        return range.contains(positionRangeValue) && range.contains(positionRangeValue-50);
    }

    public Boolean containsSecond(int position){
        int positionRangeValue = position*100;
        return range.contains(Math.abs(positionRangeValue)) && range.contains(positionRangeValue+50);
    }

    public TimelineTime getStartTime() {
        return startTime;
    }

    public TimelineTime getEndTime() {
        return endTime;
    }
}
