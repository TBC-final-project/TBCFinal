package com.c0d3in3.finalproject.ui.post.comment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.FirebaseHandler.POSTS_REF
import com.c0d3in3.finalproject.network.model.CommentModel
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.ui.auth.UserInfo
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.dialog_error_layout.*
import kotlinx.android.synthetic.main.dialog_error_layout.dialogDescriptionTV
import kotlinx.android.synthetic.main.dialog_error_layout.dialogTitleTV
import kotlinx.android.synthetic.main.dialog_remove_layout.*

class CommentsActivity : AppCompatActivity(), CommentAdapter.CommentAdapterCallback {

    private lateinit var adapter: CommentAdapter
    private lateinit var post : PostModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        init()
    }

    private fun init(){

        post = intent.extras!!.getParcelable("post")!!

        addCommentButton.setOnClickListener {
            if(commentEditText.text.isNotBlank()) addComment()
        }

        commentEditText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()) addCommentButton.setTextColor(ContextCompat.getColor(this@CommentsActivity, R.color.colorBlue))
                else addCommentButton.setTextColor(ContextCompat.getColor(this@CommentsActivity, R.color.colorLightBlue))
            }

        })
        adapter =
            CommentAdapter(post.postComments!!, this)

        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        commentsRecyclerView.adapter = adapter
    }

    private fun addComment(){
        val comment = CommentModel(System.currentTimeMillis(), UserInfo.userInfo, commentEditText.text.toString(), arrayListOf(), arrayListOf())
        post.postComments!!.add(comment)
        FirebaseHandler.getDatabase().collection(POSTS_REF).document(post.postId).update("postComments", post.postComments).addOnSuccessListener {
            commentEditText.text.clear()
            adapter.notifyItemInserted(post.postComments!!.size-1)
            commentsRecyclerView.smoothScrollToPosition(adapter.itemCount-1)
        }
    }
    override fun removeComment(position: Int) {
        val dialog = Dialog(this)

        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(R.layout.dialog_remove_layout)

        val params = dialog.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params

        dialog.dialogDescriptionTV.text = "Do you really want to delete comment: \n${post.postComments?.get(position)?.comment}"
        dialog.dialogTitleTV.text = "Comment"

        dialog.noDialogButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.yesDialogButton.setOnClickListener {
            post.postComments!!.removeAt(position)
            FirebaseHandler.getDatabase().collection(POSTS_REF).document(post.postId).update("postComments", post.postComments).addOnSuccessListener {
                adapter.notifyItemRemoved(position)
            }
            dialog.dismiss()
        }

        dialog.show()
    }
}
