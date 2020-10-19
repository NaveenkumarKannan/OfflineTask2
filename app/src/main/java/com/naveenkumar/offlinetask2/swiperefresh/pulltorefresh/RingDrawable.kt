package com.naveenkumar.offlinetask2.swiperefresh.pulltorefresh
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue


internal class RingDrawable(
    layout: PullRefreshLayout?
) :
    RefreshDrawable(
        layout
    ) {
    private var tag = "RingDrawable"
    private var isRunning = false
    private var mBounds: RectF? = null
    private var mWidth = 0
    private var mHeight = 0
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPath: Path
    private var mAngle = 0f
    private lateinit var mColorSchemeColors: IntArray
    private var mLevel = 0
    private var mDegress = 0f
    fun log(message: String) {
        Log.e(tag, message)
    }

    override fun setPercent(percent: Float) {
        mPaint.color = evaluate(
            percent * 10,
            mColorSchemeColors[0],
            mColorSchemeColors[1]
        )
        mAngle = 359 * percent
        log("setPercent")
        log("Percent = $percent")
        log("mAngle = $mAngle")
    }

    override fun setColorSchemeColors(colorSchemeColors: IntArray?) {
        if (colorSchemeColors != null) {
            mColorSchemeColors = colorSchemeColors
            log("setColorSchemeColors")
        }else{
            log("setColorSchemeColors null")
        }
    }

    override fun offsetTopAndBottom(offset: Int) {
        invalidateSelf()
    }

    override fun start() {
        mLevel = 50
        isRunning = true
        invalidateSelf()
        log("start")
    }

    private fun updateLevel(level: Int) {
        val animationLevel = if (level == MAX_LEVEL) 0 else level
        val stateForLevel = animationLevel / 50
        val percent = level % 50 / 50f
        val startColor = mColorSchemeColors[stateForLevel]
        val end = (stateForLevel + 1) % mColorSchemeColors.size
        val endColor = mColorSchemeColors[end]
        mPaint.color = evaluate(percent, startColor, endColor)
        mDegress = 360 * percent
        log("updateLevel")
        log("level = $level")
        log("stateForLevel = $stateForLevel\nEnd = $end")
    }

    override fun stop() {
        isRunning = false
        mDegress = 0f
        log("stop")
    }

    override fun isRunning(): Boolean {
        log("isRunning")
        return isRunning
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mWidth = refreshLayout!!.finalOffset
        mHeight = refreshLayout.finalOffset
        mBounds = RectF(
            bounds.width() / 2f - mWidth / 2f,
            bounds.top.toFloat(),
            bounds.width() / 2f + mWidth / 2f,
            (bounds.top + mHeight).toFloat()
        )
        mBounds!!.inset(dp2px(15).toFloat(), dp2px(15).toFloat())
        log("onBoundsChange")
    }

    override fun draw(canvas: Canvas) {
        log("onDraw")
        canvas.save()
        //        canvas.translate(0, mTop);
        canvas.rotate(mDegress, mBounds!!.centerX(), mBounds!!.centerY())
        drawRing(canvas)
        canvas.restore()
        if (isRunning) {
            mLevel = if (mLevel >= MAX_LEVEL) 0 else mLevel + 1
            updateLevel(mLevel)
            invalidateSelf()
        }
    }

    private fun drawRing(canvas: Canvas) {
        mPath.reset()
        mPath.arcTo(mBounds!!, 270f, mAngle, true)
        canvas.drawPath(mPath, mPaint)
        //        canvas.drawArc(mBounds, 270, mAngle, true, mPaint);
        log("drawRing")
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context!!.resources.displayMetrics
        ).toInt()
    }

    private fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = startValue shr 24 and 0xff
        val startR = startValue shr 16 and 0xff
        val startG = startValue shr 8 and 0xff
        val startB = startValue and 0xff
        val endA = endValue shr 24 and 0xff
        val endR = endValue shr 16 and 0xff
        val endG = endValue shr 8 and 0xff
        val endB = endValue and 0xff
        return startA + (fraction * (endA - startA)).toInt() shl 24 or
                (startR + (fraction * (endR - startR)).toInt() shl 16) or
                (startG + (fraction * (endG - startG)).toInt() shl 8) or
                startB + (fraction * (endB - startB)).toInt()
    }

    companion object {
        private const val MAX_LEVEL = 200
    }

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = dp2px(3).toFloat()
        mPaint.strokeCap = Paint.Cap.ROUND
        mPath = Path()
        log("constructor")
        log("mPath = $mPath")
    }
}
