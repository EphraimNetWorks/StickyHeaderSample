package com.networks.testapplication.ui.adapters_viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.networks.testapplication.R;
import com.networks.testapplication.utils.NetworkState;
import com.networks.testapplication.utils.NetworkStateCallback;
import com.networks.testapplication.utils.RefreshCallback;
import com.networks.testapplication.utils.Status;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NetworkStateViewHolder extends BaseViewHolder {

    @BindView(R.id.error_message_body_textView)
    TextView errorMessageBodyTextView;
    @BindView(R.id.error_message_title_textView)
    TextView errorMessageTitleTextView;

    @BindView(R.id.retry_loading_button)
    Button retryLoadingButton;

    @BindView(R.id.loading_progress_bar)
    ProgressBar loadingProgressBar;

    @BindView(R.id.network_state_layout)
    FrameLayout networkStateLayout;

    @BindView(R.id.network_state_imageview)
    ImageView networkStateImageview;
    
    Context mContext = itemView.getContext();
    private NetworkStateCallback networkCallback;

    private NetworkStateViewHolder(View itemView, RefreshCallback refreshCallback, NetworkStateCallback networkCallback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.networkCallback = networkCallback;
        retryLoadingButton.setOnClickListener(v -> refreshCallback.refreshList());
    }

    @Override
    void onBind(int position) {
        super.onBind(position);

        NetworkState networkState = networkCallback.getNetworkState();

        switch (networkState.getStatus()){
            case SUCCESS:

                setLayoutHeight(networkState.isResponseEmpty());



                if(networkState.isResponseEmpty()) {

                    retryLoadingButton.setVisibility(View.GONE);
                    errorMessageTitleTextView.setVisibility(View.VISIBLE);
                    errorMessageBodyTextView.setVisibility(View.VISIBLE);

                    networkStateImageview.setImageResource(networkState.getIconResId());
                    errorMessageTitleTextView.setText(mContext.getString(networkState.getMessageResId()));
                    errorMessageBodyTextView.setText(mContext.getString(networkState.getMessageBodyResId()));

                }else {

                    retryLoadingButton.setVisibility(View.GONE);
                    networkStateImageview.setVisibility(View.GONE);
                    errorMessageTitleTextView.setVisibility(View.GONE);
                    errorMessageBodyTextView.setVisibility(View.GONE);

                }

                break;
            case FAILED:

                setLayoutHeight(networkState.isResponseEmpty());

                retryLoadingButton.setVisibility(View.VISIBLE);
                networkStateImageview.setVisibility(View.VISIBLE);
                errorMessageTitleTextView.setVisibility(View.VISIBLE);
                errorMessageBodyTextView.setVisibility(View.VISIBLE);

                networkStateImageview.setImageResource(networkState.getIconResId());
                errorMessageTitleTextView.setText(mContext.getString(networkState.getMessageResId()));
                errorMessageBodyTextView.setText(mContext.getString(networkState.getMessageBodyResId()));

                break;
            case RUNNING:

                setLayoutHeight(networkState.isResponseEmpty());


                retryLoadingButton.setVisibility(View.GONE);
                networkStateImageview.setVisibility(View.GONE);
                errorMessageTitleTextView.setVisibility(View.GONE);
                errorMessageBodyTextView.setVisibility(View.GONE);

                break;
        }

        //loading
        loadingProgressBar.setVisibility(networkState.getStatus() == Status.RUNNING ? View.VISIBLE : View.GONE);

    }

    /*
    * Enlarge layout to fill screen if response is empty
     */
    private void setLayoutHeight(boolean isResponseEmpty){

        if(isResponseEmpty) {

            ViewGroup.LayoutParams newLayoutParams = networkStateLayout.getLayoutParams();
            newLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            networkStateLayout.setLayoutParams(newLayoutParams);

        }else {

            ViewGroup.LayoutParams newLayoutParams = networkStateLayout.getLayoutParams();
            newLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            networkStateLayout.setLayoutParams(newLayoutParams);

        }
    }

    public static NetworkStateViewHolder create(ViewGroup parent, RefreshCallback refreshCallback, NetworkStateCallback networkCallback) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_network_state, parent, false);
        return new NetworkStateViewHolder(view, refreshCallback, networkCallback);
    }

    @Override
    protected void clear() {

    }
}