package com.networks.testapplication.ui.upcoming_events;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.networks.testapplication.R;
import com.networks.testapplication.data.DayEventReservations;
import com.networks.testapplication.data.UpcomingEvent;
import com.networks.testapplication.ui.adapters_viewholders.HeaderDataImpl;
import com.networks.testapplication.ui.adapters_viewholders.UpcomingEventListAdapter;
import com.networks.testapplication.utils.DateTimeUtils;
import com.networks.testapplication.utils.NetworkState;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpcomingEventActivity extends AppCompatActivity implements UpcomingEventListAdapter.Callback {

    @BindView(R.id.upcoming_guests_recycler_view)
    RecyclerView mRecyclerView;

    UpcomingEventListAdapter mUpcomingEventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_date);
        ButterKnife.bind(this);

        mUpcomingEventAdapter = new UpcomingEventListAdapter(this);
        mRecyclerView.setAdapter(mUpcomingEventAdapter);

        //mimick scenarios
        //mimickFailRetrySucceedCall();
        mimickFailRetryEmptyResponseCall();

    }


    private void addData(ArrayList<DayEventReservations> data) {
        for (DayEventReservations dayEventReservations: data) {
            HeaderDataImpl headerData1 = new HeaderDataImpl(HeaderDataImpl.HEADER,
                    R.layout.item_sticky_header,dayEventReservations.getDayDate());


            mUpcomingEventAdapter.addHeaderAndData(dayEventReservations.getUpcomingEvents(), headerData1);
        }

    }

    private void mimickFailRetrySucceedCall(){
        //network call failed

        mUpcomingEventAdapter.setNetworkState(NetworkState.error(true,
                R.string.no_event_found_title, R.string.no_event_found_body));

        //run after 3 seconds
        new Handler().postDelayed(() -> {

            //network call success
            mUpcomingEventAdapter.setNetworkState(NetworkState.loading(true));

            //succeed after 5 seconds
            new Handler().postDelayed(() -> {

                //network call success
                mUpcomingEventAdapter.setNetworkState(NetworkState.loaded());
                addData(getDayEventReservationsList(getSampleResponse()));

            },5000);

        },3000);
    }



    private void mimickFailRetryEmptyResponseCall(){
        //network call failed

        mUpcomingEventAdapter.setNetworkState(NetworkState.error(true, R.string.no_event_found_title, R.string.no_event_found_body));

        //run after 3 seconds
        new Handler().postDelayed(() -> {

            //network call success
            mUpcomingEventAdapter.setNetworkState(NetworkState.loading(true));

            //succeed after 5 seconds
            new Handler().postDelayed(() -> {

                //network call success
                mUpcomingEventAdapter.setNetworkState(NetworkState.loaded(true, R.drawable.ic_no_events, R.string.no_event_found_title, R.string.no_event_found_body));


            },5000);

        },3000);
    }

    /*
     * Convert api response to list of day reservations
     */
    private ArrayList<DayEventReservations> getDayEventReservationsList(ArrayList<UpcomingEvent> upcomingEventsResponse){

        //list of days and their upcoming guests
        ArrayList<DayEventReservations> dayEventReservationsList = new ArrayList<>();

        //sort upcoming events by date
        Collections.sort(upcomingEventsResponse, (upcomingEvent, t1) -> upcomingEvent.getStart().compareTo(t1.getStart()));


        for(UpcomingEvent upcomingEvent: upcomingEventsResponse){

            // convert start date(with time) to day date(without time)
            String meetingDayDate = DateTimeUtils.convertDate(upcomingEvent.getStart(),
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MMM dd,yyyy");

            //add new day to dayReservationsList if it doesn't exist
            DayEventReservations dayEventReservations = new DayEventReservations(meetingDayDate);
            dayEventReservations.setUpcomingEvents(new ArrayList<>());
            if(!dayEventReservationsList.contains(dayEventReservations)){
                dayEventReservationsList.add(dayEventReservations);
            }


            // get day reservation position in list which will be used to add new upcoming guests
            int dayReservationsListPosition = dayEventReservationsList.indexOf(dayEventReservations);

            // add upcoming guest to corresponding day reservations object
            dayEventReservationsList.get(dayReservationsListPosition).addUpcomingEvent(upcomingEvent);

        }

        return dayEventReservationsList;

    }

    /*
     * Creates a new sample API Response
     */
    private ArrayList<UpcomingEvent> getSampleResponse(){

        ArrayList<String> sampleDates = new ArrayList<>();
        sampleDates.add("2019-11-28T15:25:36.000Z");
        sampleDates.add("2019-12-09T15:25:36.000Z");
        sampleDates.add("2019-12-12T15:25:36.000Z");
        sampleDates.add("2019-12-13T15:25:36.000Z");
        sampleDates.add("2019-12-15T15:25:36.000Z");
        sampleDates.add("2019-12-19T15:25:36.000Z");
        sampleDates.add("2019-12-21T15:25:36.000Z");
        sampleDates.add("2019-12-22T15:25:36.000Z");
        sampleDates.add("2019-12-24T15:25:36.000Z");
        sampleDates.add("2019-12-28T15:25:36.000Z");

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
            sampleUpcomingEvent.setDescription("North Conference 2 at 333 South Grand Avenue - sb, Sandbox has been reserved using Robin");
            sampleUpcomingEvent.setLocationId(15975);

            upcomingGuestsList.add(sampleUpcomingEvent);
        }

        return upcomingGuestsList;
    }

    @Override
    public void onItemClick(String eventId) {

    }

    @Override
    public void refreshEventList() {

    }

    @Override
    public void showDialog(int title, int body, int positiveText, DialogInterface.OnClickListener positiveListener, int negativeText, DialogInterface.OnClickListener negativeListener, Object o) {

    }
}

