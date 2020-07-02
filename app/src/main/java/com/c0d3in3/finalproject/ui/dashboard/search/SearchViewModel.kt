package com.c0d3in3.finalproject.ui.dashboard.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is search Fragment"
    }
    val text: LiveData<String> = _text
}
