package com.c0d3in3.finalproject.ui.post.comment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.CustomLinearLayoutManager
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.bean.CommentModel
import com.c0d3in3.finalproject.bean.NotificationModel
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.databinding.ActivityCommentsBinding
import com.c0d3in3.finalproject.extensions.setListenerColor
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.tools.DialogCallback
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.profile.ProfileActivity
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_comments.*
import kotlin.properties.Delegates

class CommentsActivity : BaseActivity(), CommentAdapter.CommentAdapterCallback {

    private var adapter: CommentAdapter? = null
    private var model: PostModel? = null
    private lateinit var post: PostModel
    private var position by Delegates.notNull<Int>()
    private lateinit var commentViewModel: CommentViewModel
    private lateinit var listener: ListenerRegistration

    override fun getLayout() = R.layout.activity_comments

    override fun init() {

        commentViewModel =
            ViewModelProvider(this, CommentViewModelFactory()).get(CommentViewModel::class.java)

        getModel()

        val binding: ActivityCommentsBinding = DataBindingUtil.setContentView(this, getLayout())
        binding.postModel = post


        initToolbar("${post.postAuthorModel?.userFullName}'s post")

        if (App.getCurrentUser().userProfileImage.isNotEmpty()) Glide.with(applicationContext)
            .load(App.getCurrentUser().userProfileImage).into(profileImageView)
        else profileImageView.setImageResource(R.mipmap.img_profile)

        setListeners()


        if (adapter == null) {
            adapter = CommentAdapter(this)
            commentsRecyclerView.layoutManager = CustomLinearLayoutManager(this)
            commentsRecyclerView.adapter = adapter
            //post.postComments?.let { adapter!!.setList(it) }
        }

        commentViewModel.getPost().observe(this, Observer {
            if (it != null) {
                post = it
                if (commentSwipeRefreshLayout.isRefreshing) commentSwipeRefreshLayout.isRefreshing =
                    false
                if (it.postComments != null) {
                    adapter?.setList(it.postComments!!)
                }
            }
        })

        commentSwipeRefreshLayout.setOnRefreshListener {
            if (commentSwipeRefreshLayout.isRefreshing)
                commentViewModel.loadPost()
        }

    }

    private fun setListeners() {
        commentEditText.setListenerColor(
            addCommentButton,
            R.color.colorLightBlue,
            R.color.colorBlue
        )

        val docRef = FirebaseHandler.getDatabase()
            .collection(FirebaseHandler.POSTS_REF).document(post.postId)
        listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("NotificationsListener", "Listen failed.", e)
                finish()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val postModel = snapshot.toObject(PostModel::class.java)

                if (postModel == null) {
                    Toast.makeText(this, "Post was deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else commentViewModel.setPostModel(postModel)
            }

        }
    }

    override fun onDestroy() {
        if(this::listener.isInitialized) listener.remove()
        super.onDestroy()
    }

    private fun getModel() {
        model = intent.getParcelableExtra("model")
        position = intent.getIntExtra("position", -1)



        if (model == null) finish()
        else {
            post = model as PostModel
            commentViewModel.setPostModel(model!!)
        }


        if (post.postComments == null) post.postComments = arrayListOf()
    }

    override fun onBackPressed() {
        val mIntent = Intent()
        //post.postComments = commentViewModel.getComments().value
        mIntent.putExtra("model", post)
        mIntent.putExtra("position", position)
        setResult(Activity.RESULT_OK, mIntent)
        super.onBackPressed()
    }


    @SuppressLint("SetTextI18n")
    override fun removeComment(position: Int) {
        val desc =
            "${getString(R.string.do_you_really_want_to_delete_comment)} \n${post.postComments?.get(
                position
            )?.comment}"
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

    override fun likeComment(position: Int) {
        commentViewModel.likeComment(position)
        adapter?.notifyItemChanged(position)
    }

    override fun openProfile(position: Int) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("model", post.postComments?.get(position)?.commentAuthorModel)
        startActivity(intent)
    }

    fun addComment(v: View) {
        if (commentEditText.text.isBlank()) return
        val comment = CommentModel(
            System.currentTimeMillis(),
            App.getCurrentUser().userId,
            commentEditText.text.toString(),
            arrayListOf(),
            arrayListOf()
        )
        commentViewModel.addComment(comment)
        commentEditText.text.clear()
        commentEditText.clearFocus()
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(commentEditText.windowToken, 0)
    }
}