package com.networks.testapplication.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.networks.testapplication.R;

public class SelectableTimelinePoint extends FrameLayout {


    private View firstLine;
    private View verticalFirstLine;
    private RangeView firstRangeView;
    private View secondLine;
    private RangeView secondRangeView;
    private View verticalLine;
    private View verticalLineExtension;
    private TextView textview;

    private int selectedColor = Color.GREEN;
    private int unselectableColor = Color.LTGRAY;
    private int defaultColor = Color.TRANSPARENT;


    private OnPointRangeStateChangeListener mlistener;

    private SelectableTimelineView.Item mItem;

    private TimelineTime mCustomFirstStartTime = null;
    private TimelineTime mCustomSecondStartTime = null;

    public SelectableTimelinePoint(Context context){
        super(context);
        initView(context);
    }

    public SelectableTimelinePoint(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        View view = View.inflate(context, R.layout.selectable_timeline_point, null);
        firstLine = view.findViewById(R.id.first_line);
        verticalFirstLine = view.findViewById(R.id.first_vertical_line);
        firstRangeView = view.findViewById(R.id.first_range_view);
        secondLine = view.findViewById(R.id.second_line);
        secondRangeView = view.findViewById(R.id.second_range_view);
        verticalLine = view.findViewById(R.id.vertical_line);
        textview = view.findViewById(R.id.timeline_text);
        verticalLineExtension = view.findViewById(R.id.vertical_line_contd);
        addView(view);
    }

    public void setFirstRangeSelectablePercentage(double unselectableRatio){
        firstRangeView.setSelectableRangePercentage(unselectableRatio, RangeView.SelectableSide.RIGHT);
    }

    public void setSecondSelectablePercentage(double unselectableRatio){
        secondRangeView.setSelectableRangePercentage(unselectableRatio, RangeView.SelectableSide.RIGHT);
    }

    public void setItem(SelectableTimelineView.Item item, int count){

        mItem = item;

        updateLineVisibility(mItem.getPosition(),count);

        setupFirstListener(mItem);
        setupSecondListener(mItem);

        firstLine.setBackgroundColor(mItem.getFirstLineColor());
        verticalFirstLine.setBackgroundColor(mItem.getFirstLineColor());
        secondLine.setBackgroundColor(mItem.getSecondLineColor());

        if(item.getFirstLineColor() == unselectableColor) {
            firstRangeView.setBackgroundColor(ColorUtils.setAlphaComponent(unselectableColor, 80));
        }
        if(item.getSecondLineColor() == unselectableColor) {
            secondRangeView.setBackgroundColor(ColorUtils.setAlphaComponent(unselectableColor, 80));
        }

        verticalLine.setBackgroundColor(unselectableColor);
        verticalLineExtension.setBackgroundColor(unselectableColor);

        if(item.getFirstLineColor() == selectedColor || item.getSecondLineColor() == selectedColor){
            textview.setTypeface(null, Typeface.BOLD);
            textview.setTextColor(Color.BLACK);
        }else {
            textview.setTypeface(null, Typeface.NORMAL);
            textview.setTextColor(Color.GRAY);
        }

        String timeSuffix= "AM";
        if( item.getPosition()/12>0){
            timeSuffix = "PM";
        }

        switch (item.getPosition()){
            case 0:
            case 24:
                textview.setText("12AM");
                break;
            case 12:
                textview.setText("12PM");
                break;
            default:
                textview.setText(""+item.getPosition()%12+timeSuffix);
                break;
        }

    }

    public SelectableTimelineView.Item getItem() {
        return mItem;
    }

    public void setOnRangeSelectedListener(OnPointRangeStateChangeListener listener){
        mlistener = listener;
    }

    public boolean isFirstRangeSelected() {
        return firstRangeView.isRangeSelected();
    }

    public boolean isSecondRangeSelected() {
        return secondRangeView.isRangeSelected();
    }

    public void setCustomFirstStartTime(TimelineTime mCustomStartTime) {
        this.mCustomFirstStartTime = mCustomStartTime;
    }
    public void setCustomSecondStartTime(TimelineTime mCustomStartTime) {
        this.mCustomSecondStartTime = mCustomStartTime;
    }

    private void setupFirstListener(SelectableTimelineView.Item item){
        if(item.getFirstLineColor() == selectedColor) {
            firstRangeView.setOnClickListener(view -> {
                if (firstRangeView.isRangeSelected()) {
                    deselectFirstRange();
                }else {
                    selectFirstRange();
                }
            });

        }
    }

