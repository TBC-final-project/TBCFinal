package com.c0d3in3.finalproject.ui.post.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.network.model.CommentModel
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.UserInfo
import kotlinx.android.synthetic.main.comment_item_layout.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class CommentAdapter(private val comments: ArrayList<CommentModel>, private val callback: CommentAdapterCallback) : RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    private val usersRepository = UsersRepository()

    interface CommentAdapterCallback{
        fun removeComment(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.comment_item_layout, parent, false))
    }

    override fun getItemCount() = comments.size

    @InternalCoroutinesApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private lateinit var model: CommentModel

        @InternalCoroutinesApi
        fun onBind(){
            model = comments[adapterPosition]

            CoroutineScope(Dispatchers.IO).launch {
                model.commentAuthor?.let {
                    usersRepository.getUser(it).collect { state ->
                        when (state) {

                            is State.Success -> {
                                val model =  state.data
                                if (model != null) {
                                    withContext(Dispatchers.Main){
                                        itemView.fullNameTextView.text = model.userFullName
                                        if (model.userProfileImage.isNotEmpty()) Glide.with(itemView)
                                            .load(model.userProfileImage).into(itemView.profileImageView)
                                        else itemView.profileImageView.setCircleBackgroundColorResource(android.R.color.black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            itemView.commentTextView.text = model.comment
            itemView.timeTextView.text = Utils.getTimeDiffMinimal(model.commentTimestamp)
            if(model.commentAuthor == UserInfo.userInfo.userId) {
                itemView.setOnLongClickListener {
                    callback.removeComment(adapterPosition)
                    true
                }

            }
        }
    }

}