package com.c0d3in3.finalproject.base

//import android.view.View
//import androidx.recyclerview.widget.RecyclerView
//import com.c0d3in3.finalproject.R
//import com.c0d3in3.finalproject.bean.BaseCallback
//import com.c0d3in3.finalproject.bean.PostModel
//import com.c0d3in3.finalproject.bean.StoryModel
//
//object BaseViewHolderFactory {
//    fun create(view: View, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            R.layout.item_data -> DataViewHolder(view)
//            R.layout.item_other_data -> DataViewHolder.OtherDataViewHolder(view)
//            else -> throw Exception("Wrong view type")
//        }
//    }
//
//    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
//        BaseRecyclerViewAdapter.Binder<PostModel> {
//
//        override fun bind(data: PostModel, listener: BaseCallback<PostModel>?) {
//            TODO("Not yet implemented")
//        }
//    }
//
//    class OtherDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
//        BaseRecyclerViewAdapter.Binder<StoryModel> {
//        override fun bind(data: StoryModel, listener: BaseCallback<StoryModel>?) {
//            TODO("Not yet implemented")
//        }
//    }
//
//}