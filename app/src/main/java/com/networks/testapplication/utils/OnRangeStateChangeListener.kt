package com.networks.testapplication.utils

interface OnRangeStateChangeListener {
    fun onSelectedRangeChanged(newFrom: TimelineTime, newTo: TimelineTime)
}