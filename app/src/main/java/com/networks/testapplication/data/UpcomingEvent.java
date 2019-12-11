package com.networks.testapplication.data;

import com.saber.stickyheader.stickyData.StickyMainData;


public class UpcomingEvent implements StickyMainData {


    private Boolean isShortNoticeRegistration;

    private String eventId;

    private String title;

    private Integer spaceId;

    private String spaceName;

    private Integer robinFloorId;

    private String robinFloorNumber;

    private String robinFloorName;

    private String start;

    private String end;

    private String description;

    private Integer locationId;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Integer spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public Integer getRobinFloorId() {
        return robinFloorId;
    }

    public void setRobinFloorId(Integer robinFloorId) {
        this.robinFloorId = robinFloorId;
    }

    public String getRobinFloorNumber() {
        return robinFloorNumber;
    }

    public void setRobinFloorNumber(String robinFloorNumber) {
        this.robinFloorNumber = robinFloorNumber;
    }

    public String getRobinFloorName() {
        return robinFloorName;
    }

    public void setRobinFloorName(String robinFloorName) {
        this.robinFloorName = robinFloorName;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Boolean isShortNoticeRegistration() {
        return isShortNoticeRegistration;
    }

    public void setShortNoticeRegistration(Boolean shortNoticeRegistration) {
        isShortNoticeRegistration = shortNoticeRegistration;
    }
}
