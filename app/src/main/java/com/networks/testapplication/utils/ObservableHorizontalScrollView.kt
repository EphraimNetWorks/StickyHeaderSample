package com.networks.testapplication.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class ObservableHorizontalScrollView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : HorizontalScrollView(context, attrs, defStyle) {
    interface OnScrollListener {
        fun onScrollChanged(
            scrollView: ObservableHorizontalScrollView?,
            x: Int,
            y: Int,
            oldX: Int,
            oldY: Int
        )

        fun onEndScroll(scrollView: ObservableHorizontalScrollView?)
    }

    private var mIsScrolling = false
    private var mIsTouching = false
    private var mScrollingRunnable: Runnable? = null
    var onScrollListener: OnScrollListener? =
        null
    private var mIsScrollable = true
    fun setIsScrollable(mIsScrollable: Boolean) {
        this.mIsScrollable = mIsScrollable
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!mIsScrollable) return false
        val action = ev.action
        if (action == MotionEvent.ACTION_MOVE) {
            mIsTouching = true
            mIsScrolling = true
        } else if (action == MotionEvent.ACTION_UP) {
            if (mIsTouching && !mIsScrolling) {
                if (onScrollListener != null) {
                    onScrollListener!!.onEndScroll(this)
                }
            }
            mIsTouching = false
        }
        return super.onTouchEvent(ev)
    }

    override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int) {
        super.onScrollChanged(x, y, oldX, oldY)
        if (Math.abs(oldX - x) > 0) {
            if (mScrollingRunnable != null) {
                removeCallbacks(mScrollingRunnable)
            }
            mScrollingRunnable = Runnable {
                if (mIsScrolling && !mIsTouching) {
                    if (onScrollListener != null) {
                        onScrollListener!!.onEndScroll(this@ObservableHorizontalScrollView)
                    }
                }
                mIsScrolling = false
                mScrollingRunnable = null
            }
            postDelayed(mScrollingRunnable, 200)
        }
        if (onScrollListener != null) {
            onScrollListener!!.onScrollChanged(this, x, y, oldX, oldY)
        }
    }

}