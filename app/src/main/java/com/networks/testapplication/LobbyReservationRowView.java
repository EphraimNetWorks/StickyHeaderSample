package com.networks.testapplication;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class LobbyReservationRowView extends FrameLayout implements HorizontalScrollView.OnScrollChangeListener {

    private LobbyReservationHSVListener hsvUpdateListener;

    public LobbyReservationRowView(Context context) {
        super(context);
        initView();
    }

    public LobbyReservationRowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LobbyReservationRowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.list_item, null);
        HorizontalScrollView hsv = view.findViewById(R.id.horizontal_timebar_view);
        hsv.setOnScrollChangeListener(this);
        addView(view);
    }


    public void setHSVUpdateListener(LobbyReservationHSVListener hsvUpdateListener){
        this.hsvUpdateListener = hsvUpdateListener;
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        hsvUpdateListener.updateScrollPosition(scrollX);
    }
}
