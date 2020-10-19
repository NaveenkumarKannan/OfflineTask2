package com.naveenkumar.offlinetask2.swiperefresh.pulltorefresh

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.Transformation
import android.widget.AbsListView
import android.widget.ImageView
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import com.naveenkumar.offlinetask2.R


class PullRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) :
    ViewGroup(context, attrs) {
    private var mTarget: View? = null
    private lateinit var mRefreshView: ImageView
    private val mDecelerateInterpolator: Interpolator
    private val mTouchSlop: Int
    var finalOffset = 0
    private val mTotalDragDistance: Int
    private var mRefreshDrawable: RingDrawable? = null
    private var mCurrentOffsetTop = 0
    private var mRefreshing = false
    private var mActivePointerId = 0
    private var mIsBeingDragged = false
    private var mInitialMotionY = 0f
    private var mFrom = 0
    private var mNotify = false
    private var mListener: OnRefreshListener? = null
    private var mColorSchemeColors: IntArray
    var mDurationToStartPosition: Int
    var mDurationToCorrectPosition: Int
    private var mInitialOffsetTop = 0
    private var mDispatchTargetTouchDown = false
    private var mDragPercent = 0f
    fun setColorSchemeColors(vararg colorSchemeColors: Int) {
        mColorSchemeColors = colorSchemeColors
        mRefreshDrawable!!.setColorSchemeColors(colorSchemeColors)
    }

    fun setColor(color: Int) {
        setColorSchemeColors(color)
    }

    fun setRefreshStyle() {
        mRefreshDrawable = RingDrawable(this)
        setRefreshing(false)
        mRefreshDrawable!!.setColorSchemeColors(mColorSchemeColors)
        mRefreshView.setImageDrawable(mRefreshDrawable)
    }

    override fun onMeasure(widthMeasureSpecInt: Int, heightMeasureSpecInt: Int) {
        var widthMeasureSpec = widthMeasureSpecInt
        var heightMeasureSpec = heightMeasureSpecInt
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        ensureTarget()
        if (mTarget == null) return
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
            measuredWidth - paddingRight - paddingLeft,
            MeasureSpec.EXACTLY
        )
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
            measuredHeight - paddingTop - paddingBottom,
            MeasureSpec.EXACTLY
        )
        mTarget!!.measure(widthMeasureSpec, heightMeasureSpec)
        mRefreshView.measure(widthMeasureSpec, heightMeasureSpec)
        //        mRefreshView.measure(MeasureSpec.makeMeasureSpec(mRefreshViewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mRefreshViewHeight, MeasureSpec.EXACTLY));
    }

    private fun ensureTarget() {
        if (mTarget != null) return
        if (childCount > 0) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child !== mRefreshView) mTarget = child
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isEnabled || canChildScrollUp() && !mRefreshing) {
            return false
        }
        val action = MotionEventCompat.getActionMasked(ev)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (!mRefreshing) {
                    setTargetOffsetTop(0, true)
                }
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
                val initialMotionY = getMotionEventY(ev, mActivePointerId)
                if (initialMotionY == -1f) {
                    return false
                }
                mInitialMotionY = initialMotionY
                mInitialOffsetTop = mCurrentOffsetTop
                mDispatchTargetTouchDown = false
                mDragPercent = 0f
            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    return false
                }
                val y = getMotionEventY(ev, mActivePointerId)
                if (y == -1f) {
                    return false
                }
                val yDiff = y - mInitialMotionY
                if (mRefreshing) {
                    mIsBeingDragged = !(yDiff < 0 && mCurrentOffsetTop <= 0)
                } else if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
            }
            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
        }
        return mIsBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!mIsBeingDragged) {
            return super.onTouchEvent(ev)
        }
        val action = MotionEventCompat.getActionMasked(ev)
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                val y = MotionEventCompat.getY(ev, pointerIndex)
                val yDiff = y - mInitialMotionY
                var targetY: Int
                if (mRefreshing) {
                    targetY = (mInitialOffsetTop + yDiff).toInt()
                    if (canChildScrollUp()) {
                        targetY = -1
                        mInitialMotionY = y
                        mInitialOffsetTop = 0
                        if (mDispatchTargetTouchDown) {
                            mTarget!!.dispatchTouchEvent(ev)
                        } else {
                            val obtain = MotionEvent.obtain(ev)
                            obtain.action = MotionEvent.ACTION_DOWN
                            mDispatchTargetTouchDown = true
                            mTarget!!.dispatchTouchEvent(obtain)
                        }
                    } else {
                        if (targetY < 0) {
                            if (mDispatchTargetTouchDown) {
                                mTarget!!.dispatchTouchEvent(ev)
                            } else {
                                val obtain = MotionEvent.obtain(ev)
                                obtain.action = MotionEvent.ACTION_DOWN
                                mDispatchTargetTouchDown = true
                                mTarget!!.dispatchTouchEvent(obtain)
                            }
                            targetY = 0
                        } else if (targetY > mTotalDragDistance) {
                            targetY = mTotalDragDistance
                        } else {
                            if (mDispatchTargetTouchDown) {
                                val obtain = MotionEvent.obtain(ev)
                                obtain.action = MotionEvent.ACTION_CANCEL
                                mDispatchTargetTouchDown = false
                                mTarget!!.dispatchTouchEvent(obtain)
                            }
                        }
                    }
                } else {
                    val scrollTop = yDiff * DRAG_RATE
                    val originalDragPercent = scrollTop / mTotalDragDistance
                    if (originalDragPercent < 0) {
                        return false
                    }
                    mDragPercent = Math.min(1f, Math.abs(originalDragPercent))
                    val extraOS = Math.abs(scrollTop) - mTotalDragDistance
                    val slingshotDist = finalOffset.toFloat()
                    val tensionSlingshotPercent = Math.max(
                        0f,
                        Math.min(extraOS, slingshotDist * 2) / slingshotDist
                    )
                    val tensionPercent =
                        (tensionSlingshotPercent / 4 - Math.pow(
                            (tensionSlingshotPercent / 4).toDouble(), 2.0
                        )).toFloat() * 2f
                    val extraMove = slingshotDist * tensionPercent * 2
                    targetY = (slingshotDist * mDragPercent + extraMove).toInt()
                    if (mRefreshView.visibility != View.VISIBLE) {
                        mRefreshView.visibility = View.VISIBLE
                    }
                    if (scrollTop < mTotalDragDistance) {
                        mRefreshDrawable!!.setPercent(mDragPercent)
                    }
                }
                setTargetOffsetTop(targetY - mCurrentOffsetTop, true)
            }
            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val index = MotionEventCompat.getActionIndex(ev)
                mActivePointerId = MotionEventCompat.getPointerId(ev, index)
            }
            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mActivePointerId == INVALID_POINTER) {
                    return false
                }
                if (mRefreshing) {
                    if (mDispatchTargetTouchDown) {
                        mTarget!!.dispatchTouchEvent(ev)
                        mDispatchTargetTouchDown = false
                    }
                    return false
                }
                val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overscrollTop =
                    (y - mInitialMotionY) * DRAG_RATE
                mIsBeingDragged = false
                if (overscrollTop > mTotalDragDistance) {
                    setRefreshing(true, true)
                } else {
                    mRefreshing = false
                    animateOffsetToStartPosition()
                }
                mActivePointerId = INVALID_POINTER
                return false
            }
        }
        return true
    }

    fun setDurations(durationToStartPosition: Int, durationToCorrectPosition: Int) {
        mDurationToStartPosition = durationToStartPosition
        mDurationToCorrectPosition = durationToCorrectPosition
    }

    private fun animateOffsetToStartPosition() {
        mFrom = mCurrentOffsetTop
        mAnimateToStartPosition.reset()
        mAnimateToStartPosition.duration = mDurationToStartPosition.toLong()
        mAnimateToStartPosition.interpolator = mDecelerateInterpolator
        mAnimateToStartPosition.setAnimationListener(mToStartListener)
        mRefreshView.clearAnimation()
        mRefreshView.startAnimation(mAnimateToStartPosition)
    }

    private fun animateOffsetToCorrectPosition() {
        mFrom = mCurrentOffsetTop
        mAnimateToCorrectPosition.reset()
        mAnimateToCorrectPosition.duration = mDurationToCorrectPosition.toLong()
        mAnimateToCorrectPosition.interpolator = mDecelerateInterpolator
        mAnimateToCorrectPosition.setAnimationListener(mRefreshListener)
        mRefreshView.clearAnimation()
        mRefreshView.startAnimation(mAnimateToCorrectPosition)
    }

    private val mAnimateToStartPosition: Animation = object : Animation() {
        public override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation
        ) {
            moveToStart(interpolatedTime)
        }
    }
    private val mAnimateToCorrectPosition: Animation = object : Animation() {
        public override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation
        ) {
            val endTarget: Int = finalOffset
            val targetTop = mFrom + ((endTarget - mFrom) * interpolatedTime).toInt()
            val offset = targetTop - mTarget!!.top
            setTargetOffsetTop(offset, false /* requires update */)
        }
    }

    private fun moveToStart(interpolatedTime: Float) {
        val targetTop = mFrom - (mFrom * interpolatedTime).toInt()
        val offset = targetTop - mTarget!!.top
        setTargetOffsetTop(offset, false)
        mRefreshDrawable!!.setPercent(mDragPercent * (1 - interpolatedTime))
    }

    fun setRefreshing(refreshing: Boolean) {
        if (mRefreshing != refreshing) {
            setRefreshing(refreshing, false /* notify */)
        }
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (mRefreshing != refreshing) {
            mNotify = notify
            ensureTarget()
            mRefreshing = refreshing
            if (mRefreshing) {
                mRefreshDrawable!!.setPercent(1f)
                animateOffsetToCorrectPosition()
            } else {
                animateOffsetToStartPosition()
            }
        }
    }

    private val mRefreshListener: AnimationListener = object : AnimationListener {
        override fun onAnimationStart(animation: Animation) {
            mRefreshView.visibility = View.VISIBLE
        }

        override fun onAnimationRepeat(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {
            if (mRefreshing) {
                mRefreshDrawable!!.start()
                if (mNotify) {
                    if (mListener != null) {
                        mListener!!.onRefresh()
                    }
                }
            } else {
                mRefreshDrawable!!.stop()
                mRefreshView.visibility = View.GONE
                animateOffsetToStartPosition()
            }
            mCurrentOffsetTop = mTarget!!.top
        }
    }
    private val mToStartListener: AnimationListener = object : AnimationListener {
        override fun onAnimationStart(animation: Animation) {
            mRefreshDrawable!!.stop()
        }

        override fun onAnimationRepeat(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {
//            mRefreshDrawable.stop();
            mRefreshView.visibility = View.GONE
            mCurrentOffsetTop = mTarget!!.top
        }
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(ev)
        val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
        }
    }

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = MotionEventCompat.findPointerIndex(ev, activePointerId)
        return if (index < 0) {
            (-1).toFloat()
        } else MotionEventCompat.getY(ev, index)
    }

    private fun setTargetOffsetTop(offset: Int, requiresUpdate: Boolean) {
//        mRefreshView.bringToFront();
        mTarget!!.offsetTopAndBottom(offset)
        mCurrentOffsetTop = mTarget!!.top
        mRefreshDrawable!!.offsetTopAndBottom(offset)
        if (requiresUpdate && Build.VERSION.SDK_INT < 11) {
            invalidate()
        }
    }

    private fun canChildScrollUp(): Boolean {
        return if (Build.VERSION.SDK_INT < 14) {
            if (mTarget is AbsListView) {
                val absListView = mTarget as AbsListView
                (absListView.childCount > 0
                        && (absListView.firstVisiblePosition > 0 || absListView.getChildAt(0)
                    .top < absListView.paddingTop))
            } else {
                mTarget!!.scrollY > 0
            }
        } else {
            ViewCompat.canScrollVertically(mTarget, -1)
        }
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        ensureTarget()
        if (mTarget == null) return
        val height = measuredHeight
        val width = measuredWidth
        val left = paddingLeft
        val top = paddingTop
        val right = paddingRight
        val bottom = paddingBottom
        mTarget!!.layout(
            left,
            top + mTarget!!.top,
            left + width - right,
            top + height - bottom + mTarget!!.top
        )
        mRefreshView.layout(left, top, left + width - right, top + height - bottom)
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    fun setOnRefreshListener(listener: OnRefreshListener?) {
        this.mListener = listener
    }

    interface OnRefreshListener {
        fun onRefresh()
    }

    companion object {
        private const val DECELERATE_INTERPOLATION_FACTOR = 2f
        private const val DRAG_MAX_DISTANCE = 64
        private const val INVALID_POINTER = -1
        private const val DRAG_RATE = .5f
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PullRefreshLayout)
        val colorsId = a.getResourceId(R.styleable.PullRefreshLayout_refreshColors, 0)
        val colorId = a.getResourceId(R.styleable.PullRefreshLayout_refreshColor, 0)
        a.recycle()
        mDecelerateInterpolator =
            DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        val defaultDuration =
            resources.getInteger(android.R.integer.config_mediumAnimTime)
        mDurationToStartPosition = defaultDuration
        mDurationToCorrectPosition = defaultDuration
        mTotalDragDistance = dp2px(DRAG_MAX_DISTANCE)
        finalOffset = mTotalDragDistance
        mColorSchemeColors = if (colorsId > 0) {
            context.resources.getIntArray(colorsId)
        } else {
            intArrayOf(
                Color.rgb(0xC9, 0x34, 0x37),
                Color.rgb(0x37, 0x5B, 0xF1),
                Color.rgb(0xF7, 0xD2, 0x3E),
                Color.rgb(0x34, 0xA3, 0x50)
            )
        }
        if (colorId > 0) {
            mColorSchemeColors = intArrayOf(context.resources.getColor(colorId))
        }
        mRefreshView = ImageView(context)
        setRefreshStyle()
        mRefreshView.visibility = View.GONE
        addView(mRefreshView, 0)
        setWillNotDraw(false)
        ViewCompat.setChildrenDrawingOrderEnabled(this, true)
    }
}
