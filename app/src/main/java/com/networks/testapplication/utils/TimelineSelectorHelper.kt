package com.networks.testapplication.utils


import android.content.ClipData
import android.graphics.Rect
import android.os.Build
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.new_timeline_view.view.*
import kotlin.math.roundToInt

class TimelineSelectorHelper(timelineContainer:RelativeLayout,
                             private val timelineLinearLayout: LinearLayout,
                             timelineScrollView: ObservableHorizontalScrollView,
                             private val selectorStartPoint: View,
                             private val selectorMid:View,
                             private val selectorEndPoint: FrameLayout,
                             private val rangeChangedListener: ()->OnRangeStateChangeListener?) {

    var selectedStartTime:TimelineTime? = null
        private set
    var selectedEndTime:TimelineTime? = null
        private set

    init {

        selectorEndPoint.setOnTouchListener(CustomTouchListener(SelectorViewLabel.END))
        selectorMid.setOnTouchListener(CustomTouchListener(SelectorViewLabel.MID))
        selectorStartPoint.setOnTouchListener(CustomTouchListener(SelectorViewLabel.START))
        timelineContainer.setOnDragListener(CustomDragListener(timelineScrollView))

    }

    fun selectRange(range:TimelineRange){
        setSelectorStartPoint(getRangeViewX(range.startTime))

        val endX = getRangeViewX(range.endTime)-(selectorEndPoint.selector_end_background.width/2)
        setSelectorEndPoint(endX)

        snapSelectorStart()

        selectedStartTime = range.startTime
        selectedEndTime = range.endTime

        rangeChangedListener.invoke()?.onSelectedRangeChanged(selectedStartTime!!, selectedEndTime!!)

    }

    private fun getRangeViewX(time:TimelineTime):Float{

        // calculate x coordinate of given time
        val verticalLineWidth =
            (timelineLinearLayout.getChildAt(0) as SelectableTimelinePoint).verticalLine.width
                .toFloat()
        val rangeSize =
            (timelineLinearLayout.getChildAt(0) as SelectableTimelinePoint).firstRangeView.width
                .toFloat()
        val firstRangeStart =
            (timelineLinearLayout.getChildAt(0) as SelectableTimelinePoint)
                .verticalLine.x
        val hourMargin =
            time.hour * 2 * rangeSize + time.hour * verticalLineWidth
        val minute30 =
            if (time.minute <= 30) time.minute.toFloat() else time.minute - 30.toFloat()
        var minuteMargin = minute30 / 30 * rangeSize
        if (time.minute > 30) minuteMargin += rangeSize
        val margin = hourMargin + minuteMargin

        return margin+firstRangeStart
    }

    fun moveToRangeViewPosition(rangeView: RangeView){
        val startX = rangeView.selectableTimelinePoint.x+rangeView.x
        setSelectorStartPoint(startX)
        snapSelectorStart()
    }


    private fun setSelectorStartPoint(newStartPosition: Float){


        val firstRangeStart = getStartRangeViewX()

        val startPosition = if(newStartPosition<firstRangeStart){
            firstRangeStart
        }else{
            newStartPosition
        }

        selectorStartPoint.animate()
            .x(startPosition)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(0)
            .start()

        var endPosition = startPosition + selectorStartPoint.measuredWidth + selectorMid.measuredWidth

        val lastRangeEnd = getEndRangeViewX()
        if(endPosition>lastRangeEnd) {
            endPosition = lastRangeEnd - selectorEndPoint.selector_end_background.width
            val newMidWidth = (endPosition-(startPosition+selectorStartPoint.width)).roundToInt()
            selectorMid.updateLayoutParams<RelativeLayout.LayoutParams> {
                width = newMidWidth
            }
        }

        selectorMid.animate()
            .x(startPosition + selectorStartPoint.measuredWidth)
            .setDuration(0)
            .start()


        selectorEndPoint.animate()
            .x(endPosition)
            .setDuration(0)
            .start()

    }

    private fun setSelectorEndPoint(newEndPosition: Float){

        val selectorStartRight = selectorStartPoint.x + selectorStartPoint.width
        val lastRangeEnd = getEndRangeViewX()

        //replace new position if overlaps with start position
        val endPosition = when {
            newEndPosition>lastRangeEnd -> {
                lastRangeEnd
            }
            newEndPosition>selectorStartRight -> {
                newEndPosition
            }
            else -> selectorStartRight+selectorEndPoint.selector_end_background.width-1
        }

        val adjustedEndPosition = endPosition-(selectorEndPoint.selector_end_circle.width
                - selectorEndPoint.selector_end_background.width)

        //adjust selector end view to new position
        selectorEndPoint.animate()
            .x(adjustedEndPosition)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(0)
            .start()

        val selectorMidRight = selectorMid.x + selectorMid.measuredWidth
        val widthDifference = selectorEndPoint.x - selectorMidRight

        // update selector mid to fill space between start and end
        selectorMid.updateLayoutParams<RelativeLayout.LayoutParams> {
            width = selectorMid.width + widthDifference.toInt()
        }


    }

    private fun snapSelectorStart(){
        //calculate and snap selector's start to start of rangeview
        val snapY = timelineLinearLayout.y+timelineLinearLayout.measuredHeight-1
        val startPointRangePair = timelineLinearLayout.getPointAndRange(selectorStartPoint.x,snapY)!!

        val startRangeBounds = startPointRangePair.second.getChildXYBounds(startPointRangePair.first.x,
            startPointRangePair.first.y)
        setSelectorStartPoint(startRangeBounds.left.toFloat())

        //set new start time after snap
        selectedStartTime = startPointRangePair.second.timeRange.startTime

        val selectorEndBg = selectorEndPoint.selector_end_background
        var endX = selectorEndPoint.x + selectorEndBg.width
        val endRangeViewX = getEndRangeViewX()
        if (endX> endRangeViewX) {
            endX = endRangeViewX
        }
        val endPointRangePair = timelineLinearLayout.getPointAndRange(endX,snapY)!!

        //set new end time after snap
        selectedEndTime = endPointRangePair.second.timeRange.startTime

        //send updates on new selected time changes
        if(selectedEndTime!=selectedStartTime) {
            rangeChangedListener.invoke()
                ?.onSelectedRangeChanged(selectedStartTime!!, selectedEndTime!!)
        }

    }

    private fun getStartRangeViewX():Float{
        val firstRangeStart =
            (timelineLinearLayout.getChildAt(0) as SelectableTimelinePoint)
                .verticalLine.x
        return firstRangeStart
    }

    private fun getEndRangeViewX():Float{
        val lastSTP = timelineLinearLayout.getChildAt(timelineLinearLayout.childCount-1)as SelectableTimelinePoint
        return lastSTP.x + lastSTP.firstRangeView.width
    }

    private fun snapSelectorEnd(){

        //calculate and snap selector's end to end of rangeview
        val snapY = timelineLinearLayout.y+timelineLinearLayout.measuredHeight-1
        val selectorEndBg = selectorEndPoint.selector_end_background
        val endX = selectorEndPoint.x + selectorEndBg.width
        val endPointRangePair = timelineLinearLayout.getPointAndRange(endX,snapY)!!

        val endRangeBounds = endPointRangePair.second.getChildXYBounds(endPointRangePair.first.x,
            endPointRangePair.first.y)
        setSelectorEndPoint(endRangeBounds.right.toFloat())

        //set new end time after snap
        selectedEndTime = endPointRangePair.second.timeRange.endTime

        //send updates on new selected time changes
        if(selectedStartTime!= null && selectedEndTime!=null) {
            rangeChangedListener.invoke()?.onSelectedRangeChanged(selectedStartTime!!,selectedEndTime!!)
        }
    }

    inner class CustomDragListener(private var scrollview:ObservableHorizontalScrollView): View.OnDragListener{

        override fun onDrag(view: View, event: DragEvent): Boolean {
            when(event.action){
                DragEvent.ACTION_DRAG_ENTERED->{

                }
                DragEvent.ACTION_DRAG_STARTED->{
                    scrollview.setIsScrollable(false)
                }
                DragEvent.ACTION_DRAG_LOCATION->{

                    //update selector view position on drag
                    val viewLabel = event.localState as SelectorViewLabel
                    if(viewLabel == SelectorViewLabel.END){
                        setSelectorEndPoint(event.x)
                    }else {
                        setSelectorStartPoint(event.x)
                    }
                }
                DragEvent.ACTION_DRAG_ENDED->{
                    val viewLabel = event.localState as SelectorViewLabel
                    scrollview.setIsScrollable(true)

                    // snap selector to selected ranges
                    if(viewLabel == SelectorViewLabel.END){
                        snapSelectorEnd()
                    }else {
                        snapSelectorStart()
                    }

                }
                DragEvent.ACTION_DRAG_EXITED->{

                }
            }
            return true
        }
    }

    inner class CustomTouchListener(private val viewLabel:SelectorViewLabel): View.OnTouchListener{


        override fun onTouch(view: View, event: MotionEvent): Boolean {

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val data = ClipData.newPlainText("", "")

                    val shadowBuilder = View.DragShadowBuilder(
                        selectorEndPoint.selector_end_circle
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        view.startDragAndDrop(data,shadowBuilder, viewLabel,0)
                    }else{
                        view.startDrag(data,shadowBuilder, viewLabel,0)
                    }
                }

                else -> return true
            }
            return true
        }
    }
    
    enum class SelectorViewLabel{
        START,MID,END
    }

    private fun LinearLayout.getPointAndRange(x: Float, y: Float) : Pair<SelectableTimelinePoint,RangeView>? {
        for(index in childCount-1 downTo 0){
            val child = this.getChildAt(index)
            val bounds = child.getChildXYBounds(this.x,this.y)

            if (bounds.contains(x.roundToInt(), y.roundToInt())) {
                return Pair(child as SelectableTimelinePoint, child.getViewByCoordinates(x,y) as RangeView)
            }
        }

        return null
    }

    private fun SelectableTimelinePoint.getViewByCoordinates(x: Float, y: Float) : RangeView? {

        val container = (getChildAt(0) as FrameLayout).getChildAt(0) as ConstraintLayout
        for(index in container.childCount-1 downTo 0){
            val child = container.getChildAt(index)
            val bounds = child.getChildXYBounds(this.x,this.y)
            if (bounds.contains(x.roundToInt(), y.roundToInt()) ) {
                if(child is RangeView) {
                    return child
                }
            }
        }

        return null
    }

    private fun View.getChildXYBounds(parentX:Float, parentY:Float): Rect {
        val rectPosition = intArrayOf(0,0)
        rectPosition[0] = (parentX+x).roundToInt()
        rectPosition[1] = (parentY+y).roundToInt()
        return Rect(rectPosition[0], rectPosition[1],
            rectPosition[0]+measuredWidth,
            rectPosition[1]+measuredHeight)
    }
}