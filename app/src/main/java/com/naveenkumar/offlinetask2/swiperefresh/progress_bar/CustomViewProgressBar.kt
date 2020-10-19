package com.naveenkumar.offlinetask2.swiperefresh.progress_bar

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.naveenkumar.offlinetask2.R
import com.naveenkumar.offlinetask2.swiperefresh.IGRefreshLayout
import com.naveenkumar.offlinetask2.utils.Utility

class CustomViewProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseProgressBar(context, attrs, defStyleAttr) {

    var bar = ImageView(context)
    init {
        Utility.loadImage(bar.context,R.drawable.circle3,bar)
    }

    override fun setParent(parent: IGRefreshLayout) {
        mParent = parent

        setUpView()
    }

    override fun setPercent(percent: Float) {
        mPercent = percent
        bar.alpha = percent/100
    }

    private fun setUpView(){
        mParent.setCustomView(bar,dp2px(90),LinearLayout.LayoutParams.MATCH_PARENT)
    }

    override fun start() {
        //Utility.loadImage(bar.context,R.drawable.circle2,bar)
    }

    override fun stop() {
        //Utility.clearImage(bar.context,bar)
    }
}