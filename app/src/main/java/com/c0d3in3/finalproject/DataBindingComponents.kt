package com.c0d3in3.finalproject

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.c0d3in3.finalproject.tools.Utils

object DataBindingComponents {
    @JvmStatic
    @BindingAdapter("setLikeButtonResource")
    fun setLikeButtonResource(view: ImageView, arrayList: ArrayList<String>) {
        val result = Utils.checkLike(arrayList)
        if (result >= 0) view.setImageResource(R.mipmap.ic_favorited)
        else view.setImageResource(R.mipmap.ic_unfavorite)
    }

    @JvmStatic
    @BindingAdapter("arrayList", "textCount")
    fun<T> setTextCount(view: TextView, arrayList: ArrayList<T>, textCount: Char) {
        val mText: String = if(textCount == 'l') "likes"
        else "comments"
        view.text = "${arrayList.size} $mText"
    }


}