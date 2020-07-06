package com.c0d3in3.finalproject.ui.post.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.model.CommentModel
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.UserInfo
import kotlinx.android.synthetic.main.comment_item_layout.view.*

class CommentAdapter(private val comments: ArrayList<CommentModel>, private val callback: CommentAdapterCallback) : RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    interface CommentAdapterCallback{
        fun removeComment(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.comment_item_layout, parent, false))
    }

    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private lateinit var model: CommentModel
        fun onBind(){
            model = comments[adapterPosition]

            itemView.fullNameTextView.text = model.commentAuthor?.userFullName
            itemView.commentTextView.text = model.comment
            itemView.timeTextView.text = "${Utils.getTimeDiffMinimal(model.commentTimestamp)} ago"

            if(model.commentAuthor!!.userId == UserInfo.userInfo.userId) {
                itemView.setOnLongClickListener {
                    callback.removeComment(adapterPosition)
                    true
                }

            }
        }
    }

}