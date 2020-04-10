package com.networks.testapplication.utils

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import com.networks.testapplication.R

class RangeView : FrameLayout {
    private lateinit var parent: ConstraintLayout
    private lateinit var selectableView: View
    private lateinit var unselectableView: View
    private var mDefaultColor = Color.TRANSPARENT
    private var mUnselectedColor = Color.GRAY

    lateinit var selectableTimelinePoint :SelectableTimelinePoint

    lateinit var timeRange :TimelineRange

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context)
    }

    private fun initView(context: Context) {
        val view = View.inflate(context, R.layout.range_view, null)
        parent = view.findViewById(R.id.range_layout)
        selectableView = view.findViewById(R.id.selectable_range)
        unselectableView = view.findViewById(R.id.unselectable_range)
        unselectableView.setBackgroundColor(mUnselectedColor)
        parent.setBackgroundColor(mDefaultColor)
        addView(view)
    }

    fun setUnselectedColor(color: Int) {
        mUnselectedColor = color
        unselectableView.setBackgroundColor(mUnselectedColor)
    }

}