package com.c0d3in3.finalproject.ui.dashboard.stories

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.c0d3in3.finalproject.CustomProgressBar
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.databinding.StoryImageItemLayoutBinding
import com.c0d3in3.finalproject.tools.Utils
import kotlinx.android.synthetic.main.story_image_item_layout.view.*

class StoryRecyclerViewAdapter(private val storyList: ArrayList<StoryModel>, private val mvViewType: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        private const val VIEW_TYPE_STORY = 0
        private const val VIEW_TYPE_PROGRESS_BAR = 1
    }

    inner class ViewHolder(private val binding: StoryImageItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){

        fun onBind(){
            binding.storyModel = storyList[adapterPosition]
            binding.storyAuthorModel = storyList[0].storyAuthorModel
        }
    }

    inner class ProgressBarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun onBind(){
            val px = Utils.convertDp(10.toFloat())
            val lp = itemView.layoutParams as GridLayoutManager.LayoutParams
            lp.width = Utils.getScreenWidth() / storyList.size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_STORY -> ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.story_image_item_layout, parent, false))
            else -> ProgressBarViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.story_progressbar_layout, parent, false))
        }
    }

    override fun getItemCount() = storyList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolder -> holder.onBind()
            is ProgressBarViewHolder -> holder.onBind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (mvViewType) {
            0 -> {
                VIEW_TYPE_STORY
            }
            else -> VIEW_TYPE_PROGRESS_BAR
        }
    }
}