package com.networks.testapplication.ui.timeline

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import com.networks.testapplication.R
import com.networks.testapplication.utils.TimelineRange
import com.networks.testapplication.utils.TimelineTime
import kotlinx.android.synthetic.main.activity_timeline.*
import org.threeten.bp.LocalTime

class TimelineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        AndroidThreeTen.init(this)

        val ranges = arrayListOf(
            TimelineRange(TimelineTime(9,15), TimelineTime(9,45)),
            TimelineRange(TimelineTime(2,0), TimelineTime(2,1)),
            TimelineRange(TimelineTime(23,0), TimelineTime(24,0))
        )

        timeline_view.apply {
            setDefaultLineColor(Color.LTGRAY)
            setHighlightedLineColor(ContextCompat.getColor(this@TimelineActivity,R.color.colorAccent))
            setHighlightedRanges(ranges)
        }
    }


}
