package com.c0d3in3.finalproject.ui.post.comment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log.d
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.FirebaseHandler.POSTS_REF
import com.c0d3in3.finalproject.network.model.CommentModel
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.ui.auth.UserInfo
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.dialog_error_layout.dialogDescriptionTV
import kotlinx.android.synthetic.main.dialog_error_layout.dialogTitleTV
import kotlinx.android.synthetic.main.dialog_remove_layout.*
import kotlin.properties.Delegates

class CommentsActivity : BaseActivity(), CommentAdapter.CommentAdapterCallback {

    private lateinit var adapter: CommentAdapter
    private var model: PostModel? = null
    private lateinit var post: PostModel
    private var position by Delegates.notNull<Int>()
    private lateinit var commentViewModel: CommentViewModel

    override fun getLayout() = R.layout.activity_comments

    override fun init() {

        getModel()

        commentViewModel =
            ViewModelProvider(this, CommentViewModelFactory()).get(CommentViewModel::class.java)

        setListeners()

        adapter = if (post.postComments != null)
            CommentAdapter(post.postComments!!, this)
        else
            CommentAdapter(arrayListOf(), this)
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        commentsRecyclerView.adapter = adapter
    }

    private fun setListeners() {
        addCommentButton.setOnClickListener {
            if (commentEditText.text.isNotBlank()) addComment()
        }

        commentEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) addCommentButton.setTextColor(
                    ContextCompat.getColor(
                        this@CommentsActivity,
                        R.color.colorBlue
                    )
                )
                else addCommentButton.setTextColor(
                    ContextCompat.getColor(
                        this@CommentsActivity,
                        R.color.colorLightBlue
                    )
                )
            }

        })
    }

    private fun getModel() {
        model = intent.getParcelableExtra("model")
        position = intent.getIntExtra("position", -1)



        if (model == null || position == -1) finish()
        else commentViewModel.getPostModel(model!!)

        if (post.postComments == null) post.postComments = arrayListOf()

        initToolbar("${post.postAuthorModel?.userFullName}'s post")

        if (UserInfo.userInfo.userProfileImage.isNotEmpty()) Glide.with(applicationContext)
            .load(UserInfo.userInfo.userProfileImage).into(profileImageView)
        else profileImageView.setCircleBackgroundColorResource(android.R.color.black)
    }
    //override fun getToolbarTitle() = "${post.postAuthor?.userFullName}'s post"

    override fun onBackPressed() {
        val mIntent = Intent()
        mIntent.putExtra("model", post)
        mIntent.putExtra("position", position)
        setResult(Activity.RESULT_OK, mIntent)
        super.onBackPressed()
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

        dialog.dialogDescriptionTV.text =
            "${getString(R.string.do_you_really_want_to_delete_comment)} \n${post.postComments?.get(
                position
            )?.comment}"
        dialog.dialogTitleTV.text = getString(R.string.comment)

        dialog.noDialogButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.yesDialogButton.setOnClickListener {
            post.postComments!!.removeAt(position)
            FirebaseHandler.getDatabase().collection(POSTS_REF).document(post.postId)
                .update("postComments", post.postComments).addOnSuccessListener {
                    adapter.notifyItemRemoved(position)
                }
            dialog.dismiss()
        }

        dialog.show()
    }

    fun addComment(view: View) {

        val comment = CommentModel(
            System.currentTimeMillis(), UserInfo.userInfo.userId, commentEditText.text.toString(),
            arrayListOf(), arrayListOf()
        )
        commentViewModel.addComment(comment)
        post.postComments!!.add(comment)
        commentEditText.text.clear()
    }
}
