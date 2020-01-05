package com.networks.testapplication.ui.upcoming_events;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.networks.testapplication.R;
import com.networks.testapplication.data.DayEventReservations;
import com.networks.testapplication.data.UpcomingEvent;
import com.networks.testapplication.ui.adapters_viewholders.HeaderDataImpl;
import com.networks.testapplication.ui.adapters_viewholders.UpcomingEventListAdapter;
import com.networks.testapplication.utils.DateTimeUtils;
import com.networks.testapplication.utils.NetworkState;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpcomingEventActivity extends AppCompatActivity implements UpcomingEventListAdapter.Callback {

    @BindView(R.id.upcoming_guests_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.calendarView)
    MaterialCalendarView calendarView;

    UpcomingEventListAdapter mUpcomingEventAdapter;
    ScrollDirection mRecyclerScrollDirection;

    ArrayList<DayEventReservations> mDayEventReservationsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_date);
        ButterKnife.bind(this);

        mUpcomingEventAdapter = new UpcomingEventListAdapter(this);
        mRecyclerView.setAdapter(mUpcomingEventAdapter);


        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mRecyclerView.smoothScrollToPosition(getDatePosition(date));
            }
        });

        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if(mRecyclerScrollDirection!=null && mRecyclerScrollDirection == ScrollDirection.SCROLLING_DOWN){
                    TextView headerView = view.findViewById(R.id.sticky_header_title);
                    if (headerView != null) {

                        String dateString = headerView.getText().toString().split("\\(")[0].trim();

                        setReservationDateInCalendarView(getReservationDatePriorTo(dateString));
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

                if(mRecyclerScrollDirection!=null && mRecyclerScrollDirection == ScrollDirection.SCROLLING_UP){
                    TextView headerView = view.findViewById(R.id.sticky_header_title);
                    if (headerView != null) {

                        String dateString = headerView.getText().toString().split("\\(")[0].trim();
                        LocalDate date = DateTimeUtils.convertToLocalDate(dateString, "MMM dd,yyyy");

                        setReservationDateInCalendarView(date);
                    }
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up
                    mRecyclerScrollDirection = ScrollDirection.SCROLLING_UP;
                } else {
                    // Scrolling down
                    mRecyclerScrollDirection = ScrollDirection.SCROLLING_DOWN;
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });

        //mimick scenarios
        mimickSucceedCall();

    }

    private LocalDate getReservationDatePriorTo(String dateString){
        for(int index=0; index<mDayEventReservationsArrayList.size(); index++){
            String reservationDate = mDayEventReservationsArrayList.get(index).getDayDate();
            if(dateString.equals(reservationDate) ){
                if(index-1<0)
                    return DateTimeUtils.convertToLocalDate(reservationDate,"MMM dd,yyyy");
                else
                    reservationDate = mDayEventReservationsArrayList.get(index-1).getDayDate();
                    return DateTimeUtils.convertToLocalDate(reservationDate,"MMM dd,yyyy");
            }
        }

        String reservationDate = mDayEventReservationsArrayList.get(0).getDayDate();
        return DateTimeUtils.convertToLocalDate(reservationDate,"MMM dd,yyyy");
    }

    private void setReservationDateInCalendarView(LocalDate date){

            calendarView.setCurrentDate(date);
            calendarView.setSelectedDate(date);
    }


    private void addData(ArrayList<DayEventReservations> data) {
        for (DayEventReservations dayEventReservations: data) {
            String headerTitle = String.format(Locale.ENGLISH,"%s (%d EVENTS)",
                    dayEventReservations.getDayDate(),dayEventReservations.getUpcomingEvents().size());

            HeaderDataImpl headerData1 = new HeaderDataImpl(HeaderDataImpl.HEADER,
                    R.layout.item_sticky_header,headerTitle);

            mUpcomingEventAdapter.addHeaderAndData(dayEventReservations.getUpcomingEvents(), headerData1);
        }

    }


    private int getDatePosition(CalendarDay date){
        int index = 0;
        for(DayEventReservations dayEventReservations: mDayEventReservationsArrayList){
            CalendarDay calendarDay = CalendarDay.from(DateTimeUtils.convertToLocalDate(dayEventReservations.getDayDate(), "MMM dd,yyyy"));

            if(date.getYear() < calendarDay.getYear()){
                return index;
            }else {
                if(date.getMonth() < calendarDay.getMonth() ){
                    return index;
                }else if (date.getMonth() == calendarDay.getMonth()) {
                    if(date.getDay() <= calendarDay.getDay()){
                        return index;
                    }else {
                        index+= dayEventReservations.getUpcomingEvents().size();
                    }
                }else {
                    index+= dayEventReservations.getUpcomingEvents().size();
                }
            }

            index++;
        }
        return  index;
    }

    private void mimickSucceedCall(){

        //network call success
        mUpcomingEventAdapter.setNetworkState(NetworkState.loaded());
        mDayEventReservationsArrayList = getDayEventReservationsList(getSampleResponse());
        addData(mDayEventReservationsArrayList);

        LocalDate date = DateTimeUtils.convertToLocalDate(mDayEventReservationsArrayList.get(0).getDayDate(),
                "MMM dd,yyyy");

        setReservationDateInCalendarView(date);

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
        sampleDates.add("2019-12-09T15:25:36.000Z");
        sampleDates.add("2019-12-12T15:25:36.000Z");
        sampleDates.add("2019-12-13T15:25:36.000Z");
        sampleDates.add("2019-12-15T15:25:36.000Z");
        sampleDates.add("2019-12-19T15:25:36.000Z");
        sampleDates.add("2019-12-21T15:25:36.000Z");
        sampleDates.add("2019-12-22T15:25:36.000Z");
        sampleDates.add("2019-12-24T15:25:36.000Z");
        sampleDates.add("2019-12-30T15:25:36.000Z");
        sampleDates.add("2019-12-31T15:25:36.000Z");

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

    enum ScrollDirection{
        SCROLLING_UP, SCROLLING_DOWN
    }
}

