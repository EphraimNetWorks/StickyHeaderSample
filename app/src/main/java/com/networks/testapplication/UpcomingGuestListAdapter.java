package com.networks.testapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.saber.stickyheader.stickyView.StickHeaderRecyclerView;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class UpcomingGuestListAdapter extends StickHeaderRecyclerView<UpcomingGuest, HeaderDataImpl>
        implements RecyclerDataCallback<UpcomingGuest>{

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

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case HeaderDataImpl.HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticky_header, parent, false));

            case VIEW_TYPE_NORMAL:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_upcoming_guest, parent, false);
                Timber.d("normal view");
                return new MyViewHolder(itemView,this,mCallback);

            case VIEW_TYPE_EMPTY:
                Timber.d("empty view");
                if (getItemCount() > 1) {
                    return new EmptyViewHolder(
                            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skeleton_upcoming_guests, parent, false),
                            mCallback
                    );
                } else {
                    return new EmptyViewHolder(
                            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_upcoming_guest_view, parent, false),
                            mCallback
                    );
                }

            default:
                EmptyViewHolder emptyViewHolder = new EmptyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skeleton_upcoming_guests, parent, false), mCallback);
                emptyViewHolder.setIsRecyclable(false);
                return emptyViewHolder;
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

        TextView headerTitleTextView = header.findViewById(R.id.sticky_header_title);
        headerTitleTextView.setText(getHeaderDataInPosition(headerPosition).getHeaderTitle());

    }

    @Override
    protected int getViewType(int pos) {
        if (getItemCount() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
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
