package com.c0d3in3.finalproject.ui.dashboard.stories

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.c0d3in3.finalproject.CustomGridLayoutManager
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.ui.dashboard.DashboardActivity
import com.c0d3in3.finalproject.ui.dashboard.stories.story_view.StoryViewActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_stories.view.*

class StoriesFragment : BaseFragment(), StoryAdapter.CustomStoryCallback {

    private lateinit var storiesViewModel: StoriesViewModel
    private var adapter : StoryAdapter? = null
    private lateinit var stories: ArrayList<ArrayList<StoryModel>>

    override fun init() {
        storiesViewModel = ViewModelProvider(this, StoriesViewModelFactory()).get(StoriesViewModel::class.java)

        rootView!!.storiesFragmentRecyclerView.layoutManager = CustomGridLayoutManager(activity, 2)
        adapter = StoryAdapter(rootView!!.storiesFragmentRecyclerView, this, true)
        rootView!!.storiesFragmentRecyclerView.adapter = adapter

        storiesViewModel.getStoriesList().observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                stories = it
                adapter!!.setList(stories)
                (activity as DashboardActivity).sendStoryList(stories)
            }
        })
    }

    override fun setUpFragment() {
    }

    override fun getLayout() = R.layout.fragment_stories

    override fun onStoryClick(position: Int) {
        if(position != 0){
            val intent = Intent(activity, StoryViewActivity::class.java)
            val listJson = Gson().toJson(stories)
            intent.putExtra("storiesList", listJson)
            intent.putExtra("position", position)
            startActivity(intent)
        }
    }

    override fun onLoadMoreStories() {
        if (stories.isNotEmpty())
            storiesViewModel.loadStories(false)
    }

    override fun scrollToPosition(position: Int) {
    }

    fun setStoryList(list: ArrayList<ArrayList<StoryModel>>){
        stories = list
        adapter?.setList(list)
    }
}