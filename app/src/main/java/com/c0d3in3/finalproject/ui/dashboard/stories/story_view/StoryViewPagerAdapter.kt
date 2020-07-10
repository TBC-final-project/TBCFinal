package com.c0d3in3.finalproject.ui.dashboard.stories.story_view

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.c0d3in3.finalproject.CustomProgressBar
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.StoryModel
import kotlinx.android.synthetic.main.story_base_layout.view.*
import kotlinx.android.synthetic.main.story_progressbar_layout.view.storyTimeProgressBar


class StoryViewPagerAdapter(
    private val ctx: Context,
    private val list: ArrayList<ArrayList<StoryModel>>,
    private val callback: ViewPagerInterface) : PagerAdapter() {

    private val gestureDetector = GestureDetector(ctx,
        SingleTapConfirm()
    )

    private var currentPosition = 0
    var defaultPosition = 0
    private val viewContainer = arrayListOf<View>()

    interface ViewPagerInterface {
        fun nextViewPagerItem(): Boolean
        fun previousViewPagerItem(): Boolean
    }

    override fun isViewFromObject(view: View, itemView: Any): Boolean {
        return view == itemView
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(ctx)
            .inflate(R.layout.story_base_layout, container, false) as LinearLayout

        view.storyListRecyclerView.layoutManager =
            LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        val storyAdapter =
            StoryRecyclerViewAdapter(
                list[position],
                0
            )
        view.storyListRecyclerView.adapter = storyAdapter

        view.progressBarRecyclerView.layoutManager = GridLayoutManager(ctx, list[position].size)
        val adapter =
            StoryRecyclerViewAdapter(
                list[position],
                1
            )
        view.progressBarRecyclerView.adapter = adapter

        view.storyListRecyclerView.setOnTouchListener { v, event ->
            if (gestureDetector.onTouchEvent(event)) onSingleTapUp(
                v as RecyclerView, event
            )
            true
        }
        container.addView(view)
        viewContainer.add(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as LinearLayout)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }


    private fun onSingleTapUp(
        view: RecyclerView,
        e: MotionEvent
    ): Boolean {
        val viewWidth: Int = view.width
        // RIGHT SIDE SCREEN
        if (e.x > viewWidth * 0.7) {
            nextViewPagerItem()
        }
        // LEFT SIDE SCREEN
        if (e.x < viewWidth * 0.3) {
            previousViewPagerItem()
        }
        return true
    }

    override fun getCount() = list.size

    private fun nextViewPagerItem() {
        println("nextViewPagerItem $currentPosition")
        val currentView = viewContainer[currentPosition]
        val recyclerView = currentView.storyListRecyclerView
        val progressBarRecyclerView = currentView.progressBarRecyclerView
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val visibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
        if (recyclerView.adapter!!.itemCount - 1 > visibleItem) {
            progressBarRecyclerView[visibleItem].storyTimeProgressBar.cancelProgress()
            progressBarRecyclerView[visibleItem].storyTimeProgressBar.progress = 9999
            setProgressBar(
                progressBarRecyclerView[visibleItem + 1].storyTimeProgressBar
            )
            recyclerView.scrollToPosition(visibleItem + 1)
        } else {
            progressBarRecyclerView[visibleItem].storyTimeProgressBar.cancelProgress()
            callback.nextViewPagerItem()
        }
    }

    private fun previousViewPagerItem() {
        val currentView = viewContainer[currentPosition]
        val recyclerView = currentView.storyListRecyclerView
        val progressBarRecyclerView = currentView.progressBarRecyclerView
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val visibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
        if (0 < visibleItem) {
            progressBarRecyclerView[visibleItem].storyTimeProgressBar.cancelProgress()
            setProgressBar(
                progressBarRecyclerView[visibleItem - 1].storyTimeProgressBar
            )
            recyclerView.scrollToPosition(visibleItem - 1)
        } else {
            progressBarRecyclerView[visibleItem].storyTimeProgressBar.cancelProgress()
            callback.previousViewPagerItem()
        }
    }


    fun updateCurrentItem(position: Int) {
        currentPosition = position
        if(currentPosition >= viewContainer.size || defaultPosition == position) currentPosition = 0
        if(defaultPosition != 0 && position != defaultPosition) currentPosition += 1

        println("currentPosition $currentPosition defaultPosition $defaultPosition position $position")
        val currentView = viewContainer[currentPosition]
        for(idx in 0 until currentView.storyListRecyclerView.adapter!!.itemCount){
            currentView.progressBarRecyclerView[idx].storyTimeProgressBar.cancelProgress()
        }

        currentView.storyListRecyclerView.scrollToPosition(0)
        setProgressBar(currentView.progressBarRecyclerView[0].storyTimeProgressBar)
    }

    open class SingleTapConfirm : SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            return true
        }
    }

    private fun setProgressBar(pb: CustomProgressBar) {
        pb.startProgress()

        pb.setOnProgressBarChangeListener(object : CustomProgressBar.OnMyProgressBarChangeListener {
            override fun onProgressChanged(myProgressBar: CustomProgressBar?) {
                if (myProgressBar != null) {
                    if (myProgressBar.progress == 10000) {
                        myProgressBar.cancelProgress()
                        nextViewPagerItem()
                    }
                }
            }

        })
    }
}
