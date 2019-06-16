package app.bsc.db.drawing.view

import android.content.Context
import android.view.MotionEvent
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class CustomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    private var isCurrentlyEnabled: Boolean = false

    init {
        this.isCurrentlyEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.isCurrentlyEnabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.isCurrentlyEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.isCurrentlyEnabled = enabled
    }
}