package com.networks.testapplication.final_sample;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.networks.testapplication.R;
import com.networks.testapplication.calendar.Event;
import com.networks.testapplication.calendar.EventDecorator;
import com.networks.testapplication.calendar.MyAdapter;
import com.networks.testapplication.data.UpcomingEvent;
import com.networks.testapplication.utils.AppPreferencesHelper;
import com.networks.testapplication.utils.DateTimeUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by raghu on 2/7/17.
 */

public class FinalSampleFragment extends Fragment implements OnDateSelectedListener,UpcomingEventsAdapter.Callback {


    @BindView(R.id.calendarView)
    MaterialCalendarView calendarView;
    @BindView(R.id.upcoming_events_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.event_number_textview)
    TextView eventNumberTextview;
    @BindView(R.id.no_events_layout)
    LinearLayout noEventsLayout;

    private HashMap<CalendarDay,List<UpcomingEvent>> map = new HashMap<>();
    private UpcomingEventsAdapter adapter = new UpcomingEventsAdapter(this);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_final_sample, container, false);

        ButterKnife.bind(this,view);

        recyclerView.setAdapter(adapter);

        calendarView =  view.findViewById(R.id.calendarView);
        calendarView.setDateTextAppearance(View.ACCESSIBILITY_LIVE_REGION_ASSERTIVE);

        calendarView.setSelectedDate(LocalDate.now());

        calendarView.setOnDateChangedListener(this);

        makeJsonObjectRequest();

        return view;
    }




    private void makeJsonObjectRequest() {

        List<UpcomingEvent> eventList = ApiService.getSampleResponse();

        for(UpcomingEvent event: eventList){

            LocalDate date = LocalDate.parse(event.getStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.US));
            CalendarDay day = CalendarDay.from(date);

            if(!map.containsKey(day))
            {
                List<UpcomingEvent> events = new ArrayList<>();
                events.add(event);
                map.put(day,events);
            }else
            {
                List<UpcomingEvent> events = map.get(day);
                events.add(event);
                map.put(day,events);

            }

        }

        // after parsing
        updateRecyclerItems(CalendarDay.from(LocalDate.now()));

        //add small dots on event days
        EventDecorator eventDecorator = new EventDecorator(getContext(), map.keySet());
        calendarView.addDecorator(eventDecorator);


    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        updateRecyclerItems(date);

    }

    private void updateRecyclerItems(CalendarDay date){


        List<UpcomingEvent> event =  map.get(date);
        if(event!=null && event.size()>0) {
            adapter.submitNewList(event);
            String eventNumber = event.size() + (event.size()>1 ? " EVENTS": " EVENT");
            eventNumberTextview.setVisibility(View.VISIBLE);
            String s = DateTimeUtils.formatDate(date.getYear(),date.getMonth(),date.getDay()) + " ("+ eventNumber + ")";
            eventNumberTextview.setText(s.toUpperCase());
            noEventsLayout.setVisibility(View.GONE);
        }else {
            adapter.clear();
            String s = DateTimeUtils.formatDate(date.getYear(),date.getMonth(),date.getDay());
            eventNumberTextview.setText(s.toUpperCase());
            eventNumberTextview.setVisibility(View.VISIBLE);
            noEventsLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(String eventId) {

    }

    @Override
    public void showDialog(int title, int body, int positiveText, DialogInterface.OnClickListener positiveListener, int negativeText, DialogInterface.OnClickListener negativeListener, Object o) {

    }

    @Override
    public void refreshEventList() {

    }

}