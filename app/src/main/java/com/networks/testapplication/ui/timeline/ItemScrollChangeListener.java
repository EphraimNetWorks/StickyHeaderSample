package com.networks.testapplication.ui.timeline;

import android.util.Pair;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

public interface ItemScrollChangeListener {

    public Pair<LifecycleOwner, LiveData<Integer>> getScrollPosition();

    public void onItemScrolled(int newPosition);

}
