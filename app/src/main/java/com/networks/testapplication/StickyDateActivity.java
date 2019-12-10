package com.networks.testapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StickyDateActivity extends AppCompatActivity implements UpcomingGuestListAdapter.Callback {

    @BindView(R.id.upcoming_guests_recycler_view)
    RecyclerView mRecyclerView;


    UpcomingGuestListAdapter mUpcomingGuestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_date);
        ButterKnife.bind(this);

        mUpcomingGuestAdapter = new UpcomingGuestListAdapter(this);
        mRecyclerView.setAdapter(mUpcomingGuestAdapter);

        //mimicking scenarios
        mimickFailSucceedCall();
        //mimicSucceedFailSucceedCall();

    }

    private void mimickFailSucceedCall(){
        //network call failed
        mUpcomingGuestAdapter.showEmptyViewHolder(true);

        //succeed after 5 seconds
        new Handler().postDelayed(() -> {

            //network call success
            mUpcomingGuestAdapter.showEmptyViewHolder(false);
            addData(getDayReservationsList(getSampleResponse()));

        },5000);
    }

    private void mimickSucceedFailSucceedCall(){
        //network call success
        addData(getDayReservationsList(getSampleResponse()));
        mRecyclerView.smoothScrollToPosition(mUpcomingGuestAdapter.getItemCount());

        // fail after 5 seconds
        new Handler().postDelayed(() -> {

            //network call failed
            mUpcomingGuestAdapter.showEmptyViewHolder(true);
            mRecyclerView.smoothScrollToPosition(mUpcomingGuestAdapter.getItemCount());

            //succeed after another 5 seconds
            new Handler().postDelayed(() -> {

                //network call success
                mUpcomingGuestAdapter.showEmptyViewHolder(false);
                addData(getDayReservationsList(getSampleResponse()));

            },5000);

        },5000);
    }


    private void addData(ArrayList<DayReservations> data) {
        for (DayReservations dayReservations: data) {
            HeaderDataImpl headerData1 = new HeaderDataImpl(HeaderDataImpl.HEADER,
                    R.layout.item_sticky_header,dayReservations.getDayDate());

            //notice addHeaderAndData is being used instead of setHeaderAndData
            mUpcomingGuestAdapter.addHeaderAndData(dayReservations.getUpcomingGuests(), headerData1);
        }

    }


    /*
     * Convert api response to list of day reservations
     */
    private ArrayList<DayReservations> getDayReservationsList(UpcomingGuestsList upcomingGuestsResponse){

        //list of days and their upcoming guests
        ArrayList<DayReservations> dayReservationsList = new ArrayList<>();

        //sort meetings by date
        List<Meeting> meetings = upcomingGuestsResponse.getMeetings();
        Collections.sort(meetings, (meeting, t1) -> meeting.getDateStart().compareTo(t1.getDateStart()));


        for (Meeting meeting: meetings) {

            // convert meeting date(with time) to day date(without time)
            String meetingDayDate = DateTimeUtils.convertDate(meeting.getDateStart(),
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MMM dd,yyyy");

            //add new day to dayReservationsList if it doesn't exist
            DayReservations dayReservations = new DayReservations(meetingDayDate);
            dayReservations.setUpcomingGuests(new ArrayList<>());
            if(!dayReservationsList.contains(dayReservations)){
                dayReservationsList.add(dayReservations);
            }

            // get day reservation position in list which will be used to add new upcoming guests
            int dayReservationsListPosition = dayReservationsList.indexOf(dayReservations);

            //sort visitors by last name
            List<Visitor> visitors = meeting.getVisitors();
            Collections.sort(visitors, (visitor, t1) -> visitor.getLastName().compareTo(t1.getLastName()));

            for(Visitor visitor: visitors){

                UpcomingGuest upcomingGuest = new UpcomingGuest(visitor,meeting);

                // add upcoming guest to corresponding day reservations object
                dayReservationsList.get(dayReservationsListPosition).addUpcomingGuest(upcomingGuest);
            }
        }

        return dayReservationsList;
    }

    /*
     * Creates a sample API Response
     */
    private UpcomingGuestsList getSampleResponse(){

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

        UpcomingGuestsList upcomingGuestsList = new UpcomingGuestsList();

        upcomingGuestsList.setTotalResults(10);
        upcomingGuestsList.setMeetings(new ArrayList<>());

        for(int i = 0; i<10; i++){

            Meeting meeting = new Meeting();
            meeting.setId("meeting "+i);
            meeting.setDateStart(sampleDates.get(i));
            meeting.setDateEnd(sampleDates.get(i));

            meeting.setVisitors(new ArrayList<>());
            for(int j = 0; j<7; j++){
                Visitor visitor = new Visitor();
                visitor.setVisitId("visitor "+j);
                visitor.setFirstName("FirstName"+j);
                visitor.setLastName("LastName"+j);
                visitor.setEmail("sampleemail@df.dj");

                meeting.getVisitors().add(visitor);
            }

            upcomingGuestsList.getMeetings().add(meeting);
        }

        return upcomingGuestsList;
    }

    @Override
    public void onItemClick(String guestFirstName, String guestLastName, String guestEmail) {

    }

    @Override
    public void refreshGuestList() {

    }

    @Override
    public void showDialog(int title, int body, int positiveText, DialogInterface.OnClickListener positiveListener, int negativeText, DialogInterface.OnClickListener negativeListener, Object o) {

    }
}

