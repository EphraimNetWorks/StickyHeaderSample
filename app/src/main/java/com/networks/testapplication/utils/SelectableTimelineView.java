package com.networks.testapplication.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.networks.testapplication.R;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectableTimelineView extends FrameLayout implements
        SelectableTimelinePoint.OnPointRangeStateChangeListener{

    private int selectedColor = Color.GREEN;
    private int unselectableColor = Color.LTGRAY;
    private int defaultColor = Color.TRANSPARENT;

    private LinearLayout timelineLinearLayout;
    private ObservableHorizontalScrollView hsv;

    private int maximumSelectableRanges = 48;
    private int selectedRangesCount = 0;

    private Context ctx;

    private OnRangeStateChangeListener mlistener;

    private HashSet<SelectableTimelinePoint> selectedRanges = new HashSet<>();

    private TimelineTime selectRangeStartTime;

    private TimelineTime selectRangeEndTime;

    public SelectableTimelineView(Context context){
        super(context);
        ctx = context;
        initView(context);
        setUnselectableRanges(new ArrayList<>(),false);
    }

    public SelectableTimelineView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        ctx = context;
        initView(context);
        setUnselectableRanges(new ArrayList<>(), false);
    }


    private void initView(Context context){
        View view = View.inflate(context, R.layout.new_timeline_view, null);
        hsv = view.findViewById(R.id.timeline_hsv);
        timelineLinearLayout = view.findViewById(R.id.timeline_view_linear_layout);
        addView(view);
    }

    public void setOnScrollChangeListener(SelectableTimelineView.OnScrollListener scrollChangeListener){


        hsv.setOnScrollListener(new ObservableHorizontalScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {
                scrollChangeListener.onScrollChange(x);
            }

            @Override
            public void onEndScroll(ObservableHorizontalScrollView scrollView) {

            }
        });


    }

    public void setUnselectableRanges(List<TimelineRange> selectedRanges, boolean scrollToCurrentTime) {
        timelineLinearLayout.removeAllViews();
        for(int hour=0; hour<25; hour++){
            Item item = new Item(selectedColor, selectedColor, hour);

            boolean containsFirst = false;
            boolean containsSecond = false;
            for (TimelineRange selectedRange: selectedRanges){

                if(!containsFirst){
                    containsFirst = selectedRange.containsFirst(hour);
                    if(containsFirst){
                        item.setFirstLineColor(unselectableColor);
                    }
                }
                if(!containsSecond){
                    containsSecond = selectedRange.containsSecond(hour);
                    if(containsSecond){
                        item.setSecondLineColor(unselectableColor);
                    }
                }
                if(containsFirst && containsSecond){
                    break;
                }
            }

            SelectableTimelinePoint timelinePoint = new SelectableTimelinePoint(ctx);
            timelinePoint.setUnselectableColor(unselectableColor);
            timelinePoint.setDefaultColor(defaultColor);
            timelinePoint.setSelectedColor(selectedColor);
            timelinePoint.setOnRangeSelectedListener(this);
            timelinePoint.setItem(item,25);

            timelineLinearLayout.addView(timelinePoint);
        }


        if(scrollToCurrentTime) {

            selectDefaultRange();

            new Handler().postDelayed(() -> {

                LocalTime time = LocalTime.now();
                scrollToTime(new TimelineTime(time.getHour(), time.getMinute()));
            }, 100);
        }
    }

    private void selectDefaultRange(){

        LocalTime time = LocalTime.now();
        int position = time.getHour()+ 1;
        boolean startFromFirst = time.getMinute()<30;

        while (position<timelineLinearLayout.getChildCount())  {
            SelectableTimelinePoint point = (SelectableTimelinePoint) timelineLinearLayout.getChildAt(position);
            if(startFromFirst) {
                if (point.getItem().getFirstLineColor() != unselectableColor){
                    point.selectFirstRange();
                    break;
                }
            }
            if (point.getItem().getSecondLineColor() != unselectableColor && position<24){
                point.selectSecondRange();
                break;
            }

            position++;
            startFromFirst = true;

        }

    }

    private void deselectStartRange(){

        int position = selectRangeStartTime.getHour()+ (selectRangeStartTime.getMinute()>=30?1:0);

        SelectableTimelinePoint point = (SelectableTimelinePoint) timelineLinearLayout.getChildAt(position);
        if(point.isFirstRangeSelected()) {
            point.deselectFirstRange();
        }else {
            point.deselectSecondRange();
        }

    }

    private void deselectEndRange(){

        int position = selectRangeEndTime.getHour()+ (selectRangeEndTime.getMinute()>30?1:0);

        SelectableTimelinePoint point = (SelectableTimelinePoint) timelineLinearLayout.getChildAt(position);
        if(point.isSecondRangeSelected()) {
            point.deselectSecondRange();
        }else {
            point.deselectFirstRange();
        }

    }


    public void setOnRangeSelectedListener(OnRangeStateChangeListener listener){
        mlistener = listener;
    }


    public void setSelectedColor(int color){

        selectedColor = color;
    }

    public void setDeselectedColor(int color){

        defaultColor = color;
    }

    public void setUnselectableColor(int color){

        unselectableColor = color;
    }

    public TimelineTime getSelectedRangeEndTime() {
        return selectRangeEndTime;
    }

    public TimelineTime getSelectedRangeStartTime() {
        return selectRangeStartTime;
    }

    public void scrollToTime(TimelineTime time){
        int offset = 5;
        scrollTo(timelineLinearLayout.getChildAt(time.getHour()).getLeft()+offset);
    }

    public void scrollTo(int position){
        hsv.smoothScrollTo( position,50);
    }

    public int getScrollPosition(){
        return hsv.getScrollX();
    }

    public interface OnScrollListener{
        void onScrollChange(int scrollX);
    }

    @Override
    public void onRangeSelected(SelectableTimelinePoint point,
                                TimelineTime from, TimelineTime to) {



        boolean isContinuing = selectedRanges.isEmpty();

        if(to.equals(selectRangeStartTime) || from.equals(selectRangeEndTime) ){
            isContinuing = true;
        }

        if(!isContinuing) {
            deselectAll();

        }
        selectedRangesCount++;
        selectedRanges.add(point);

        if(selectRangeStartTime == null || from.isBefore(selectRangeStartTime)){
            selectRangeStartTime = from;

            if(maximumSelectableRanges < selectedRangesCount){
                deselectEndRange();
            }
        }

        if(selectRangeEndTime == null || to.isAfter(selectRangeEndTime)){
            selectRangeEndTime = to;
            if(maximumSelectableRanges < selectedRangesCount){
                deselectStartRange();
            }
        }


        if(mlistener != null) {
            mlistener.onRangeSelected(from, to);
        }
    }

    private void deselectAll(){
        ArrayList<SelectableTimelinePoint> pointsToDeselect = new ArrayList<>();
        pointsToDeselect.addAll(selectedRanges);
        selectedRanges.clear();
        selectRangeEndTime = null;
        selectRangeStartTime = null;
        for(SelectableTimelinePoint selectedPoint: pointsToDeselect){
            if(selectedPoint.getItem().position >0 && selectedPoint.getItem().getFirstLineColor() != unselectableColor){
                selectedPoint.deselectFirstRange();
            }
            if(selectedPoint.getItem().position <24 && selectedPoint.getItem().getSecondLineColor()!= unselectableColor){
                selectedPoint.deselectSecondRange();
            }
        }
    }

    @Override
    public void onRangeDeselected(SelectableTimelinePoint point,
                                  TimelineTime from, TimelineTime to) {

        boolean isMid;
        if (selectedRanges.isEmpty() || from.equals(selectRangeStartTime) || to.equals(selectRangeEndTime)) {
            isMid = false;
        } else {
            isMid = true;
        }

        if (isMid) {
            deselectAll();
        }

        if (!point.isFirstRangeSelected() && !point.isSecondRangeSelected()) {
            selectedRanges.remove(point);
        }

        if (selectedRanges.isEmpty()) {
            selectRangeStartTime = null;
        } else if (from.equals(selectRangeStartTime)) {
            selectRangeStartTime = to;
        }

        if (selectedRanges.isEmpty()) {
            selectRangeEndTime = null;
        } else if (to.equals(selectRangeEndTime)) {
            selectRangeEndTime = from;
        }

        if (selectedRangesCount > 0) selectedRangesCount--;

        if (mlistener != null) {
            mlistener.onRangeDeselected(from, to);
        }
    }

    public void setMaximumSelectableRanges(int maximumSelectableRanges) {
        this.maximumSelectableRanges = maximumSelectableRanges;
    }

    public class Item{
        private int firstLineColor;
        private int secondLineColor;
        private int position;

        public Item(int firstLineColor, int secondLineColor, int position){
            this.firstLineColor = firstLineColor;
            this.secondLineColor = secondLineColor;
            this.position = position;
        }

        public int getFirstLineColor() {
            return firstLineColor;
        }

        public int getSecondLineColor() {
            return secondLineColor;
        }

        public void setFirstLineColor( int firstLineColor) {
            this.firstLineColor = firstLineColor;
        }

        public void setSecondLineColor(int secondLineColor) {
            this.secondLineColor = secondLineColor;
        }

        public int getPosition() {
            return position;
        }

    }

}
