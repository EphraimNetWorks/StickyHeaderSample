package com.networks.testapplication.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.networks.testapplication.R;

public class RangeView extends FrameLayout {


    private RelativeLayout parent;
    private View selectableView;


    private int mSelectedColor = Color.GREEN;
    private int mDefaultColor = Color.TRANSPARENT;

    private boolean isSelected = false;

    public RangeView(Context context){
        super(context);
        initView(context);
    }

    public RangeView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        View view = View.inflate(context, R.layout.range_view, null);

        parent = view.findViewById(R.id.range_layout);
        selectableView = view.findViewById(R.id.selectable_range);

        parent.setBackgroundColor(mDefaultColor);
        addView(view);
    }

    public void setSelectableRangePercentage(double unselectableRatio, SelectableSide selectableSide) {

        float measuredWidth = convertDpToPixel(40f, getContext());
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) selectableView.getLayoutParams();
        if(selectableSide == SelectableSide.RIGHT) {
            layoutParams.setMarginEnd(0);
            layoutParams.setMarginStart((int) (measuredWidth * unselectableRatio));
        }else {
            layoutParams.setMarginStart(0);
            layoutParams.setMarginEnd((int) (measuredWidth * unselectableRatio));
        }

        selectableView.setLayoutParams(layoutParams);
    }

    public void select(){
        selectableView.setBackgroundColor(ColorUtils.setAlphaComponent(mSelectedColor, 50));
        isSelected = true;
    }

    public void deselect(){
        selectableView.setBackgroundColor(mDefaultColor);
        isSelected = false;
    }

    public void setSelectedColor(int color){

       mSelectedColor = color;
       if(isSelected){
           select();
       }
    }

    public boolean isRangeSelected(){
        return isSelected;
    }

    public void setDefaultColor(int color){

        mDefaultColor = color;
        if(!isSelected){
            deselect();
        }
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    enum SelectableSide{
        RIGHT, LEFT
    }

}
