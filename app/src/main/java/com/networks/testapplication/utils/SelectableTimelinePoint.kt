package com.networks.testapplication.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import com.networks.testapplication.R

class SelectableTimelinePoint : FrameLayout {

    private lateinit var firstLine: View
    private lateinit var verticalFirstLine: View
    lateinit var firstRangeView: RangeView
        private set
    private lateinit var secondLine: View
    private lateinit var secondRangeView: RangeView
    lateinit var verticalLine: View
        private set
    private lateinit var verticalLineExtension: View
    private lateinit var textview: TextView
    private var selectedColor = Color.GREEN
    private var unselectableColor = Color.LTGRAY
    var item: Item?= null
        private set

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
        val view =
            View.inflate(context, R.layout.selectable_timeline_point, null)
        firstLine = view.findViewById(R.id.first_line)
        verticalFirstLine = view.findViewById(R.id.first_vertical_line)
        firstRangeView = view.findViewById(R.id.first_range_view)
        secondLine = view.findViewById(R.id.second_line)
        secondRangeView = view.findViewById(R.id.second_range_view)
        verticalLine = view.findViewById(R.id.vertical_line)
        textview = view.findViewById(R.id.timeline_text)
        verticalLineExtension = view.findViewById(R.id.vertical_line_contd)
        addView(view)
    }

    fun setItem(
        item: Item,
        count: Int
    ) {
        this.item = item
        updateLineVisibility(item.position, count)

        //set line and range view colors
        firstLine.setBackgroundColor(item.firstLineColor)
        verticalFirstLine.setBackgroundColor(item.firstLineColor)
        secondLine.setBackgroundColor(item.secondLineColor)
        if (item.firstLineColor == unselectableColor) {
            firstRangeView.setBackgroundColor(ColorUtils.setAlphaComponent(unselectableColor, 80))
        }
        if (item.secondLineColor == unselectableColor) {
            secondRangeView.setBackgroundColor(
                ColorUtils.setAlphaComponent(
                    unselectableColor,
                    80
                )
            )
        }

        if (item.firstLineColor == unselectableColor || item.secondLineColor == unselectableColor) {
            textview.setTypeface(null, Typeface.NORMAL)
            textview.setTextColor(Color.GRAY)
            verticalLine.setBackgroundColor(unselectableColor)
            verticalLineExtension.setBackgroundColor(unselectableColor)
        } else {
            textview.setTypeface(null, Typeface.BOLD)
            textview.setTextColor(Color.BLACK)
            verticalLine.setBackgroundColor(selectedColor)
            verticalLineExtension.setBackgroundColor(selectedColor)
        }

        //set time text
        var timeSuffix = "AM"
        if (item.position / 12 > 0) {
            timeSuffix = "PM"
        }
        when (item.position) {
            0, 24 -> textview.text = "12AM"
            12 -> textview.text = "12PM"
            else -> textview.text = "" + item.position % 12 + timeSuffix
        }

        setRangeViewTime()
    }

    private fun setRangeViewTime() {
        // set time for each rangeview in selectable point
        if(item!!.position>0) {
            firstRangeView.timeRange = TimelineRange(
                TimelineTime(
                    item!!.position - 1,
                    30
                ), TimelineTime(item!!.position, 0)
            )
        }

        if(item!!.position<24) {
            secondRangeView.timeRange = TimelineRange(
                TimelineTime(
                    item!!.position,
                    0
                ), TimelineTime(item!!.position, 30)
            )
        }

    }

    private fun updateLineVisibility(position: Int, count: Int) {
        when (position) {
            0 -> {
                firstLine.visibility = View.GONE
                verticalFirstLine.visibility = View.GONE
                secondLine.visibility = View.VISIBLE
            }
            count - 1 -> {
                secondLine.visibility = View.GONE
                verticalFirstLine.visibility = View.VISIBLE
                firstLine.visibility = View.VISIBLE
            }
            else -> {
                secondLine.visibility = View.VISIBLE
                verticalFirstLine.visibility = View.VISIBLE
                firstLine.visibility = View.VISIBLE
            }
        }
    }

    fun setUnselectableColor(color: Int) {
        unselectableColor = color
        firstRangeView.setUnselectedColor(color)
        secondRangeView.setUnselectedColor(color)
    }

    fun setSelectableColor(color: Int) {
        selectedColor = color
    }

    class Item(var firstLineColor: Int, var secondLineColor: Int, val position: Int)
}