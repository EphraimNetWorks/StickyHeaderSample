package com.networks.testapplication.final_sample;

import android.content.DialogInterface;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.networks.testapplication.data.UpcomingEvent;

import java.util.ArrayList;
import java.util.List;

public class UpcomingEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerDataCallback<UpcomingEvent> {

    private UpcomingEventsAdapter.Callback mCallback;

    private ArrayList<UpcomingEvent> mUpcomingEvents = new ArrayList<>();

    public UpcomingEventsAdapter(UpcomingEventsAdapter.Callback callback) {
        mCallback = callback;
    }

    @Override
    public UpcomingEvent getItemDataInPosition(int position) {

        return mUpcomingEvents.get(position);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return  UpcomingEventViewHolder.create(parent,this,mCallback);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseViewHolder) {
            ((BaseViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        return mUpcomingEvents.size();
    }


    public void submitNewList(List<UpcomingEvent> newEvents){
        clear();
        mUpcomingEvents.addAll(newEvents);
        notifyItemRangeInserted(0,newEvents.size());
    }

    public void clear() {

        int oldItemsSize = mUpcomingEvents.size();
        mUpcomingEvents.clear();
        notifyItemRangeRemoved(0,oldItemsSize);
    }

    public interface Callback {
        void onItemClick(String eventId);
        public void showDialog(int title, int body, int positiveText, DialogInterface.OnClickListener positiveListener, int negativeText, DialogInterface.OnClickListener negativeListener, Object o);
        void refreshEventList();

    }


}
