package com.networks.testapplication.ui.upcoming_events;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.networks.testapplication.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

public class DateDecorator implements DayViewDecorator {

    private Context context;

    public DateDecorator(Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.my_date_bg_color);
        view.setBackgroundDrawable(drawable);
    }
}
