package com.c0d3in3.finalproject

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


class CustomViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        // Never allow swiping to switch between pages
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Never allow swiping to switch between pages
        return false
    }

    override fun executeKeyEvent(event: KeyEvent): Boolean {
        return false
    }

}