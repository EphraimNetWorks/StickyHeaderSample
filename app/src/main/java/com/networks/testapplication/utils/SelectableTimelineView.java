package com.networks.testapplication.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
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

        int intervalIncrement = 5;
        int approximatedHour = time.getHour() + ((time.getMinute()+intervalIncrement > 60)? 1:0);
        int approximatedMinute = (time.getMinute()+(intervalIncrement-time.getMinute()%intervalIncrement))%60;
        time = LocalTime.of(approximatedHour,approximatedMinute );

        int position = time.getHour()+ (time.getMinute()<30?0:1);
        boolean startFromFirst = time.getMinute()>=30;
        double unselectableRatio ;
        if(time.getMinute()%30 == 0){
           unselectableRatio = 0;
        }else if(time.getMinute()<30){
            unselectableRatio = time.getMinute()/30.0;
        }else {
            unselectableRatio = (time.getMinute()-30)/30.0;
        }

        while (position<timelineLinearLayout.getChildCount())  {
            SelectableTimelinePoint point = (SelectableTimelinePoint) timelineLinearLayout.getChildAt(position);


            if(startFromFirst) {
                if (point.getItem().getFirstLineColor() != unselectableColor){
                    point.setFirstRangeSelectablePercentage(unselectableRatio);
                    point.setCustomFirstStartTime(new TimelineTime(time.getHour(),time.getMinute()));
                    point.selectFirstRange();
                    break;
                }
            }
            if (point.getItem().getSecondLineColor() != unselectableColor && position<24){
                point.setSecondSelectablePercentage(unselectableRatio);
                point.setCustomSecondStartTime(new TimelineTime(time.getHour(),time.getMinute()));
                point.selectSecondRange();
                break;
            }

            position++;
            startFromFirst = true;

        }

    }

    public void selectRange(TimelineRange rangeToSelect){

        TimelineTime startTime = rangeToSelect.getStartTime();
        TimelineTime endTime =  rangeToSelect.getEndTime();

        int startPosition = startTime.getHour()+ (startTime.getMinute()<30?0:1);
        int endPosition = endTime.getHour()+ (endTime.getMinute()>30?1:0);

        boolean startFromFirst = startTime.getMinute()>=30;
        boolean endAtFirst = endTime.getMinute()==0 || endTime.getMinute()>30;

        for(int i = startPosition; i<endPosition+1; i++){

            SelectableTimelinePoint point = (SelectableTimelinePoint) timelineLinearLayout.getChildAt(i);
            if(i == startPosition) {
                if(startFromFirst) {
                    selectFirstRange(point);
                }
                if(endPosition >startPosition || (endPosition == startPosition && !endAtFirst)){
                    selectSecondRange(point);
                }
            }else if(endAtFirst && i == endPosition){
                selectFirstRange(point);
            }else {
                selectFirstRange(point);
                selectSecondRange(point);
            }

        }

    }

    private void selectFirstRange(SelectableTimelinePoint point){
        if (point.getItem().getFirstLineColor() != unselectableColor && !point.isFirstRangeSelected()){
            point.selectFirstRange();
        }
    }

    private void selectSecondRange(SelectableTimelinePoint point){
        if (point.getItem().getFirstLineColor() != unselectableColor && !point.isSecondRangeSelected()){
            point.selectSecondRange();
        }
    }

    public void deselectRange(TimelineRange rangeToSelect){

        TimelineTime startTime = rangeToSelect.getStartTime();
        TimelineTime endTime =  rangeToSelect.getEndTime();

        int startPosition = startTime.getHour()+ (startTime.getMinute()<30?0:1);
        int endPosition = endTime.getHour()+ (endTime.getMinute()>30?1:0);

        boolean startFromFirst = startTime.getMinute()>=30;
        boolean endAtFirst = endTime.getMinute()==0 || endTime.getMinute()>30;

        if(selectRangeEndTime!= null && selectRangeEndTime.isAfter(endTime)){
            deselectPoints(startPosition,endPosition,startFromFirst,endAtFirst);
        }else {
            inverseDeselectPoints(startPosition,endPosition,startFromFirst,endAtFirst);
        }

    }

    private void deselectPoints(int startPosition, int endPosition,
                                boolean startFromFirst, boolean endAtFirst){
        for(int i = startPosition; i<endPosition+1; i++){
            deselectPoint(i, startPosition, endPosition, startFromFirst, endAtFirst);
        }
    }

    private void inverseDeselectPoints(int startPosition, int endPosition,
                                       boolean startFromFirst, boolean endAtFirst){
        for(int i = endPosition; i>startPosition-1; i--){
            deselectPoint(i, startPosition, endPosition, startFromFirst, endAtFirst);
        }
    }

    private void deselectPoint(int i , int startPosition, int endPosition,
                               boolean startFromFirst, boolean endAtFirst){
        SelectableTimelinePoint point = (SelectableTimelinePoint) timelineLinearLayout.getChildAt(i);
        if(i == startPosition) {
            if(startFromFirst) {
                deselectFirstRange(point);
            }
            if(endPosition >startPosition || (endPosition == startPosition && !endAtFirst)){
                deselectSecondRange(point);
            }
        }else if(endAtFirst && i == endPosition){
            deselectFirstRange(point);
        }else {
            deselectFirstRange(point);
            deselectSecondRange(point);
        }
    }

    private void deselectFirstRange(SelectableTimelinePoint point){
        if (point.getItem().getFirstLineColor() != unselectableColor && point.isFirstRangeSelected()){
            point.deselectFirstRange();
        }
    }

    private void deselectSecondRange(SelectableTimelinePoint point){
        if (point.getItem().getFirstLineColor() != unselectableColor && point.isSecondRangeSelected()){
            point.deselectSecondRange();
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

    public View findViewAt( int x, int y) {
        return findViewAt(timelineLinearLayout, x, y);
    }

    public View findViewAt(ViewGroup viewGroup, int x, int y) {
        for(int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                View foundView = findViewAt((ViewGroup) child, x, y);
                if (foundView != null && foundView.isShown()) {
                    return foundView;
                }
            } else {
                int[] location = new int[2];
                child.getLocationOnScreen(location);
                Rect rect = new Rect(location[0], location[1], location[0] + child.getWidth(), location[1] + child.getHeight());
                if (rect.contains(x, y)) {
                    return child;
                }
            }
        }

        return null;
    }


}
