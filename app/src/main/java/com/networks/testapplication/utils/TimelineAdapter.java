package com.networks.testapplication.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.networks.testapplication.R;

import java.util.ArrayList;
import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private List<Item> viewHolderItems = new ArrayList<>();
    private int highlightedColor = Color.BLUE;
    private int defaultColor = Color.LTGRAY;

    public TimelineAdapter(List<TimelineRange> ranges){
        setRanges(ranges);
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_point, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        holder.bindTimelinePoint(position,getItemCount(), viewHolderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return viewHolderItems.size();
    }

    public void setHighlightedColor(int highlightedColor){
        this.highlightedColor = highlightedColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setRanges(List<TimelineRange> selectedRanges) {
        viewHolderItems.clear();
        for(int hour=0; hour<25; hour++){
            Item item = new Item(defaultColor, defaultColor);
            for (TimelineRange selectedRange: selectedRanges){
                Pair<Boolean,Boolean> containsPair = selectedRange.contains(hour);
                if(containsPair.first){
                    item.setFirstLineColor(highlightedColor);
                }
                if(containsPair.second){
                    item.setSecondLineColor(highlightedColor);
                }
                if(containsPair.first || containsPair.second){
                    break;
                }
            }
            viewHolderItems.add(item);
        }
        notifyDataSetChanged();
    }

    class TimelineViewHolder extends RecyclerView.ViewHolder {

        private View firstLine;
        private View secondLine;
        private View verticalLine;
        private TextView textview;

        public TimelineViewHolder(View view){
            super(view);
            firstLine = view.findViewById(R.id.first_line);
            secondLine = view.findViewById(R.id.second_line);
            verticalLine = view.findViewById(R.id.vertical_line);
            textview = view.findViewById(R.id.timeline_text);
        }

        public void bindTimelinePoint(int position,int count,Item item){

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

    }

    private class Item{
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
