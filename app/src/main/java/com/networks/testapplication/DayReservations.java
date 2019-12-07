package com.networks.testapplication;

import androidx.annotation.Nullable;

import com.saber.stickyheader.stickyData.StickyMainData;

import java.util.ArrayList;

public class DayReservations {

    private String dayDate;

    private ArrayList<UpcomingGuest> upcomingGuests;

    public DayReservations(String dayDate){
        this.dayDate = dayDate;
    }

    public void addUpcomingGuest(UpcomingGuest upcomingGuest){
        upcomingGuests.add(upcomingGuest);
    }

    public String getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }

    public ArrayList<UpcomingGuest> getUpcomingGuests() {
        return upcomingGuests;
    }

    public void setUpcomingGuests(ArrayList<UpcomingGuest> upcomingGuests) {
        this.upcomingGuests = upcomingGuests;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof DayReservations && ((DayReservations) obj).dayDate.equals(dayDate);

    }
}
