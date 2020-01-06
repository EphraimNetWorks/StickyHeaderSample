package com.networks.testapplication.calendar;

import org.threeten.bp.LocalDate;

import java.util.Date;

public class Event {

    private LocalDate date;
    private String events;

    public Event(LocalDate date, String events) {
        this.date = date;
        this.events = events;

    }

    public LocalDate getDate() {
        return date;
    }

    public String getEvents() {
        return events;
    }
}
