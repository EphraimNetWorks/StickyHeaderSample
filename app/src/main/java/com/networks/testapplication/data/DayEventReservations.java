package com.networks.testapplication.data;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DayEventReservations {

    private String dayDate;

    private ArrayList<UpcomingEvent> upcomingEvents;

    public DayEventReservations(String dayDate){
        this.dayDate = dayDate;
    }

    public void addUpcomingEvent(UpcomingEvent upcomingGuest){
        upcomingEvents.add(upcomingGuest);
    }

    public String getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }

    public ArrayList<UpcomingEvent> getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(ArrayList<UpcomingEvent> upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof DayEventReservations && ((DayEventReservations) obj).dayDate.equals(dayDate);

    }
}
