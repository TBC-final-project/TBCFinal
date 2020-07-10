package com.c0d3in3.finalproject.ui.dashboard.search

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.base.BaseFragment

import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.UserModel
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_search.view.searchEditText

class SearchFragment : BaseFragment(), SearchAdapter.SearchAdapterCallback {



    private lateinit var searchViewModel: SearchViewModel
    private var searchList = arrayListOf<UserModel>()
    private var adapter: SearchAdapter? = null

    override fun getLayout() = R.layout.fragment_search

    override fun init() {

    }

    override fun setUpFragment() {

        if(adapter == null){
            adapter = SearchAdapter(this)
            rootView!!.searchRecyclerView.adapter = adapter
        }

        searchViewModel = ViewModelProvider(this, SearchViewModelFactory()).get(SearchViewModel::class.java)

        searchViewModel.getSearchList().observe(viewLifecycleOwner, Observer {
            if(adapter != null && it != null){
                searchList = it
                adapter!!.setList(searchList)
            }
        })

        setListeners()
    }

    private fun setListeners(){
        rootView!!.searchEditText.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isBlank()) searchViewModel.clearList()
                searchViewModel.searchUser(s.toString())
            }

        })
    }

    override fun onSearchItemClick(position: Int) {

    }

}