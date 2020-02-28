package com.networks.testapplication.utils;

public interface OnRangeStateChangeListener{
    void onRangeSelected(TimelineTime from, TimelineTime to);
    void onRangeDeselected(TimelineTime from, TimelineTime to);
}
