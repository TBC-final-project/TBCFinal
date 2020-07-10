package com.c0d3in3.finalproject.ui.dashboard.stories

import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.R
import kotlinx.android.synthetic.main.fragment_stories.view.*

class StoriesFragment : BaseFragment() {

    private lateinit var storiesViewModel: StoriesViewModel
    private var adapter : StoryAdapter? = null

    override fun init() {

    }

    override fun setUpFragment() {

        storiesViewModel = ViewModelProvider(this).get(StoriesViewModel::class.java)

    }

    override fun getLayout() = R.layout.fragment_stories
}