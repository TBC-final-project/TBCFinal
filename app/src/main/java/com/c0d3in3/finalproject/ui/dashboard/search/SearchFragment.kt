package com.c0d3in3.finalproject.ui.dashboard.search

import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.BaseFragment

import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.ui.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : BaseFragment() {


    private lateinit var searchViewModel: SearchViewModel

    override fun init() {
        //(activity as DashboardActivity).setToolbarTitle(getString(R.string.search))
    }

    override fun setUpFragment() {



        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        val textView: TextView = rootView!!.text_search
        searchViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
    }

    override fun getLayout() = R.layout.seach_fragment

}
