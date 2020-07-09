package com.c0d3in3.finalproject.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.Constants.VIEW_TYPE_LOADING
import com.c0d3in3.finalproject.Constants.VIEW_TYPE_WALL_ITEM
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.databinding.PostImageItemLayoutBinding
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.UserInfo
import kotlinx.android.synthetic.main.post_image_item_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostsAdapter(private val recyclerView: RecyclerView? = null, private val callback: CustomPostCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var posts = arrayListOf<PostModel>()
    private val usersRepository = UsersRepository()
    private var isLoading = false
    private val visibleThreshold = 5
    interface CustomPostCallback {
        fun onLikeButtonClick(position: Int)
        fun onCommentButtonClick(position: Int)
        fun openDetailedPost(position: Int)
        fun onLoadMore()
    }

    init {
        if(posts.isEmpty() && !isLoading) {
            callback.onLoadMore()
            isLoading = true
        }
        recyclerView?.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = linearLayoutManager.itemCount
                val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    callback.onLoadMore()
                    isLoading = true
                }
            }
        })
    }

    fun setPostsList(list: ArrayList<PostModel>) {
        posts = list
        for (idx in 0 until posts.size) {
            if(posts[idx].postAuthorModel != null) continue
            if (idx == posts.size - 1) getUser(posts[idx], true)
            else getUser(posts[idx], false)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_WALL_ITEM -> ViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.post_image_item_layout,
                parent,
                false))
            else -> LoadMorePostsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recyclerview_load_layout, parent, false)
            )
        }
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> holder.onBind()
        }
    }

    inner class ViewHolder(private val binding: PostImageItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        lateinit var model: PostModel

        fun onBind() {
            model = posts[adapterPosition]

            binding.postModel = model

            itemView.postHeaderLayout.setOnClickListener(this)
            itemView.postImageView.setOnClickListener(this)
            itemView.commentButton.setOnClickListener(this)
            itemView.likeButton.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                itemView.postImageView.id, itemView.postHeaderLayout.id -> callback.openDetailedPost(
                    adapterPosition
                )
                itemView.commentButton.id -> callback.onCommentButtonClick(adapterPosition)
                itemView.likeButton.id -> callback.onLikeButtonClick(adapterPosition)

            }
        }


    }

    private fun getUser(model: PostModel, isLast: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            usersRepository.getUser(model.postAuthor!!).collect { state ->
                when (state) {
                    is State.Success -> {
                        model.postAuthorModel = state.data!!
                        if (isLast) withContext(Dispatchers.Main) {
                            notifyDataSetChanged()
                            isLoading = false
                        }
                    }
                }
            }
        }
    }

    inner class LoadMorePostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun getItemViewType(position: Int): Int {
        return when {
            posts[position].isLast -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_WALL_ITEM
        }
    }

}