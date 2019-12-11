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
import com.networks.testapplication.data.UpcomingGuest;
import com.networks.testapplication.utils.EmptyHeaderViewHolder;
import com.networks.testapplication.utils.EmptyHeaderDataImpl;
import com.networks.testapplication.utils.NetworkState;
import com.networks.testapplication.utils.NetworkStateCallback;
import com.networks.testapplication.utils.RefreshCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class UpcomingGuestListAdapter extends StickyHeaderAdapter<UpcomingGuest, HeaderDataImpl>
        implements RecyclerDataCallback<UpcomingGuest>, RefreshCallback, NetworkStateCallback {

    private UpcomingGuestListAdapter.Callback mCallback;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;



    public UpcomingGuestListAdapter(UpcomingGuestListAdapter.Callback callback) {
        mCallback = callback;
    }

    @Override
    public UpcomingGuest getItemDataInPosition(int position) {

        return getDataInPosition(position);
    }

    @Override
    public NetworkState getNetworkState() {
        return mNetworkState;
    }

    @Override
    public void refreshList() {
        mCallback.refreshGuestList();
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
                return new MyViewHolder(itemView,this,mCallback);

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
            headerTitleTextView.setText(getHeaderDataInPosition(headerPosition).getHeaderTitle());
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
        void onItemClick(String guestFirstName, String guestLastName,String guestEmail);
        public void showDialog(int title, int body, int positiveText, DialogInterface.OnClickListener positiveListener, int negativeText, DialogInterface.OnClickListener negativeListener, Object o);
        void refreshGuestList();

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

            HeaderDataImpl headerData = getHeaderDataInPosition(position);
            messageTextView.setText(headerData.getHeaderTitle());
        }

        @Override
        protected void clear() {

        }

    }

}
