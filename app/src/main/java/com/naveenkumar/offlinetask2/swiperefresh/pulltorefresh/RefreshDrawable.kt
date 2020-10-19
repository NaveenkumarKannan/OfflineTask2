package com.naveenkumar.offlinetask2.swiperefresh.pulltorefresh

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable

abstract class RefreshDrawable(
    val refreshLayout: PullRefreshLayout?
) :
    Drawable(), Drawable.Callback, Animatable {
    val context: Context?
        get() = refreshLayout?.context

    abstract fun setPercent(percent: Float)
    abstract fun setColorSchemeColors(colorSchemeColors: IntArray?)
    abstract fun offsetTopAndBottom(offset: Int)
    override fun invalidateDrawable(who: Drawable) {
        val callback = callback
        callback?.invalidateDrawable(this)
    }

    override fun scheduleDrawable(
        who: Drawable,
        what: Runnable,
        `when`: Long
    ) {
        val callback = callback
        callback?.scheduleDrawable(this, what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        val callback = callback
        callback?.unscheduleDrawable(this, what)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(p0: ColorFilter?) {}

}
