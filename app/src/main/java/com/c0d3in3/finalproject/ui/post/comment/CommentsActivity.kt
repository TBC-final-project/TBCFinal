package com.c0d3in3.finalproject.ui.post.comment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.model.CommentModel
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.tools.DialogCallback
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.UserInfo
import kotlinx.android.synthetic.main.activity_comments.*
import kotlin.properties.Delegates

class CommentsActivity : BaseActivity(), CommentAdapter.CommentAdapterCallback {

    private var adapter: CommentAdapter? = null
    private var model: PostModel? = null
    private lateinit var post: PostModel
    private var position by Delegates.notNull<Int>()
    private lateinit var commentViewModel: CommentViewModel

    override fun getLayout() = R.layout.activity_comments

    override fun init() {

        commentViewModel =
            ViewModelProvider(this, CommentViewModelFactory()).get(CommentViewModel::class.java)

        getModel()

        setListeners()

        commentViewModel.getComments().observe(this, Observer {
            if (adapter == null) {
                adapter = CommentAdapter(this)
                commentsRecyclerView.layoutManager = LinearLayoutManager(this)
                commentsRecyclerView.adapter = adapter
            }

            adapter?.setList(it)
        })
    }

    private fun setListeners() {
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
        else {
            post = model as PostModel
            commentViewModel.setPostModel(model!!)
        }


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


    @SuppressLint("SetTextI18n")
    override fun removeComment(position: Int) {
        val desc =
            "${getString(R.string.do_you_really_want_to_delete_comment)} \n${post.postComments?.get(
                position)?.comment}"
        Utils.createOptionalDialog(
            this,
            getString(R.string.comment),
            desc,
            object : DialogCallback {
                override fun onResponse(dialog: Dialog) {
                    commentViewModel.removeComment(position)
                    dialog.dismiss()
                }

                override fun onCancel() {
                }
            }
        )
    }

    fun addComment(v: View) {

        println("shemovidass")
        val comment = CommentModel(
            System.currentTimeMillis(), UserInfo.userInfo.userId, commentEditText.text.toString(),
            arrayListOf(), arrayListOf()
        )
        commentViewModel.addComment(comment)
        commentEditText.text.clear()
    }
}