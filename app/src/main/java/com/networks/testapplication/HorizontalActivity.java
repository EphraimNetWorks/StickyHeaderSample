package com.networks.testapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class HorizontalActivity extends AppCompatActivity implements
        LobbyReservationHSVListener {
    LinearLayout container;
    MutableLiveData<Integer> hsvPosition = new MutableLiveData<Integer>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal);

        setUpView();
    }

    public void setUpView(){
        container = findViewById(R.id.container);

        for (int i=0; i<5; i++){
            LobbyReservationRowView view = new LobbyReservationRowView(this);
            view.setHSVUpdateListener(this);
            container.addView(view);
        }

        hsvPosition.observe(this,new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                for(int i=0; i<container.getChildCount();i++){
                    HorizontalScrollView hsv = container.getChildAt(i).findViewById(R.id.horizontal_timebar_view);
                    hsv.smoothScrollTo(integer,0);
                }
            }
        });
    }

    @Override
    public void updateScrollPosition(int scrollX) {
        hsvPosition.setValue(scrollX);
    }



}
