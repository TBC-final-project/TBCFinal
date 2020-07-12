package com.c0d3in3.finalproject.ui.post.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.MyDiffCallback
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.databinding.CommentItemLayoutBinding
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.bean.CommentModel
import kotlinx.android.synthetic.main.comment_item_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentAdapter(private val callback: CommentAdapterCallback) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private var comments = mutableListOf<CommentModel>()
    private val usersRepository = UsersRepository()

    interface CommentAdapterCallback {
        fun removeComment(position: Int)
        fun likeComment(position: Int)
    }

    fun setList(list: ArrayList<CommentModel>) {
        if (list.isNotEmpty()) {
            val mutableList = list.toMutableList()
            val diffResult = DiffUtil.calculateDiff(
                MyDiffCallback(mutableList, comments)
            )
            comments = mutableList
            for (idx in 0 until comments.size) {
                if (comments[idx].commentAuthorModel != null && idx != comments.size-1) continue
                if (idx == comments.size - 1) getUser(comments[idx], true, diffResult)
                else getUser(comments[idx], false, null)
            }
        }
        else{
            comments.clear()
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CommentItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.comment_item_layout,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(private val binding: CommentItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var model: CommentModel

        fun onBind() {
            model = comments[adapterPosition]

            binding.commentModel = model

            if (model.commentAuthor == App.getCurrentUser().userId) {
                itemView.setOnLongClickListener {
                    callback.removeComment(adapterPosition)
                    true
                }
            }

            itemView.likeButton.setOnClickListener {
                callback.likeComment(adapterPosition)
            }
        }
    }

    private fun getUser(
        model: CommentModel,
        isLast: Boolean,
        diffResult: DiffUtil.DiffResult?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            usersRepository.getUser(model.commentAuthor!!).collect { state ->
                when (state) {
                    is State.Success -> {
                        model.commentAuthorModel = state.data!!
                        if (isLast) withContext(Dispatchers.Main) {
                            diffResult?.dispatchUpdatesTo(this@CommentAdapter
                            )
                        }
                    }
                }
            }
        }
    }
}