package com.c0d3in3.finalproject.ui.dashboard.stories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.MyDiffCallback
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.databinding.StorySmallItemLayoutBinding
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.ui.auth.UserInfo
import kotlinx.android.synthetic.main.add_story_big_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StoryAdapter(
    private val recyclerView: RecyclerView,
    private val callback: CustomStoryCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface CustomStoryCallback {
        fun onStoryClick(position: Int)
        fun onLoadMoreStories()
    }

    companion object{
        const val ADD_STORY_TYPE = 0
        const val DEFAULT_STORY_TYPE = 1
    }

    private var storyList = mutableListOf<ArrayList<StoryModel>>()
    private var usersRepository = UsersRepository()
    private var isLoading = false

    private val visibleThreshold = 3

    init {
        if (storyList.isEmpty() && !isLoading) {
            callback.onLoadMoreStories()
            isLoading = true
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = linearLayoutManager.itemCount
                val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && visibleThreshold < totalItemCount && visibleThreshold >= totalItemCount - lastVisibleItem) {
                    callback.onLoadMoreStories()
                    isLoading = true
                }
            }
        })
    }

    fun setList(list: List<ArrayList<StoryModel>>) {
        if(list.isNotEmpty()){
            val mutableList = list.toMutableList()
            val diffResult = DiffUtil.calculateDiff(MyDiffCallback(mutableList,storyList))
            storyList = mutableList
            for (idx in 0 until storyList.size) {
                if (storyList[idx][0].storyAuthorModel != null || storyList[idx][0].storyAuthorId == "self") continue
                if (idx == storyList.size - 1) getUser(storyList[idx][0], true, diffResult)
                else getUser(storyList[idx][0], false, null)
            }
        }
        else{
            storyList.clear()
            notifyDataSetChanged()
        }
    }

    inner class AddStoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun onBind(){
            Glide.with(itemView).load(UserInfo.userInfo.userProfileImage).into(itemView.addStoryProfileImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADD_STORY_TYPE -> {
                val layout = LayoutInflater.from(parent.context).inflate(R.layout.add_story_big_layout, parent, false)
                AddStoryViewHolder(layout)
            }
            else -> {
                val binding: StorySmallItemLayoutBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.story_small_item_layout,
                    parent,
                    false
                )
                ViewHolder(binding)
            }
        }

    }

    override fun getItemCount() = storyList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolder -> holder.onBind()
            is AddStoryViewHolder ->holder.onBind()
        }
    }

    inner class ViewHolder(private val binding: StorySmallItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var model: StoryModel

        fun onBind() {
            model = storyList[adapterPosition][0]

            if(model.storyAuthorId == UserInfo.userInfo.userId) model.storyAuthorModel?.userFullName = "Your story"
            binding.storyModel = model
            itemView.setOnClickListener { callback.onStoryClick(adapterPosition) }
        }
    }

    private fun getUser(
        model: StoryModel,
        isLast: Boolean,
        diffResult: DiffUtil.DiffResult?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            usersRepository.getUser(model.storyAuthorId).collect { state ->
                when (state) {
                    is State.Success -> {
                        model.storyAuthorModel = state.data!!
                        if(isLast) withContext(Dispatchers.Main){ diffResult?.dispatchUpdatesTo(this@StoryAdapter); isLoading = false}
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (storyList[position][0].storyAuthorId) {
            "self" -> ADD_STORY_TYPE
            else -> DEFAULT_STORY_TYPE
        }
    }
}