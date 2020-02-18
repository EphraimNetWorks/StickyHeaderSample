package com.networks.testapplication.ui.timeline

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.networks.testapplication.R
import com.networks.testapplication.utils.TimelineRange
import kotlinx.android.synthetic.main.activity_timeline.*
import org.threeten.bp.LocalTime

class TimelineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        val ranges = arrayListOf(
            TimelineRange(LocalTime.of(0,0), LocalTime.of(1,30)),
            TimelineRange(LocalTime.of(3,30), LocalTime.of(4,0)),
            TimelineRange(LocalTime.of(9,30), LocalTime.of(10,30)),
            TimelineRange(LocalTime.of(15,0), LocalTime.of(17,30))
        )

        timeline_view.setDefaultLineColor(Color.LTGRAY)
        timeline_view.setHighlightedLineColor(ContextCompat.getColor(this,R.color.colorAccent))
        timeline_view.setHighlightedRanges(ranges)
    }


}
