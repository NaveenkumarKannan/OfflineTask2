package com.naveenkumar.offlinetask2.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.naveenkumar.offlinetask2.R
import com.naveenkumar.offlinetask2.model.Data
import com.naveenkumar.offlinetask2.utils.Utility
import kotlinx.android.synthetic.main.row_layout.view.*


class RecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private var items: List<Data> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DataViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {

            is DataViewHolder -> {
                holder.bind(items[position],holder.itemView.context)
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(list: List<Data>){
        items = list
    }

    class DataViewHolder
    constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){

        private val title = itemView.tv_title
        private val company = itemView.tv_company
        private val countItems = itemView.tv_count_items
        private val date = itemView.tv_date
        private val siv1 = itemView.siv1
        private val siv2 = itemView.siv2
        private val siv3 = itemView.siv3
        private val tvImageCount = itemView.tv_image_count

        fun bind(data: Data, context: Context){
            title.text = data.title
            company.text = data.title
            countItems.text = context.getString(R.string.items_count, data.item_count)
            date.text = data.date
            if(data.images.size>3){
                Utility.loadImage(itemView.context, data.images[0], siv1)
                Utility.loadImage(itemView.context, data.images[1], siv2)
                Utility.loadImage(itemView.context, data.images[2], siv3)
                tvImageCount.text = context.getString(R.string.images_count, data.images.size-3)
                val gradientDrawable = (itemView.tv_image_count.background as GradientDrawable).mutate()
                    (gradientDrawable as GradientDrawable).setColor(context.resources.getColor(data.color))
            }else if(data.images.size == 2){
                Utility.loadImage(itemView.context, data.images[0], siv1)
                Utility.loadImage(itemView.context, data.images[1], siv2)
                siv3.visibility = View.GONE
                tvImageCount.visibility = View.GONE
                siv2.strokeColor = ColorStateList.valueOf(context.resources.getColor(R.color.colorPrimary))
                siv2.strokeWidth = 5.0F
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(-50,50,0,0)
                siv2.layoutParams = params
                /*
                    app:strokeColor="@color/colorPrimary"
                    app:strokeWidth="2dp"
                    android:layout_marginStart="-20dp"
                    android:layout_marginTop="20dp"
                */
            }
        }

    }

}
