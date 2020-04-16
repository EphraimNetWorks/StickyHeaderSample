package com.networks.testapplication.utils

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.graphics.ColorUtils
import com.networks.testapplication.R
import kotlinx.android.synthetic.main.new_timeline_view.view.*
import org.threeten.bp.LocalTime
import java.sql.Time
import java.util.*

class SelectableTimelineView @JvmOverloads constructor(context: Context,
                                                       attrs: AttributeSet? = null,
                                                       defStyleAttr: Int = 0,
                                                       defStyleRes: Int = 0):
    FrameLayout(context, attrs, defStyleAttr,defStyleRes), SelectableTimelinePoint.OnRangeClickListener{

    var selectedColor = Color.GREEN
        set(value) {
            selector_start_point.setBackgroundColor(ColorUtils.setAlphaComponent(value, 50))
            selector_mid.setBackgroundColor(ColorUtils.setAlphaComponent(value, 50))
            selector_end_background.setBackgroundColor(ColorUtils.setAlphaComponent(value, 50))
            field = value
        }
    var unselectableColor = Color.LTGRAY

    private lateinit var timelineLinearLayout: LinearLayout
    private lateinit var timelineContainer: RelativeLayout
    private lateinit var hsv: ObservableHorizontalScrollView

    private var mlistener: OnRangeStateChangeListener? = null

    var selectedRangeStartTime: TimelineTime? = null
        get() = selectorHelper.selectedStartTime
        private set

    var selectedRangeEndTime: TimelineTime? = null
        get() = selectorHelper.selectedEndTime
        private set

    private lateinit var selectorHelper: TimelineSelectorHelper

    init {
        initView(context)
        setUnselectableRanges(ArrayList(), false)
    }

    private fun initView(context: Context) {
        val view =
            View.inflate(context, R.layout.new_timeline_view, null)
        hsv = view.findViewById(R.id.timeline_hsv)
        timelineLinearLayout = view.findViewById(R.id.timeline_view_linear_layout)
        timelineContainer = view.findViewById(R.id.timeline_container)

        selectorHelper = TimelineSelectorHelper(timelineContainer,
            timelineLinearLayout,
            hsv,
            view.selector_start_point,
            view.selector_mid,
            view.selector_end_point,
            this::mlistener)

        addView(view)
    }

    fun setOnScrollChangeListener(scrollChangeListener: OnScrollListener) {
        hsv.onScrollListener = object :
            ObservableHorizontalScrollView.OnScrollListener {
            override fun onScrollChanged(
                scrollView: ObservableHorizontalScrollView?,
                x: Int,
                y: Int,
                oldX: Int,
                oldY: Int
            ) {
                scrollChangeListener.onScrollChange(x)
            }

            override fun onEndScroll(scrollView: ObservableHorizontalScrollView?) {}
        }
    }

    fun setUnselectableRanges( selectedRanges: List<TimelineRange>,scrollToCurrentTime:Boolean) {


        timelineLinearLayout.removeAllViews()
        for (hour in 0..24) {
            val item =
                SelectableTimelinePoint.Item(
                    selectedColor,
                    selectedColor,
                    hour
                )
            var containsFirst = false
            var containsSecond = false
            for (selectedRange in selectedRanges) {
                if (!containsFirst) {
                    containsFirst = selectedRange.containsFirst(hour)
                    if (containsFirst) {
                        item.firstLineColor = unselectableColor
                    }
                }
                if (!containsSecond) {
                    containsSecond = selectedRange.containsSecond(hour)
                    if (containsSecond) {
                        item.secondLineColor = unselectableColor
                    }
                }
                if (containsFirst && containsSecond) {
                    break
                }
            }

            val timelinePoint = SelectableTimelinePoint(context)
            timelinePoint.onRangeClickListener = this
            timelinePoint.setSelectableColor(selectedColor)
            timelinePoint.setUnselectableColor(unselectableColor)
            timelinePoint.setItem(item, 25)
            timelineLinearLayout.addView(timelinePoint)

        }

        timelineContainer.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                timelineContainer.viewTreeObserver
                    .removeOnGlobalLayoutListener(this)

                adjustCurrentTimeView()

                startTimeUpdates()

                selectDefaultRange(selectedRanges)

                if(scrollToCurrentTime) {
                    val time = LocalTime.now()
                    scrollToTime(TimelineTime(time.hour, time.minute))
                }
            }
        })
    }

    fun selectRange(range: TimelineRange){

        val intervalIncrement = 30

        val startTime = if(range.startTime.minute%intervalIncrement > 0){
            getApproximatedTime(intervalIncrement,range.startTime)
        }else{
            range.startTime
        }

        val endTime = if(range.endTime.minute%intervalIncrement > 0){
            getApproximatedTime(intervalIncrement,range.endTime)
        }else{
            range.endTime
        }

        selectorHelper.selectRange(TimelineRange(startTime, endTime))
    }

    private fun getApproximatedTime(intervalIncrement:Int, time: TimelineTime):TimelineTime{

        val approximatedHour =
            time.hour + if (time.minute + intervalIncrement > 60) 1 else 0
        val approximatedMinute =
            (time.minute + (intervalIncrement - time.minute % intervalIncrement)) % 60
        return TimelineTime(approximatedHour,approximatedMinute)
    }

    fun setOnRangeSelectedListener(listener: OnRangeStateChangeListener){
        mlistener = listener
    }

    override fun onRangeClick(rangeView: RangeView) {
        selectorHelper.moveToRangeViewPosition(rangeView)
    }

    private fun selectDefaultRange(unselectableRanges:List<TimelineRange>) {
        val startPoint = getApproximatedTime(30, LocalTime.now().toTimelineTime()).toLocalTime()

        var defaultTime = startPoint
        var hasSetDefaultTime = false

        do {
            var isSelectable = true
            for (range in unselectableRanges) {
                val time = defaultTime.toTimelineTime()

                if((range.startTime.isBefore(time)&& range.endTime.isAfter(time)) ||
                        range.startTime == time){
                    defaultTime = defaultTime.plusMinutes(30)
                    isSelectable = false
                    break
                }
            }
            if (isSelectable) hasSetDefaultTime = true
            if(startPoint == defaultTime) break
        } while(!hasSetDefaultTime)

        if(!hasSetDefaultTime){
            defaultTime = LocalTime.of(8,0)
        }

        var endTime = defaultTime.plusMinutes(30).toTimelineTime()

        if(defaultTime.hour>endTime.hour) endTime = TimelineTime(24,0)

        selectRange(TimelineRange(defaultTime.toTimelineTime(),endTime))

    }

    var updateCurrentTimeIndicator = true
    private val updateHandler = Handler()
    private fun startTimeUpdates() {
        updateHandler.postDelayed({
            if (updateCurrentTimeIndicator) {
                adjustCurrentTimeView()
                startTimeUpdates()
            }
        }, 60 * 1000.toLong())
    }

    var currentTimeView: View? = null
    fun adjustCurrentTimeView() {
        val time = LocalTime.now()
        val currentTime = TimelineTime(time.hour, time.minute)
        val rangeSize =
            (timelineLinearLayout.getChildAt(0) as SelectableTimelinePoint).firstRangeView.width
                .toFloat()
        if (currentTimeView == null) {
            currentTimeView =
                timelineContainer.findViewById(R.id.current_time_indicator)
        }
        val firstRangeStart =
            (timelineLinearLayout.getChildAt(0) as SelectableTimelinePoint)
                .verticalLine.x
        val hourMargin =
            currentTime.hour * 2 * rangeSize
        val minute30 =
            if (currentTime.minute <= 30) currentTime.minute.toFloat() else currentTime.minute - 30.toFloat()
        var minuteMargin = minute30 / 30 * rangeSize
        if (currentTime.minute > 30) minuteMargin += rangeSize
        val margin = (hourMargin + minuteMargin).toInt()
        currentTimeView!!.animate()
            .x(margin + firstRangeStart)
            .setDuration(0)
            .start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        updateCurrentTimeIndicator = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateCurrentTimeIndicator = true
    }

    fun scrollToTime(time: TimelineTime) {
        val offset = 5
        scrollTo(timelineLinearLayout.getChildAt(time.hour).left + offset)
    }

    fun scrollTo(position: Int) {
        hsv.smoothScrollTo(position, 50)
    }

    fun getScrollPosition(): Int {
        return hsv.scrollX
    }

    interface OnScrollListener {
        fun onScrollChange(scrollX: Int)
    }

    fun TimelineTime.toLocalTime():LocalTime{
        val localTimeHour = if(hour==24) 0 else hour
        return LocalTime.of(localTimeHour,minute)
    }

    fun LocalTime.toTimelineTime():TimelineTime{
        return TimelineTime(hour,minute)
    }

}