package com.c0d3in3.finalproject.ui.dashboard.stories

import android.widget.GridLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.StoryModel
import kotlinx.android.synthetic.main.fragment_stories.view.*

class StoriesFragment : BaseFragment(), StoryAdapter.CustomStoryCallback {

    private lateinit var storiesViewModel: StoriesViewModel
    private var adapter : StoryAdapter? = null

    override fun init() {

    }

    override fun setUpFragment() {

        storiesViewModel = ViewModelProvider(this).get(StoriesViewModel::class.java)

        rootView!!.storiesFragmentRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        val list = arrayListOf<ArrayList<StoryModel>>()
        val storyModel = StoryModel()
        storyModel.storyAuthorId = "self"
        list.add(arrayListOf(storyModel))
        adapter = StoryAdapter(rootView!!.storiesFragmentRecyclerView, this)
        adapter!!.setList(list)
        rootView!!.storiesFragmentRecyclerView.adapter = adapter

    }

    override fun getLayout() = R.layout.fragment_stories

    override fun onStoryClick(position: Int) {

    }

    override fun onLoadMoreStories() {

    }
}