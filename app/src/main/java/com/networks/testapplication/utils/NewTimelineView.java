package com.networks.testapplication.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.networks.testapplication.R;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.List;

public class NewTimelineView extends FrameLayout {

    private int highlightedColor = Color.BLUE;
    private int defaultColor = Color.LTGRAY;

    private LinearLayout timelineLinearLayout;
    private HorizontalScrollView hsv;

    private Context ctx;

    public NewTimelineView(Context context){
        super(context);
        ctx = context;
        initView(context);
        setRanges(new ArrayList<>());
    }

    public NewTimelineView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        ctx = context;
        initView(context);
        setRanges(new ArrayList<>());
    }


    private void initView(Context context){
        View view = View.inflate(context, R.layout.new_timeline_view, null);
        hsv = view.findViewById(R.id.timeline_hsv);
        timelineLinearLayout = view.findViewById(R.id.timeline_view_linear_layout);
        addView(view);
    }

    public void setOnScrollChangeListener(NewTimelineView.OnScrollListener scrollChangeListener){


        hsv.getViewTreeObserver().addOnScrollChangedListener(() ->
                scrollChangeListener.onScrollChange(hsv.getScrollX()));
    }

    public void setRanges(List<TimelineRange> selectedRanges) {
        timelineLinearLayout.removeAllViews();
        for(int hour=0; hour<25; hour++){
            Item item = new Item(defaultColor, defaultColor);

            boolean containsFirst = false;
            boolean containsSecond = false;
            for (TimelineRange selectedRange: selectedRanges){

                if(!containsFirst){
                    containsFirst = selectedRange.containsFirst(hour);
                    if(containsFirst){
                        item.setFirstLineColor(highlightedColor);
                    }
                }
                if(!containsSecond){
                    containsSecond = selectedRange.containsSecond(hour);
                    if(containsSecond){
                        item.setSecondLineColor(highlightedColor);
                    }
                }
                if(containsFirst && containsSecond){
                    break;
                }
            }

            TimelinePoint timelinePoint = new TimelinePoint(ctx);
            timelinePoint.setHighlightedLineColor(highlightedColor);
            timelinePoint.setDefaultLineColor(defaultColor);
            timelinePoint.setItem(item,hour,25);

            timelineLinearLayout.addView(timelinePoint);
        }

        new Handler().postDelayed(() -> {
            LocalTime time = LocalTime.now();
            scrollToTime(new TimelineTime(time.getHour(),time.getMinute()));
        },100);
    }

    public void setHighlightedLineColor(int color){

        highlightedColor = color;
    }

    public void setDefaultLineColor(int color){

        defaultColor = color;
    }

    public void scrollToTime(TimelineTime time){
        int offset = 5;
        scrollTo(timelineLinearLayout.getChildAt(time.getHour()).getLeft()+offset);
    }

    public void scrollTo(int position){
        hsv.smoothScrollTo( position,50);
    }

    public interface OnScrollListener{
        void onScrollChange(int scrollX);
    }

    public class Item{
        private int firstLineColor;
        private int secondLineColor;

        public Item(int firstLineColor, int secondLineColor){
            this.firstLineColor = firstLineColor;
            this.secondLineColor = secondLineColor;
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

    }

}