    private void setupSecondListener(SelectableTimelineView.Item item){
        if(item.getSecondLineColor() == selectedColor) {
            secondRangeView.setOnClickListener(view -> {
                if (secondRangeView.isRangeSelected()) {
                    deselectSecondRange();
                }else {

                    selectSecondRange();
                }
            });
        }
    }

    public void selectFirstRange(){

        verticalLine.setBackgroundColor(selectedColor);
        verticalLineExtension.setBackgroundColor(selectedColor);
        firstRangeView.select();

        if (mlistener != null) {
            mlistener.onRangeSelected(this,
                    mCustomFirstStartTime!=null ? mCustomFirstStartTime :new TimelineTime(mItem.getPosition() - 1, 30),
                    new TimelineTime(mItem.getPosition(), 0));
        }
    }

    public TimelineTime getSelectedStartTime(){
        TimelineTime time;
        if(firstRangeView.isRangeSelected())
            time = mCustomFirstStartTime!=null ? mCustomFirstStartTime :new TimelineTime(mItem.getPosition() - 1, 30);
        else time = mCustomSecondStartTime!=null ? mCustomSecondStartTime :new TimelineTime(mItem.getPosition(), 0);
        return time;
    }

    public TimelineTime getSelectedEndTime(){
        TimelineTime time;
        if(!secondRangeView.isRangeSelected())
            time = new TimelineTime(mItem.getPosition(), 0);
        else time = new TimelineTime(mItem.getPosition(), 30);
        return time;
    }

    public void selectSecondRange(){

        secondRangeView.select();
        verticalLine.setBackgroundColor(selectedColor);
        verticalLineExtension.setBackgroundColor(selectedColor);

        if (mlistener != null) {
            mlistener.onRangeSelected(this,
                    mCustomSecondStartTime!=null ? mCustomSecondStartTime :new TimelineTime(mItem.getPosition(), 0),
                    new TimelineTime(mItem.getPosition(), 30));
        }
    }



    public void deselectFirstRange(){
        firstRangeView.deselect();

        if(!secondRangeView.isRangeSelected()) {
            verticalLine.setBackgroundColor(unselectableColor);
            verticalLineExtension.setBackgroundColor(unselectableColor);
        }

        if (mlistener != null) {
            mlistener.onRangeDeselected(this,
                    mCustomFirstStartTime!=null ? mCustomFirstStartTime :new TimelineTime(mItem.getPosition() - 1, 30),
                    new TimelineTime(mItem.getPosition(), 0));
        }
    }

    public void deselectSecondRange(){

        secondRangeView.deselect();

        if(!firstRangeView.isRangeSelected()) {
            verticalLine.setBackgroundColor(unselectableColor);
            verticalLineExtension.setBackgroundColor(unselectableColor);
        }

        if (mlistener != null) {
            mlistener.onRangeDeselected(this,
                    mCustomSecondStartTime!=null ? mCustomSecondStartTime :new TimelineTime(mItem.getPosition(), 0),
                    new TimelineTime(mItem.getPosition(), 30));
        }

    }

    private void updateLineVisibility(int position, int count){
        if(position == 0){
            firstLine.setVisibility(View.GONE);
            verticalFirstLine.setVisibility(View.GONE);
            secondLine.setVisibility(View.VISIBLE);
        }else if (position == count-1){
            secondLine.setVisibility(View.GONE);
            verticalFirstLine.setVisibility(View.VISIBLE);
            firstLine.setVisibility(View.VISIBLE);
        }else {
            secondLine.setVisibility(View.VISIBLE);
            verticalFirstLine.setVisibility(View.VISIBLE);
            firstLine.setVisibility(View.VISIBLE);
        }
    }

    public void setSelectedColor(int color){

       selectedColor = color;
       firstRangeView.setSelectedColor(color);
       secondRangeView.setSelectedColor(color);
    }

    public void setUnselectableColor(int color){

        unselectableColor = color;

        if(mItem!= null && mItem.getFirstLineColor() == unselectableColor){
            firstRangeView.setDefaultColor(color);
        }
        if(mItem!= null && mItem.getSecondLineColor() == unselectableColor){
            secondRangeView.setDefaultColor(color);
        }

    }

    public void setDefaultColor(int color){

        defaultColor = color;

        if(mItem!= null && mItem.getFirstLineColor() == defaultColor){
            firstRangeView.setDefaultColor(color);
        }
        if(mItem!= null && mItem.getSecondLineColor() == defaultColor){
            secondRangeView.setDefaultColor(color);
        }
    }

    public interface OnPointRangeStateChangeListener{
        void onRangeSelected(SelectableTimelinePoint point, TimelineTime from, TimelineTime to);
        void onRangeDeselected(SelectableTimelinePoint point, TimelineTime from, TimelineTime to);
    }


}
