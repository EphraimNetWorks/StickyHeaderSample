package com.networks.testapplication.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.networks.testapplication.R;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.List;

public class TimelinePoint extends FrameLayout {


    private View firstLine;
    private View secondLine;
    private View verticalLine;
    private TextView textview;


    private int highlightedColor = Color.BLUE;
    private int defaultColor = Color.LTGRAY;

    public TimelinePoint(Context context){
        super(context);
        initView(context);
    }

    public TimelinePoint(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        View view = View.inflate(context, R.layout.timeline_point, null);
        firstLine = view.findViewById(R.id.first_line);
        secondLine = view.findViewById(R.id.second_line);
        verticalLine = view.findViewById(R.id.vertical_line);
        textview = view.findViewById(R.id.timeline_text);
        addView(view);
    }


    public void setItem(NewTimelineView.Item item, int position, int count){
        updateLineVisibility(position,count);

        firstLine.setBackgroundColor(item.getFirstLineColor());
        secondLine.setBackgroundColor(item.getSecondLineColor());

        if(item.getFirstLineColor() == highlightedColor || item.getSecondLineColor() == highlightedColor){
            verticalLine.setBackgroundColor(highlightedColor);
            textview.setTypeface(null, Typeface.BOLD);
            textview.setTextColor(Color.BLACK);
        }else {
            verticalLine.setBackgroundColor(defaultColor);
            textview.setTypeface(null, Typeface.NORMAL);
            textview.setTextColor(Color.GRAY);
        }

        switch (position){
            case 0:
                textview.setText("12AM");
                break;
            case 12:
                textview.setText("12PM");
                break;
            case 24:
                textview.setText("12AM");
                break;
            default:
                textview.setText(""+position%12);
                break;
        }

    }

    private void updateLineVisibility(int position, int count){
        if(position == 0){
            firstLine.setVisibility(View.GONE);
            secondLine.setVisibility(View.VISIBLE);
        }else if (position == count-1){
            secondLine.setVisibility(View.GONE);
            firstLine.setVisibility(View.VISIBLE);
        }else {
            secondLine.setVisibility(View.VISIBLE);
            firstLine.setVisibility(View.VISIBLE);
        }
    }

    public void setHighlightedLineColor(int color){

       highlightedColor = color;
    }

    public void setDefaultLineColor(int color){

        defaultColor = color;
    }


}
