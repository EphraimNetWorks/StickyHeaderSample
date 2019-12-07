package com.networks.testapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpcomingGuestsList {
    @SerializedName("totalResults")
    @Expose
    private Integer totalResults;
    @SerializedName("meetings")
    @Expose
    private List<Meeting> meetings = null;


    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

}
