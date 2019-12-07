package com.networks.testapplication;


import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class EmptyViewHolder extends BaseViewHolder {

    @Nullable
    @BindView(R.id.tv_message)
    TextView messageTextView;

    @Nullable
    @BindView(R.id.linearLayoutView)
    LinearLayout mLinearLayoutView;

    @Nullable
    @BindView(R.id.tv_message_title)
    TextView messageTextViewTitle;

    @Nullable
    @BindView(R.id.imageViewEmpty)
    ImageView mImageView;

    @Nullable
    @BindView(R.id.network_retry)
    AppCompatButton mNetworkRetry;


    public EmptyViewHolder(@NonNull View itemView,
                           UpcomingGuestListAdapter.Callback mCallback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Timber.d("empty view holder inside eventlist adapter");
        new Handler().postDelayed(() -> {
            mLinearLayoutView.setVisibility(View.VISIBLE);
            if(!NetworkUtils.INSTANCE.isNetworkConnected(itemView.getContext())){
                messageTextView.setText(R.string.no_internet_body);
                messageTextViewTitle.setText(R.string.no_internet);
                mImageView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_no_internet));
                mImageView.getLayoutParams().height = (int) itemView.getContext().getResources().getDimension(R.dimen.image_view_height);
                mImageView.getLayoutParams().width = (int) itemView.getContext().getResources().getDimension(R.dimen.image_view_width);
                mImageView.requestLayout();
                Timber.d("network not connected");
                mNetworkRetry.setVisibility(View.VISIBLE);
                Timber.d("network connection lost");
                mNetworkRetry.setOnClickListener(view -> mCallback.refreshGuestList());
            } else {
                messageTextView.setText(R.string.no_upcoming_guests_registered_text);
                messageTextViewTitle.setText(R.string.no_upcoming_guests);
                mImageView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_guests));
                mNetworkRetry.setVisibility(View.GONE);

            }
        }, 500);

    }

    @Override
    protected void clear() {

    }

}
