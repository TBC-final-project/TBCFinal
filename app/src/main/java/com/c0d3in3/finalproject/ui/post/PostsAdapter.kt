package com.c0d3in3.finalproject.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.tools.Utils
import kotlinx.android.synthetic.main.post_image_item_layout.view.*

class PostsAdapter(private val posts: ArrayList<PostModel>, private val callback : CustomPostCallback) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    interface CustomPostCallback{
        fun onLikeButtonClick()
        fun onCommentButtonClick()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.post_image_item_layout, parent, false))
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        lateinit var model : PostModel

        fun onBind(){
            model = posts[adapterPosition]

            itemView.timeTextView.text = Utils.getTimeDiffMinimal(model.postTimestamp)
            itemView.titleTextView.text = model.postTitle
            itemView.fullNameTextView.text = model.postAuthor?.userFullName

            itemView.commentCountTextView.text = "${model.postComments?.size ?: 0}"

            itemView.likeCountTextView.text = "${model.postLikes?.size ?: 0}"

            itemView.commentButton.setOnClickListener {
                callback.onCommentButtonClick()
            }

            itemView.likeButton.setOnClickListener {

            }
        }
    }
}