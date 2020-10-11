package com.naveenkumar.offlinetask2.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.naveenkumar.offlinetask2.R

class Utility {

    companion object {
        private var utils: Utility? = null
        var toast: Toast? = null

        fun makeToast(context: Context, message: String?) {
            if (toast != null)
                toast!!.cancel()

            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast!!.show()
        }
        fun log(message: String) {
            Log.e("Utility", "log: $message")
        }
        fun log(message: String, TAG: String) {
            Log.e(TAG, "log: $message")
        }
        private var exit = false

        fun onBack(activity: Activity) {
            if (exit) {
                activity.finish()
            } else {
                makeToast(activity, "Press Back again to Exit.")
                exit = true
                Handler(Looper.myLooper()!!).postDelayed({ exit = false }, 3 * 1000.toLong())
            }
        }

        fun options(): RequestOptions {
            return RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.businessman)
                .error(R.drawable.businessman)
        }

        fun loadImage(context: Context, url: String, imageView: ImageView) {
            Glide.with(context)
                .applyDefaultRequestOptions(options())
                .load(url)
                .into(imageView)
        }


        var dialog: Dialog? = null
        fun showProgress(context: Context) {
            dialog = Dialog(context)
            dialog!!.setContentView(R.layout.custom_loader)
            dialog!!.window
                ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val image: ImageView = dialog!!.findViewById(R.id.img_loader)
            Glide.with(context).load(R.drawable.fog).into(image)
            dialog!!.setCancelable(false)
            dialog!!.show()
        }

        fun close() {
            dialog?.dismiss()
        }
    }

}