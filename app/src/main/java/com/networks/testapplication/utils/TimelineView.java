package com.networks.testapplication.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.networks.testapplication.R;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.List;

public class TimelineView extends FrameLayout {


    private RecyclerView timelineRecycler;

    private List<TimelineRange> ranges = new ArrayList<>();
    private TimelineAdapter adapter = new TimelineAdapter(ranges);

    public TimelineView(Context context){
        super(context);
        initView(context);
    }

    public TimelineView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        View view = View.inflate(context, R.layout.timeline_view, null);
        timelineRecycler = view.findViewById(R.id.timeline_recycler);
        timelineRecycler.setAdapter(adapter);
        addView(view);
    }


    public void setHighlightedRanges(List<TimelineRange> highlightedRanges){
        adapter.setRanges(highlightedRanges);
        new Handler().postDelayed(() -> {
            LocalTime time = LocalTime.now();
            scrollTo(new TimelineTime(time.getHour(),time.getMinute()));
        },100);
    }

    public void setOnScrollChangeListener(TimelineView.OnScrollListener scrollChangeListener){


        if(Build.VERSION.SDK_INT >=23) {

            timelineRecycler.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    scrollChangeListener.onScrollChange(view, scrollX-oldScrollX);
                }
            });

        }else {
            timelineRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    scrollChangeListener.onScrollChange(TimelineView.this,dx);
                }
            });
        }
    }



    public void setHighlightedLineColor(int color){
        adapter.setHighlightedColor(color);
    }

    public void setDefaultLineColor(int color){
        adapter.setDefaultColor(color);
    }

    public void scrollTo(TimelineTime time){
        ((LinearLayoutManager)timelineRecycler.getLayoutManager())
                .scrollToPositionWithOffset(getTimePosition(time),50);
    }

    public void smoothScrollToPosition(int dx){
        timelineRecycler.smoothScrollBy(dx, 0, new LinearInterpolator());
    }

    private int getTimePosition(TimelineTime time){

        return time.getHour()+(time.getMinute()<30 ? 0:1);
    }

    public interface OnScrollListener{
        void onScrollChange(View view, int dx);
    }

}
