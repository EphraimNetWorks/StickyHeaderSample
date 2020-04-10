package com.networks.testapplication.ui.timeline


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jakewharton.threetenabp.AndroidThreeTen
import com.networks.testapplication.R
import com.networks.testapplication.utils.OnRangeStateChangeListener
import com.networks.testapplication.utils.TimelineRange
import com.networks.testapplication.utils.TimelineTime
import kotlinx.android.synthetic.main.activity_timeline.*

class TimelineActivity : AppCompatActivity(), ItemScrollChangeListener{

    private val timelineViewScrollPosition = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        AndroidThreeTen.init(this)


        val ranges = arrayListOf(
            TimelineRange(TimelineTime(3,30), TimelineTime(4,30)),
                    TimelineRange(TimelineTime(11,0), TimelineTime(14,0))
        )

        selectable_timeline_view.selectedColor = (ContextCompat.getColor(this, R.color.colorAccent))

        selectable_timeline_view.setUnselectableRanges(ranges,true)
        selectable_timeline_view.setOnRangeSelectedListener(object : OnRangeStateChangeListener{
            override fun onSelectedRangeChanged(newFrom: TimelineTime, newTo: TimelineTime){

                Toast.makeText(this@TimelineActivity,
                    "Range ${newFrom.hour}:${newFrom.minute} to ${newTo.hour}:${newTo.minute} Selected",
                    Toast.LENGTH_LONG).show()

            }
        })

        select_range_button.setOnClickListener {
            selectable_timeline_view.selectRange(TimelineRange(TimelineTime(5,30),
                TimelineTime(6,0)))
        }


    }

    override fun getScrollPosition(): Pair<LifecycleOwner, LiveData<Int>> {
        return Pair(this,timelineViewScrollPosition)
    }

    override fun onItemScrolled(newPosition: Int) {
        timelineViewScrollPosition.value = newPosition
    }
}
