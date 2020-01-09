package com.networks.testapplication.ui.adapters_viewholders;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.networks.testapplication.R;
import com.networks.testapplication.data.UpcomingEvent;
import com.networks.testapplication.utils.EmptyHeaderViewHolder;
import com.networks.testapplication.utils.EmptyHeaderDataImpl;
import com.networks.testapplication.utils.NetworkState;
import com.networks.testapplication.utils.NetworkStateCallback;
import com.networks.testapplication.utils.RefreshCallback;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class UpcomingEventListAdapter extends StickyHeaderAdapter<UpcomingEvent, DateHeaderDataImpl>
        implements RecyclerDataCallback<UpcomingEvent>, RefreshCallback, NetworkStateCallback {

    private UpcomingEventListAdapter.Callback mCallback;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;



    public UpcomingEventListAdapter(UpcomingEventListAdapter.Callback callback) {
        mCallback = callback;
    }

    @Override
    public UpcomingEvent getItemDataInPosition(int position) {

        return getDataInPosition(position);
    }

    @Override
    public void refreshList() {
        mCallback.refreshEventList();
    }

    @Override
    public NetworkState getNetworkState() {
        return mNetworkState;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            //empty header for empty view holder
            case EmptyHeaderDataImpl.EMPTY_HEADER:
                return new EmptyHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_sticky_header, parent, false));
            case HeaderDataImpl.HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticky_header, parent, false));

            case VIEW_TYPE_NORMAL:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_upcoming_guest, parent, false);
                Timber.d("normal view");
                return new UpcomingEventViewHolder(itemView,this,mCallback);

            case VIEW_TYPE_EMPTY:
                Timber.d("empty view");
                return NetworkStateViewHolder.create(parent,this, this);

            default:
                throw new IllegalArgumentException("Unknown view type: "+viewType);
        }
    }

    @Override
    public long getItemId(int position) {
        Timber.d("item id for eventlist adapter : %s",position);
        return position;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseViewHolder) {
            ((BaseViewHolder) holder).onBind(position);
        }
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        // this method is called when your header move and you must not only bind header data in HeaderViewHolder
        //but also bind header data here.

        if (!(hasExtraRow() && headerPosition == getItemCount() - 2)) {
            TextView headerTitleTextView = header.findViewById(R.id.sticky_header_title);
            DateHeaderDataImpl headerData = getHeaderDataInPosition(headerPosition);
            String dateString = headerData.getHeaderDate().format(DateTimeFormatter.ofPattern("EEEE MMM dd"));
            headerTitleTextView.setText(dateString.toUpperCase());
            mCallback.onNewHeaderAttached(headerData.getHeaderDate());
        }

    }

    @Override
    protected int getViewType(int pos) {
        if (hasExtraRow() && pos == getItemCount() - 1) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public interface Callback {
        void onItemClick(String eventId);
        public void showDialog(int title, int body, int positiveText, DialogInterface.OnClickListener positiveListener, int negativeText, DialogInterface.OnClickListener negativeListener, Object o);
        void refreshEventList();
        void onNewHeaderAttached(LocalDate date);

    }

    class HeaderViewHolder extends BaseViewHolder {

        @Nullable
        @BindView(R.id.sticky_header_title)
        TextView messageTextView;


        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }

        @Override
        void onBind(int position) {
            super.onBind(position);

            DateHeaderDataImpl headerData = getHeaderDataInPosition(position);
            String dateString = headerData.getHeaderDate().format(DateTimeFormatter.ofPattern("EEEE MMM dd"));
            messageTextView.setText(dateString.toUpperCase());
        }

        @Override
        protected void clear() {

        }

    }

}
