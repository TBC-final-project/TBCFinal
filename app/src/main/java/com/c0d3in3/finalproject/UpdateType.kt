package com.c0d3in3.finalproject

import androidx.recyclerview.widget.RecyclerView

enum class UpdateType {


    INSERT {
        override fun notifyChange(
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            indexChanged: Int
        ) {

        }
    },
    REMOVE {
        override fun notifyChange(
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            indexChanged: Int
        ) {

        }
    },
    CHANGE {
        override fun notifyChange(
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            indexChanged: Int
        ) {

        }
    };


    abstract fun notifyChange(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, indexChanged: Int)

}