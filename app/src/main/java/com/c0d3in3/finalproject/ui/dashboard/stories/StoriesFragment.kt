package com.c0d3in3.finalproject.ui.dashboard.stories

import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.BaseFragment
import com.c0d3in3.finalproject.R
import kotlinx.android.synthetic.main.fragment_stories.view.*

class StoriesFragment : BaseFragment() {

    private lateinit var storiesViewModel: StoriesViewModel

    override fun init() {
        //(activity as DashboardActivity).setToolbarTitle(getString(R.string.stories))
    }

    override fun setUpFragment() {


        storiesViewModel = ViewModelProvider(this).get(StoriesViewModel::class.java)
        val textView: TextView = rootView!!.text_dashboard
        storiesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
    }

    override fun getLayout() = R.layout.fragment_stories
}
