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
import org.threeten.bp.LocalTime
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
        val rangeSize =
            (timelineLinearLayout.getChildAt(0) as SelectableTimelinePoint).firstRangeView.width
                .toFloat()
        val firstRangeStart =
            (timelineLinearLayout.getChildAt(0) as SelectableTimelinePoint)
                .verticalLine.x
        val hourMargin = time.hour * 2 * rangeSize
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


    private fun setSelectorStartPoint(newStartPosition: Float, isDragging:Boolean = false){

        val firstRangeStart = getStartRangeViewX()
        //selector end width not accounted for since lastRangeEnd accounts for it
        val selectorWidth = selectorStartPoint.width+selectorMid.width
        val lastRangeEnd = getEndRangeViewX()- selectorEndPoint.selector_end_background.width


        val startPosition = when {
            newStartPosition<firstRangeStart -> { // new position less than first range view position
                firstRangeStart
            }
            isDragging &&
                    newStartPosition+selectorWidth>=lastRangeEnd -> {// dragging has reached end
                lastRangeEnd-selectorWidth
            }
            else -> newStartPosition // new position
        }


        selectorStartPoint.animate()
            .x(startPosition)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(0)
            .start()

        var endPosition = startPosition + selectorStartPoint.measuredWidth + selectorMid.measuredWidth

        if(endPosition>lastRangeEnd) {
            endPosition = lastRangeEnd
            if(!isDragging){
                val newMidWidth = (endPosition-(startPosition+selectorStartPoint.width)).roundToInt()
                selectorMid.updateLayoutParams<RelativeLayout.LayoutParams> {
                    width = newMidWidth
                }
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

        val selectorStartRight =
            selectorStartPoint.x + selectorStartPoint.width+selectorEndPoint.selector_end_background.width+1

        val lastRangeEnd = getEndRangeViewX()

        //replace new position if overlaps with start position
        val endPosition = when {
            newEndPosition>lastRangeEnd -> {
                lastRangeEnd
            }
            newEndPosition>=selectorStartRight -> {
                newEndPosition
            }
            else -> selectorStartRight
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

        // calculate number of selected ranges
        val selectorWidth = selectorStartPoint.width+selectorMid.width+selectorEndPoint.selector_end_background.width+1
        val rangeViewWidth = startRangeBounds.right - startRangeBounds.left
        val numberOfSelectedRanges = selectorWidth/rangeViewWidth

        //calculate end time
        val endTime = LocalTime.of(selectedStartTime!!.hour, selectedStartTime!!.minute)
            .plusMinutes((numberOfSelectedRanges*30).toLong())

        //update selected end time
        selectedEndTime = if(selectedStartTime!!.hour==23 && endTime.hour==0){
            TimelineTime(24,0)
        }else{
            TimelineTime(endTime.hour, endTime.minute)
        }

        //push updates on new selected time changes
        rangeChangedListener.invoke()
            ?.onSelectedRangeChanged(selectedStartTime!!, selectedEndTime!!)

    }

    private fun getStartRangeViewX():Float{
        return (timelineLinearLayout.getChildAt(0) as SelectableTimelinePoint)
            .verticalLine.x
    }

    private fun getEndRangeViewX():Float{
        val lastSTP = timelineLinearLayout.getChildAt(timelineLinearLayout.childCount-1)as SelectableTimelinePoint
        return lastSTP.x + lastSTP.firstRangeView.width
    }

    private fun snapSelectorEnd(){

        //calculate and snap selector's end to end of rangeview

        //get range view at selector's end position
        val snapY = timelineLinearLayout.y+timelineLinearLayout.measuredHeight-1
        val selectorEndBg = selectorEndPoint.selector_end_background
        var endX = selectorEndPoint.x + selectorEndBg.width
        var endPointRangePair = timelineLinearLayout.getPointAndRange(endX,snapY)!!

        var endRangeBounds = endPointRangePair.second.getChildXYBounds(endPointRangePair.first.x,
            endPointRangePair.first.y)

        //select previous range if end x doesn't cover more than a third of range
        if((endX - endRangeBounds.left)<(endPointRangePair.second.width/3)){
            endX = (endRangeBounds.left-1).toFloat()
            endPointRangePair = timelineLinearLayout.getPointAndRange(endX,snapY)!!

            endRangeBounds = endPointRangePair.second.getChildXYBounds(endPointRangePair.first.x,
                endPointRangePair.first.y)
        }

        //snap selector end to rangeview end
        setSelectorEndPoint(endRangeBounds.right.toFloat())

        //set new end time after snap
        selectedEndTime = endPointRangePair.second.timeRange.endTime

        //send updates on new selected time changes
        rangeChangedListener.invoke()?.onSelectedRangeChanged(selectedStartTime!!,selectedEndTime!!)
    }

    inner class CustomDragListener(private var scrollview:ObservableHorizontalScrollView): View.OnDragListener{

        override fun onDrag(view: View, event: DragEvent): Boolean {

            val viewLabel = event.localState as SelectorViewLabel? ?: return false

            when(event.action){
                DragEvent.ACTION_DRAG_ENTERED->{

                }
                DragEvent.ACTION_DRAG_STARTED->{
                    scrollview.setIsScrollable(false)
                }
                DragEvent.ACTION_DRAG_LOCATION->{

                    //update selector view position on drag

                    if(viewLabel == SelectorViewLabel.END){
                        setSelectorEndPoint(event.x)
                    }else {
                        setSelectorStartPoint(event.x,true)
                    }
                }
                DragEvent.ACTION_DRAG_ENDED->{
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