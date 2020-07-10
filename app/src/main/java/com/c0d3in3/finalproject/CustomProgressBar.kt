package com.c0d3in3.finalproject

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar

class CustomProgressBar : ProgressBar {

    private var listener: OnMyProgressBarChangeListener? = null
    private var objectAnimator = ObjectAnimator.ofInt(this, "progress", this.progress, 10000)

    fun setOnProgressBarChangeListener(l: OnMyProgressBarChangeListener) {
         listener = l
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    override fun setProgress(progress: Int) {
        super.setProgress(progress)

        listener?.onProgressChanged(this)
    }

    interface OnMyProgressBarChangeListener {
        fun onProgressChanged(myProgressBar: CustomProgressBar?)
    }

    fun startProgress() {
        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                println("start")
            }

            override fun onAnimationEnd(animation: Animator?) {
                println("ended")
            }

            override fun onAnimationCancel(animation: Animator?) {
                animation?.apply { removeAllListeners() }
            }

            override fun onAnimationRepeat(animation: Animator?) {

            }
        })
        objectAnimator.apply {
            duration = (6000).toLong()
            start()
        }
    }


    fun cancelProgress() {
        this.progress = 0
        objectAnimator.apply {
            cancel()
        }
    }
}