package com.networks.testapplication.final_sample;

import com.networks.testapplication.data.UpcomingEvent;

import java.util.ArrayList;

public class ApiService {

    /*
     * Creates a new sample API Response
     */
    static public ArrayList<UpcomingEvent> getSampleResponse(){

        ArrayList<String> sampleDates = new ArrayList<>();
        sampleDates.add("2020-01-09T15:25:36.000Z");
        sampleDates.add("2020-01-12T15:25:36.000Z");
        sampleDates.add("2020-01-13T15:25:36.000Z");
        sampleDates.add("2020-01-15T15:25:36.000Z");
        sampleDates.add("2020-01-19T15:25:36.000Z");
        sampleDates.add("2020-01-21T15:25:36.000Z");
        sampleDates.add("2020-01-22T15:25:36.000Z");
        sampleDates.add("2020-01-24T15:25:36.000Z");
        sampleDates.add("2020-01-30T15:25:36.000Z");
        sampleDates.add("2020-01-31T15:25:36.000Z");

        ArrayList<UpcomingEvent> upcomingGuestsList = new ArrayList<>();

        for(int i = 2; i<20; i++){

            UpcomingEvent sampleUpcomingEvent = new UpcomingEvent();
            sampleUpcomingEvent.setEventId("139593257");
            sampleUpcomingEvent.setTitle("Angela in North Conference 2");
            sampleUpcomingEvent.setSpaceId(78286);
            sampleUpcomingEvent.setSpaceName("North Conference 2");
            sampleUpcomingEvent.setRobinFloorId(12642);
            sampleUpcomingEvent.setRobinFloorNumber("4");
            sampleUpcomingEvent.setRobinFloorName("4th Floor");
            sampleUpcomingEvent.setStart(sampleDates.get(i/2 - 1));
            sampleUpcomingEvent.setEnd(sampleDates.get(i/2 - 1));
            sampleUpcomingEvent.setDescription("North Conference 2 at 333 South Grand Avenue");
            sampleUpcomingEvent.setLocationId(15975);

            upcomingGuestsList.add(sampleUpcomingEvent);
        }

        return upcomingGuestsList;
    }
}
