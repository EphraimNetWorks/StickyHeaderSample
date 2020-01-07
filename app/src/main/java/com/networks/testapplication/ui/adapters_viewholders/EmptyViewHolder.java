package com.networks.testapplication.ui.adapters_viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.networks.testapplication.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmptyViewHolder extends BaseViewHolder {

    @BindView(R.id.empty_results_textview)
    TextView emptyMessageTextView;

    private String emptyMessage;

    private EmptyViewHolder(View itemView, String emptyMessage) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.emptyMessage = emptyMessage;
    }

    @Override
    void onBind(int position) {
        super.onBind(position);

        //empty message
        emptyMessageTextView.setText(emptyMessage);


    }

    public static EmptyViewHolder create(ViewGroup parent, String emptyMessage) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_empty, parent, false);
        return new EmptyViewHolder(view, emptyMessage);
    }

    @Override
    protected void clear() {

    }
}