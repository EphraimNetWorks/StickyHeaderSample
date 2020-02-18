package com.networks.testapplication.utils;


import android.util.Pair;
import android.util.Range;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

public class TimelineRange {


    private Range<Integer> range;

    public TimelineRange(LocalTime startTime, LocalTime endTime){
        int startMinute = startTime.getMinute();
        int endMinute = endTime.getMinute();
        int rangeLower = startTime.getHour()*100+ (startMinute<30? 0:50);
        int rangeUpper = endTime.getHour()*100+ (endMinute<30? 0:50);
        range = new Range<>(rangeLower, rangeUpper);

    }


    public Pair<Boolean,Boolean> contains(int position){
        int positionRangeValue = position*100;
        return new Pair<>(range.contains(positionRangeValue) && range.contains(Math.abs(positionRangeValue-50)),
                 range.contains(Math.abs(positionRangeValue)) && range.contains(positionRangeValue+50));
    }
}
