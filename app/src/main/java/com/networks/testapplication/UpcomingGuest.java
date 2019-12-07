package com.networks.testapplication;

import com.saber.stickyheader.stickyData.StickyMainData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UpcomingGuest implements StickyMainData {
    

    private String visitId;

    private String firstName;

    private String lastName;

    private String email;

    private Object phone;

    private String status;

    private String meetingId;

    private String meetingDateStart;

    private String meetingDateEnd;

    private Boolean isShortNoticeRegistration;
    
    public UpcomingGuest(Visitor visitor, Meeting meeting){

        this.visitId = visitor.getVisitId();
        this.firstName = visitor.getFirstName();
        this.lastName = visitor.getLastName();
        this.email = visitor.getEmail();
        this.phone = visitor.getPhone();
        this.status = visitor.getStatus();
        this.meetingId = meeting.getId();
        this.meetingDateStart = meeting.getDateStart();
        this.meetingDateEnd = meeting.getDateEnd();

    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingDateStart() {
        return meetingDateStart;
    }

    public void setMeetingDateStart(String meetingDateStart) {
        this.meetingDateStart = meetingDateStart;
    }

    public String getMeetingDateEnd() {
        return meetingDateEnd;
    }

    public void setMeetingDateEnd(String meetingDateEnd) {
        this.meetingDateEnd = meetingDateEnd;
    }


    public Boolean isShortNoticeRegistration() {
        return isShortNoticeRegistration;
    }

    public void setShortNoticeRegistration(Boolean shortNoticeRegistration) {
        isShortNoticeRegistration = shortNoticeRegistration;
    }
}
