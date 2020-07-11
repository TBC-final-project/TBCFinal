package com.c0d3in3.finalproject.ui.post.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.databinding.CommentItemLayoutBinding
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.bean.CommentModel
import com.c0d3in3.finalproject.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentAdapter(private val callback: CommentAdapterCallback) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private var comments = arrayListOf<CommentModel>()
    private val usersRepository = UsersRepository()

    interface CommentAdapterCallback {
        fun removeComment(position: Int)
    }

    fun setList(comments: ArrayList<CommentModel>) {
        this.comments = comments
        notifyDataSetChanged()
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

            getUser(model, adapterPosition)
            binding.commentModel = model
            if (model.commentAuthor == UserInfo.userInfo.userId) {
                itemView.setOnLongClickListener {
                    callback.removeComment(adapterPosition)
                    true
                }
            }
        }

        private fun getUser(model: CommentModel, position: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                if (model.commentAuthorModel == null) {
                    usersRepository.getUser(model.commentAuthor!!).collect { state ->
                        when (state) {
                            is State.Success -> {
                                withContext(Dispatchers.Main){
                                    model.commentAuthorModel = state.data
                                    notifyItemChanged(position)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}