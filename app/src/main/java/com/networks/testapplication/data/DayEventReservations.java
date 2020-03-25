package com.networks.testapplication.data;

import androidx.annotation.Nullable;

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;

public class DayEventReservations {

    private ZonedDateTime dayDate;

    private ArrayList<UpcomingEvent> upcomingEvents;

    public DayEventReservations(ZonedDateTime dayDate){
        this.dayDate = dayDate;
    }

    public void addUpcomingEvent(UpcomingEvent upcomingGuest){
        upcomingEvents.add(upcomingGuest);
    }

    public ZonedDateTime getDayDate() {
        return dayDate;
    }

    public void setDayDate(ZonedDateTime dayDate) {
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
