package com.networks.testapplication.ui.timeline;

import android.graphics.Color;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.networks.testapplication.R;
import com.networks.testapplication.utils.NewTimelineView;
import com.networks.testapplication.utils.TimelineRange;
import com.networks.testapplication.utils.TimelineTime;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestRecyclerAdapter extends RecyclerView.Adapter<TestRecyclerAdapter.TestViewHolder> {

    private ItemScrollChangeListener mItemScrollChangeListener;
    public TestRecyclerAdapter(ItemScrollChangeListener itemScrollChangeListener){
        mItemScrollChangeListener = itemScrollChangeListener;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item, parent, false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        holder.bind(mItemScrollChangeListener);
    }

    @Override
    public int getItemCount() {
        return 30;
    }

    class TestViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.timeline_view)
        NewTimelineView mTimelineView;

        public TestViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }

        public void bind(ItemScrollChangeListener itemScrollChangeListener){

            mTimelineView.setDefaultLineColor(Color.LTGRAY);
            mTimelineView.setHighlightedLineColor(ContextCompat.getColor(itemView.getContext(),R.color.colorAccent));
            ArrayList<TimelineRange> ranges = new ArrayList<>();
            ranges.add(new TimelineRange(new TimelineTime(0,15), new TimelineTime(3,45)));

            Pair<LifecycleOwner, LiveData<Integer>> pair = itemScrollChangeListener.getScrollPosition();

            mTimelineView.setRanges(ranges, pair.second.getValue()==null);
            new Handler().post(()->{

                if (pair.second.getValue()!=null && mTimelineView.getScrollPosition() != pair.second.getValue())
                    mTimelineView.scrollTo(pair.second.getValue());
            });

            pair.second.observe(pair.first, scrollX -> {
                mTimelineView.scrollTo(scrollX);
            });


            mTimelineView.setOnScrollChangeListener(scrollX -> mItemScrollChangeListener.onItemScrolled(scrollX));
        }
    }

}
