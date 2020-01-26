package com.networks.testapplication.ui.upcoming_events;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.networks.testapplication.R;
import com.networks.testapplication.calendar.EventDecorator;
import com.networks.testapplication.data.DayEventReservations;
import com.networks.testapplication.data.UpcomingEvent;
import com.networks.testapplication.ui.adapters_viewholders.DateHeaderDataImpl;
import com.networks.testapplication.ui.adapters_viewholders.HeaderDataImpl;
import com.networks.testapplication.ui.adapters_viewholders.UpcomingEventListAdapter;
import com.networks.testapplication.utils.DateTimeUtils;
import com.networks.testapplication.utils.NetworkState;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpcomingEventActivity extends AppCompatActivity implements UpcomingEventListAdapter.Callback, OnDateSelectedListener {

    @BindView(R.id.calendarView)
    MaterialCalendarView calendarView;
    @BindView(R.id.upcoming_guests_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.month_name_textview)
    TextView monthNameTextView;

    UpcomingEventListAdapter mUpcomingEventAdapter;

    ArrayList<DayEventReservations> mDayEventReservationsArrayList;
    ArrayList<CalendarDay> dates = new ArrayList<>();
    LinearLayoutManager layoutManager;
    Boolean updateCalendarViewOnScroll = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_date);
        ButterKnife.bind(this);

        mUpcomingEventAdapter = new UpcomingEventListAdapter(this);
        mRecyclerView.setAdapter(mUpcomingEventAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy == 0) {
                    updateCalendarViewOnScroll = false;
                }else {
                    updateCalendarViewOnScroll = true;
                }
            }
        });

        layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        //mimick scenarios
        mimickSucceedCall();

    }

    private void setUpCalenderView(){

        calendarView.setOnDateChangedListener(this);

        calendarView.setDateTextAppearance(View.ACCESSIBILITY_LIVE_REGION_ASSERTIVE);

        updateCalendarViewDay(LocalDate.now());

        onDateSelected(calendarView, CalendarDay.from(LocalDate.now()),true);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        Pair<Boolean, Integer> isContained_Position = getPositionToScrollTo(date);
        updateCalendarViewOnScroll = isContained_Position.first;

        layoutManager.scrollToPositionWithOffset(isContained_Position.second,0);

    }

    private Pair<Boolean, Integer> getPositionToScrollTo(CalendarDay date){
        if(mUpcomingEventAdapter.getDatePosition(date) != null){
            return new Pair<>(true,mUpcomingEventAdapter.getDatePosition(date));
        }else {
            for(CalendarDay calendarDay : dates){
                if(calendarDay.getDate().compareTo(date.getDate()) >= 0){
                    return new Pair<>(false,mUpcomingEventAdapter.getDatePosition(calendarDay));
                }
            }
            return new Pair<>(false,mUpcomingEventAdapter.getDatePosition(dates.get(dates.size()-1)));
        }
    }

    private Month currentMonth;
    private void checkAndUpdateReservationMonth(LocalDate date){

        if(currentMonth == null || !currentMonth.equals(date.getMonth())){
            monthNameTextView.setText(date.getMonth().getDisplayName(TextStyle.FULL, Locale.US).toUpperCase());
            currentMonth = date.getMonth();
        }
    }

    private void updateCalendarViewDay(LocalDate date){

        calendarView.setCurrentDate(date);
        calendarView.setSelectedDate(date);
    }

    private void addData(ArrayList<DayEventReservations> data) {
        for (DayEventReservations dayEventReservations: data) {

            DateHeaderDataImpl headerData1 = new DateHeaderDataImpl(HeaderDataImpl.HEADER,
                    R.layout.item_sticky_header,dayEventReservations.getDayDate());

            dates.add(CalendarDay.from(dayEventReservations.getDayDate()));
            mUpcomingEventAdapter.addHeaderAndData(dayEventReservations.getUpcomingEvents(), headerData1);
        }

        setUpCalenderView();

        //add small dots on event days
        EventDecorator eventDecorator = new EventDecorator(Color.RED, dates);
        calendarView.addDecorator(eventDecorator);

    }

    private void mimickSucceedCall(){

        //network call success
        mUpcomingEventAdapter.setNetworkState(NetworkState.loaded());
        mDayEventReservationsArrayList = getDayEventReservationsList(getSampleResponse());
        addData(mDayEventReservationsArrayList);

        checkAndUpdateReservationMonth(mDayEventReservationsArrayList.get(0).getDayDate());

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

            // convert start date(with time) to local date
            LocalDate date = LocalDate.parse(upcomingEvent.getStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

            //add new day to dayReservationsList if it doesn't exist
            DayEventReservations dayEventReservations = new DayEventReservations(date);
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
        sampleDates.add("2020-01-15T15:25:36.000Z");
        sampleDates.add("2020-01-19T15:25:36.000Z");
        sampleDates.add("2020-01-21T15:25:36.000Z");
        sampleDates.add("2020-01-22T15:25:36.000Z");
        sampleDates.add("2020-02-24T15:25:36.000Z");
        sampleDates.add("2020-03-30T15:25:36.000Z");
        sampleDates.add("2020-03-31T15:25:36.000Z");

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

    @Override
    public void onNewHeaderAttached(LocalDate date) {

        checkAndUpdateReservationMonth(date);

        if (updateCalendarViewOnScroll) updateCalendarViewDay(date);

    }

}

