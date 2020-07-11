package com.c0d3in3.finalproject.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.c0d3in3.finalproject.MyDiffCallback
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.databinding.PostImageItemLayoutBinding
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.bean.PostModel
import kotlinx.android.synthetic.main.post_image_item_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostsAdapter(private val callback: CustomPostCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var posts = mutableListOf<PostModel>()
    private val usersRepository = UsersRepository()

    interface CustomPostCallback {
        fun onLikeButtonClick(position: Int)
        fun onCommentButtonClick(position: Int)
        fun openDetailedPost(position: Int)
    }

    fun updateSingleItem(model: PostModel,position: Int){
        posts[position] = model
        notifyItemChanged(position)
    }

    fun setPostsList(list: List<PostModel>) {

        if (list.isNotEmpty()) {
            val mutableList = list.toMutableList()
            if(mutableList.size > posts.size){
                val diffResult = DiffUtil.calculateDiff(
                    MyDiffCallback(mutableList, posts)
                )
                posts = mutableList
                for (idx in 0 until posts.size) {
                    if (posts[idx].postAuthorModel != null) continue
                    if (idx == posts.size - 1) getUser(posts[idx], true, diffResult)
                    else getUser(posts[idx], false, null)
                }
            }

        }
        else{
            posts.clear()
            notifyDataSetChanged()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.post_image_item_layout,
                parent,
                false
            )
        )
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

    private fun getUser(
        model: PostModel,
        isLast: Boolean,
        diffResult: DiffUtil.DiffResult?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            usersRepository.getUser(model.postAuthor!!).collect { state ->
                when (state) {
                    is State.Success -> {
                        model.postAuthorModel = state.data!!
                        if (isLast) withContext(Dispatchers.Main) {
                            diffResult?.dispatchUpdatesTo(
                                this@PostsAdapter
                            )
                        }
                    }
                }
            }
        }
    }

}