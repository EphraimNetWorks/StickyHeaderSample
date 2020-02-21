package com.networks.testapplication.ui.timeline

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.jakewharton.threetenabp.AndroidThreeTen
import com.networks.testapplication.R
import com.networks.testapplication.utils.TimelineRange
import com.networks.testapplication.utils.TimelineTime
import kotlinx.android.synthetic.main.activity_timeline.*
import org.threeten.bp.LocalTime

class TimelineActivity : AppCompatActivity(), ItemScrollChangeListener{

    private val timelineViewScrollPosition = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        AndroidThreeTen.init(this)

        timeline_recycler_view.adapter = TestRecyclerAdapter(this)



    }

    override fun getScrollPosition(): Pair<LifecycleOwner, LiveData<Int>> {
        return Pair(this,timelineViewScrollPosition)
    }

    override fun onItemScrolled(newPosition: Int) {
        timelineViewScrollPosition.value = newPosition
    }
}
