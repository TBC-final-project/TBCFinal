package com.c0d3in3.finalproject

import androidx.recyclerview.widget.RecyclerView

data class ListHolder<T>(val list: MutableList<T> = mutableListOf()) {
    var indexChanged: Int = -1
    private var updateType: UpdateType? = null

//...

    fun applyChange(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        updateType?.notifyChange(adapter, indexChanged)
    }
}