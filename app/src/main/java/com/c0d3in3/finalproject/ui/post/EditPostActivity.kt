package com.c0d3in3.finalproject.ui.post

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.c0d3in3.finalproject.Constants.POSTS_REF
import com.c0d3in3.finalproject.Constants.POST_TYPE_IMAGE
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.databinding.ActivityEditPostBinding
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.tools.Utils
import kotlin.properties.Delegates

class EditPostActivity : BaseActivity() {

    private lateinit var model: PostModel
    private var position by Delegates.notNull<Int>()

    override fun getLayout() = R.layout.activity_edit_post

    override fun init() {
        model = intent.getParcelableExtra("model")
        position = intent.getIntExtra("position", 0)

        val binding: ActivityEditPostBinding = DataBindingUtil.setContentView(this, getLayout())
        binding.model = model

        initToolbar("Edit post")

        binding.btnUpdatePost.setOnClickListener {
            val title = binding.etAddPostTitle.text.toString()
            val description = binding.etAddPostDescription.text.toString()
            if (title.isBlank()) return@setOnClickListener Utils.createDialog(
                this,
                "Error",
                "Title can't be empty"
            )
            model.postTitle = title
            model.postDescription = description
            FirebaseHandler.getDatabase().collection(POSTS_REF).document(model.postId)
                .update("postTitle", title, "postDescription", description)
            val intent = intent
            intent.putExtra("model", model)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}
