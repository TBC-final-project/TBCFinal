package com.c0d3in3.finalproject

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.UserInfo
import de.hdodenhof.circleimageview.CircleImageView

object DataBindingComponents {
    @JvmStatic
    @BindingAdapter("setLikeButtonResource")
    fun setLikeButtonResource(view: ImageView, arrayList: ArrayList<String>?) {
        if(arrayList != null){
            if (UserInfo.userInfo.userId in arrayList) view.setImageResource(R.mipmap.ic_favorited)
            else view.setImageResource(R.mipmap.ic_unfavorite)
        }
    }


    @JvmStatic
    @BindingAdapter("setImage")
    fun setImage(view: ImageView, url: String?){
        println(url)
         if(url != null) Glide.with(view.context).load(url).into(view)
    }


    @JvmStatic
    @BindingAdapter("getTimeDiff")
    fun getTimeDiff(view: TextView, timestamp : Long?){
        if(timestamp != null){
            val currentStamp = System.currentTimeMillis()
            val diff = currentStamp - timestamp
            view.text = when {
                diff < 60000 -> "${diff / 1000} second ago"
                diff in 60001 until 3600000 -> "${diff / 60000} minutes ago"
                diff in 3600001 until 86400000 -> "${diff / 3600000} hours ago"
                diff in 86400001 until 2678400000 -> "${diff / 86400000} days ago"
                diff in 2678400001 until 32140800000 -> "${diff / 2678400000} months ago"
                else -> "more than year ago"
            }
        }
    }

    @JvmStatic
    @BindingAdapter("getTimeDiffMinimal")
    fun getTimeDiffMinimal(view: TextView, timestamp : Long?) {
        if (timestamp != null) {
            val currentStamp = System.currentTimeMillis()
            val diff = currentStamp - timestamp
            view.text = when {
                diff < 60000 -> "${diff / 1000}s"
                diff in 60001 until 3600000 -> "${diff / 60000}m"
                diff in 3600001 until 86400000 -> "${diff / 3600000}h"
                diff in 86400001 until 2678400000 -> "${diff / 86400000}d"
                diff in 2678400001 until 32140800000 -> "${diff / 2678400000}m"
                else -> "1y"
            }
        }
    }

}