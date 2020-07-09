package com.c0d3in3.finalproject.ui.post.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.databinding.CommentItemLayoutBinding
import com.c0d3in3.finalproject.network.model.CommentModel
import com.c0d3in3.finalproject.ui.auth.UserInfo

class CommentAdapter(private val callback: CommentAdapterCallback) : RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    private var comments = arrayListOf<CommentModel>()

    interface CommentAdapterCallback{
        fun removeComment(position: Int)
    }

    fun setList(comments : ArrayList<CommentModel>){
        this.comments = comments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : CommentItemLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.comment_item_layout, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(private val binding: CommentItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        private lateinit var model: CommentModel

        fun onBind(){
            model = comments[adapterPosition]

            binding.commentModel = model
            if(model.commentAuthor == UserInfo.userInfo.userId) {
                itemView.setOnLongClickListener {
                    callback.removeComment(adapterPosition)
                    true
                }
            }
        }
    }
}