package com.naveenkumar.offlinetask2.swiperefresh

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View


class AnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private var pullHeight = 0
    private var pullDelta = 0
    private var mWidthOffset = 0f
    private var mAniStatus = AnimatorStatus.PULL_DOWN

    internal enum class AnimatorStatus {
        PULL_DOWN, DRAG_DOWN, REL_DRAG, SPRING_UP,  // rebound to up, the position is less than PULL_HEIGHT
        POP_BALL, OUTER_CIR, REFRESHING, DONE, STOP;

        override fun toString(): String {
            return when (this) {
                PULL_DOWN -> "pull down"
                DRAG_DOWN -> "drag down"
                REL_DRAG -> "release drag"
                SPRING_UP -> "spring up"
                POP_BALL -> "pop ball"
                OUTER_CIR -> "outer circle"
                REFRESHING -> "refreshing..."
                DONE -> "done!"
                STOP -> "stop"
            }
        }
    }

    private var mBackPaint: Paint? = null
    private var mBallPaint: Paint? = null
    private var mOutPaint: Paint? = null
    private var mPath: Path? = null
    private fun initView(
        context: Context
    ) {
        pullHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            100f,
            context.resources.displayMetrics
        ).toInt()
        pullDelta = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            50f,
            context.resources.displayMetrics
        ).toInt()
        mWidthOffset = 0.5f
        mBackPaint = Paint()
        mBackPaint!!.isAntiAlias = true
        mBackPaint!!.style = Paint.Style.FILL
        mBackPaint!!.color = -0x746f51
        mBallPaint = Paint()
        mBallPaint!!.isAntiAlias = true
        mBallPaint!!.color = 0x00000000
        mBallPaint!!.style = Paint.Style.FILL
        mOutPaint = Paint()
        mOutPaint!!.isAntiAlias = true
        mOutPaint!!.color = -0x1
        mOutPaint!!.style = Paint.Style.STROKE
        mOutPaint!!.strokeWidth = 5f
        mPath = Path()
    }

    private var mRadius = 0
    private var mWidth = 0
    private var mHeight = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var hms = heightMeasureSpec
        val height = MeasureSpec.getSize(hms)
        if (height > pullDelta + pullHeight) {
            hms = MeasureSpec.makeMeasureSpec(
                pullDelta + pullHeight,
                MeasureSpec.getMode(hms)
            )
        }
        super.onMeasure(widthMeasureSpec, hms)
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            mRadius = height / 6
            mWidth = width
            mHeight = height
            if (mHeight < pullHeight) {
                mAniStatus = AnimatorStatus.PULL_DOWN
            }
            when (mAniStatus) {
                AnimatorStatus.PULL_DOWN -> if (mHeight >= pullHeight) {
                    mAniStatus = AnimatorStatus.DRAG_DOWN
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (mAniStatus) {
            AnimatorStatus.PULL_DOWN -> canvas.drawRect(
                0f,
                0f,
                mWidth.toFloat(),
                mHeight.toFloat(),
                mBackPaint!!
            )
            AnimatorStatus.REL_DRAG, AnimatorStatus.DRAG_DOWN -> drawDrag(
                canvas
            )
            AnimatorStatus.SPRING_UP -> {
                drawSpring(canvas, springDelta)
                invalidate()
            }
            AnimatorStatus.POP_BALL -> {
                drawPopBall(canvas)
                invalidate()
            }
            AnimatorStatus.OUTER_CIR -> {
                drawOutCir(canvas)
                invalidate()
            }
            AnimatorStatus.REFRESHING -> {
                drawRefreshing(canvas)
                invalidate()
            }
            AnimatorStatus.DONE -> {
                drawDone(canvas)
                invalidate()
            }
            AnimatorStatus.STOP -> drawDone(canvas)
        }
        if (mAniStatus == AnimatorStatus.REL_DRAG) {
            val params = layoutParams
            var height: Int
            // NOTICE: If the height equals mLastHeight, then the requestLayout() will not work correctly
            do {
                height = relHeight
            } while (height == mLastHeight && relRatio != 1f)
            mLastHeight = height
            params.height = pullHeight + height
            requestLayout()
        }
    }

    private fun drawDrag(canvas: Canvas) {
        canvas.drawRect(0f, 0f, mWidth.toFloat(), pullHeight.toFloat(), mBackPaint!!)
        mPath!!.reset()
        mPath!!.moveTo(0f, pullHeight.toFloat())
        mPath!!.quadTo(
            mWidthOffset * mWidth, pullHeight + (mHeight - pullHeight) * 2.toFloat(),
            mWidth.toFloat(), pullHeight.toFloat()
        )
        canvas.drawPath(mPath!!, mBackPaint!!)
    }

    private fun drawSpring(canvas: Canvas, springDelta: Int) {
        mPath!!.reset()
        mPath!!.moveTo(0f, 0f)
        mPath!!.lineTo(0f, pullHeight.toFloat())
        mPath!!.quadTo(
            mWidth / 2.toFloat(), pullHeight - springDelta.toFloat(),
            mWidth.toFloat(), pullHeight.toFloat()
        )
        mPath!!.lineTo(mWidth.toFloat(), 0f)
        canvas.drawPath(mPath!!, mBackPaint!!)
        val curH = pullHeight - springDelta / 2
        if (curH > pullHeight - pullDelta / 2) {
            val leftX = (mWidth / 2 - 2 * mRadius + sprRatio * mRadius).toInt()
            mPath!!.reset()
            mPath!!.moveTo(leftX.toFloat(), curH.toFloat())
            mPath!!.quadTo(
                mWidth / 2.toFloat(), curH - mRadius * sprRatio * 2,
                mWidth - leftX.toFloat(), curH.toFloat()
            )
            canvas.drawPath(mPath!!, mBallPaint!!)
        } else {
            canvas.drawArc(
                RectF(
                    (mWidth / 2 - mRadius).toFloat(),
                    (curH - mRadius).toFloat(),
                    (mWidth / 2 + mRadius).toFloat(),
                    (curH + mRadius).toFloat()
                ), 180f, 180f, true, mBallPaint!!
            )
        }
    }

    private fun drawPopBall(canvas: Canvas) {
        mPath!!.reset()
        mPath!!.moveTo(0f, 0f)
        mPath!!.lineTo(0f, pullHeight.toFloat())
        mPath!!.quadTo(
            mWidth / 2.toFloat(), pullHeight - pullDelta.toFloat(),
            mWidth.toFloat(), pullHeight.toFloat()
        )
        mPath!!.lineTo(mWidth.toFloat(), 0f)
        canvas.drawPath(mPath!!, mBackPaint!!)
        val cirCentStart = pullHeight - pullDelta / 2
        val cirCenY = (cirCentStart - mRadius * 2 * popRatio).toInt()
        canvas.drawArc(
            RectF(
                (mWidth / 2 - mRadius).toFloat(),
                (cirCenY - mRadius).toFloat(),
                (mWidth / 2 + mRadius).toFloat(),
                (cirCenY + mRadius).toFloat()
            ), 180f, 360f, true, mBallPaint!!
        )
        if (popRatio < 1) {
            drawTail(canvas, cirCenY, cirCentStart + 1, popRatio)
        } else {
            canvas.drawCircle(
                mWidth / 2.toFloat(),
                cirCenY.toFloat(),
                mRadius.toFloat(),
                mBallPaint!!
            )
        }
    }

    private fun drawTail(
        canvas: Canvas,
        centerY: Int,
        bottom: Int,
        fraction: Float
    ) {
        val bezier1w = (mWidth / 2 + mRadius * 3 / 4 * (1 - fraction)).toInt()
        val start = PointF((mWidth / 2 + mRadius).toFloat(), centerY.toFloat())
        val bezier1 = PointF(bezier1w.toFloat(), bottom.toFloat())
        val bezier2 = PointF((bezier1w + mRadius / 2).toFloat(), bottom.toFloat())
        mPath!!.reset()
        mPath!!.moveTo(start.x, start.y)
        mPath!!.quadTo(
            bezier1.x, bezier1.y,
            bezier2.x, bezier2.y
        )
        mPath!!.lineTo(mWidth - bezier2.x, bezier2.y)
        mPath!!.quadTo(
            mWidth - bezier1.x, bezier1.y,
            mWidth - start.x, start.y
        )
        canvas.drawPath(mPath!!, mBallPaint!!)
    }

    private fun drawOutCir(canvas: Canvas) {
        mPath!!.reset()
        mPath!!.moveTo(0f, 0f)
        mPath!!.lineTo(0f, pullHeight.toFloat())
        mPath!!.quadTo(
            mWidth / 2.toFloat(), pullHeight - (1 - outRatio) * pullDelta,
            mWidth.toFloat(), pullHeight.toFloat()
        )
        mPath!!.lineTo(mWidth.toFloat(), 0f)
        canvas.drawPath(mPath!!, mBackPaint!!)
        val innerY = pullHeight - pullDelta / 2 - mRadius * 2
        canvas.drawCircle(mWidth / 2.toFloat(), innerY.toFloat(), mRadius.toFloat(), mBallPaint!!)
    }

    private var mRefreshStart = 90
    private var mRefreshStop = 90
    private var targetDegree = 270
    private var mIsStart = true
    private var mIsRefreshing = true
    private fun drawRefreshing(canvas: Canvas) {
        canvas.drawRect(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), mBackPaint!!)
        val innerY = pullHeight - pullDelta / 2 - mRadius * 2
        canvas.drawCircle(mWidth / 2.toFloat(), innerY.toFloat(), mRadius.toFloat(), mBallPaint!!)
        val outerR = mRadius + 10
        mRefreshStart += if (mIsStart) 3 else 10
        mRefreshStop += if (mIsStart) 10 else 3
        mRefreshStart %= 360
        mRefreshStop %= 360
        var swipe = mRefreshStop - mRefreshStart
        swipe = if (swipe < 0) swipe + 360 else swipe
        canvas.drawArc(
            RectF(
                (mWidth / 2 - outerR).toFloat(),
                (innerY - outerR).toFloat(),
                (mWidth / 2 + outerR).toFloat(),
                (innerY + outerR).toFloat()
            ),
            mRefreshStart.toFloat(), swipe.toFloat(), false, mOutPaint!!
        )
        if (swipe >= targetDegree) {
            mIsStart = false
        } else if (swipe <= 10) {
            mIsStart = true
        }
        if (!mIsRefreshing) {
            applyDone()
        }
    }

    // stop refreshing
    fun setRefreshing(isFresh: Boolean) {
        mIsRefreshing = isFresh
    }

    private fun drawDone(canvas: Canvas) {
        val beforeColor = mOutPaint!!.color
        if (doneRatio < 0.3) {
            canvas.drawRect(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), mBackPaint!!)
            val innerY = pullHeight - pullDelta / 2 - mRadius * 2
            canvas.drawCircle(
                mWidth / 2.toFloat(), innerY.toFloat(), mRadius.toFloat(),
                mBallPaint!!
            )
            val outerR = (mRadius + 10 + 10 * doneRatio / 0.3f).toInt()
            val afterColor = Color.argb(
                (0xff * (1 - doneRatio / 0.3f)).toInt(),
                Color.red(beforeColor),
                Color.green(beforeColor),
                Color.blue(beforeColor)
            )
            mOutPaint!!.color = afterColor
            canvas.drawArc(
                RectF(
                    (mWidth / 2 - outerR).toFloat(),
                    (innerY - outerR).toFloat(),
                    (mWidth / 2 + outerR).toFloat(),
                    (innerY + outerR).toFloat()
                ), 0f, 360f, false, mOutPaint!!
            )
        }
        mOutPaint!!.color = beforeColor
        if (doneRatio >= 0.3 && doneRatio < 0.7) {
            canvas.drawRect(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), mBackPaint!!)
            val fraction = (doneRatio - 0.3f) / 0.4f
            val startCentY = pullHeight - pullDelta / 2 - mRadius * 2
            val curCentY = (startCentY + (pullDelta / 2 + mRadius * 2) * fraction).toInt()
            canvas.drawCircle(
                mWidth / 2.toFloat(),
                curCentY.toFloat(),
                mRadius.toFloat(),
                mBallPaint!!
            )
            if (curCentY >= pullHeight - mRadius * 2) {
                drawTail(canvas, curCentY, pullHeight, 1 - fraction)
            }
        }
        if (doneRatio in 0.7..1.0) {
            val fraction = (doneRatio - 0.7f) / 0.3f
            canvas.drawRect(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), mBackPaint!!)
            val leftX = (mWidth / 2 - mRadius - 2 * mRadius * fraction).toInt()
            mPath!!.reset()
            mPath!!.moveTo(leftX.toFloat(), pullHeight.toFloat())
            mPath!!.quadTo(
                mWidth / 2.toFloat(), pullHeight - mRadius * (1 - fraction),
                mWidth - leftX.toFloat(), pullHeight.toFloat()
            )
            canvas.drawPath(mPath!!, mBallPaint!!)
        }
    }

    private var mLastHeight = 0
    private val relHeight: Int
        get() = (mSpriDeta * (1 - relRatio)).toInt()

    private val springDelta: Int
        get() = (pullDelta * sprRatio).toInt()

    private var mStart: Long = 0
    private var mStop: Long = 0
    private var mSpriDeta = 0
    fun releaseDrag() {
        mStart = System.currentTimeMillis()
        mStop = mStart + REL_DRAG_DUR
        mAniStatus = AnimatorStatus.REL_DRAG
        mSpriDeta = mHeight - pullHeight
        requestLayout()
    }

    private val relRatio: Float
        get() {
            if (System.currentTimeMillis() >= mStop) {
                springUp()
                return 1F
            }
            val ratio =
                (System.currentTimeMillis() - mStart) / REL_DRAG_DUR.toFloat()
            return ratio.coerceAtMost(1f)
        }

    private var mSprStart: Long = 0
    private var mSprStop: Long = 0
    private fun springUp() {
        mSprStart = System.currentTimeMillis()
        mSprStop = mSprStart + SPRING_DUR
        mAniStatus = AnimatorStatus.SPRING_UP
        invalidate()
    }

    private val sprRatio: Float
        get() {
            if (System.currentTimeMillis() >= mSprStop) {
                popBall()
                return 1F
            }
            val ratio =
                (System.currentTimeMillis() - mSprStart) / SPRING_DUR.toFloat()
            return ratio.coerceAtMost(1f)
        }

    private var mPopStart: Long = 0
    private var mPopStop: Long = 0
    private fun popBall() {
        mPopStart = System.currentTimeMillis()
        mPopStop = mPopStart + POP_BALL_DUR
        mAniStatus = AnimatorStatus.POP_BALL
        invalidate()
    }

    private val popRatio: Float
        get() {
            if (System.currentTimeMillis() >= mPopStop) {
                startOutCir()
                return 1F
            }
            val ratio =
                (System.currentTimeMillis() - mPopStart) / POP_BALL_DUR.toFloat()
            return ratio.coerceAtMost(1f)
        }

    private var mOutStart: Long = 0
    private var mOutStop: Long = 0
    private fun startOutCir() {
        mOutStart = System.currentTimeMillis()
        mOutStop = mOutStart + OUTER_DUR
        mAniStatus = AnimatorStatus.OUTER_CIR
        mRefreshStart = 90
        mRefreshStop = 90
        targetDegree = 270
        mIsStart = true
        mIsRefreshing = true
        invalidate()
    }

    private val outRatio: Float
        get() {
            if (System.currentTimeMillis() >= mOutStop) {
                mAniStatus = AnimatorStatus.REFRESHING
                mIsRefreshing = true
                return 1F
            }
            val ratio =
                (System.currentTimeMillis() - mOutStart) / OUTER_DUR.toFloat()
            return ratio.coerceAtMost(1f)
        }

    private var mDoneStart: Long = 0
    private var mDoneStop: Long = 0
    private fun applyDone() {
        mDoneStart = System.currentTimeMillis()
        mDoneStop = mDoneStart + DONE_DUR
        mAniStatus = AnimatorStatus.DONE
    }

    private val doneRatio: Float
        get() {
            if (System.currentTimeMillis() >= mDoneStop) {
                mAniStatus = AnimatorStatus.STOP
                if (onViewAniDone != null) {
                    onViewAniDone!!.viewAniDone()
                }
                return 1F
            }
            val ratio =
                (System.currentTimeMillis() - mDoneStart) / DONE_DUR.toFloat()
            return ratio.coerceAtMost(1f)
        }

    private var onViewAniDone: OnViewAniDone? = null
    fun setOnViewAniDone(onViewAniDone: OnViewAniDone?) {
        this.onViewAniDone = onViewAniDone
    }

    interface OnViewAniDone {
        fun viewAniDone()
    }

    fun setAniBackColor(color: Long) {
        mBackPaint!!.color = color.toInt()
    }

    fun setAniForeColor(color: Long) {
        mBallPaint!!.color = color.toInt()
        mOutPaint!!.color = color.toInt()
        setBackgroundColor(color.toInt())
    }

    // the height of view is smallTimes times of circle radius
    fun setRadius(smallTimes: Int) {
        mRadius = mHeight / smallTimes
    }

    companion object {
        private const val REL_DRAG_DUR: Long = 200
        private const val SPRING_DUR: Long = 200
        private const val POP_BALL_DUR: Long = 300
        private const val OUTER_DUR: Long = 200
        private const val DONE_DUR: Long = 1000
    }

    init {
        initView(context)
    }
}
