package com.c0d3in3.finalproject.ui.post

import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.tools.Utils
import kotlinx.android.synthetic.main.post_image_item_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostsAdapter(private val callback: CustomPostCallback) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    private var posts = arrayListOf<PostModel>()

    interface CustomPostCallback {
        fun onLikeButtonClick(position: Int)
        fun onCommentButtonClick(position: Int)
        fun openDetailedPost(position: Int)
    }

    fun setPostsList(list: ArrayList<PostModel>) {
        posts = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.post_image_item_layout, parent, false)
        )
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        lateinit var model: PostModel

        fun onBind() {
            model = posts[adapterPosition]

            CoroutineScope(Dispatchers.Main).launch {
                val likePos = withContext(Dispatchers.Default) {
                    posts[adapterPosition].postLikes?.let { Utils.checkLike(it) }
                }
                if (likePos != null) {
                    if (likePos >= 0)
                        itemView.likeButton.setImageResource(R.mipmap.ic_favorited)
                    else
                        itemView.likeButton.setImageResource(R.mipmap.ic_unfavorite)
                }
            }

            itemView.timeTextView.text = Utils.getTimeDiffMinimal(model.postTimestamp)
            itemView.titleTextView.text = model.postTitle
            itemView.fullNameTextView.text = model.postAuthor?.userFullName

            itemView.commentCountTextView.text = "${model.postComments?.size ?: 0}"
            itemView.likeCountTextView.text = "${model.postLikes?.size ?: 0}"

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
}
