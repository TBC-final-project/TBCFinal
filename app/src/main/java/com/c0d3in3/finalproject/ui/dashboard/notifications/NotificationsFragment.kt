package com.c0d3in3.finalproject.ui.dashboard.notifications

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.Constants.NOTIFICATION_COMMENT
import com.c0d3in3.finalproject.Constants.NOTIFICATION_LIKE_COMMENT
import com.c0d3in3.finalproject.Constants.NOTIFICATION_LIKE_POST
import com.c0d3in3.finalproject.Constants.NOTIFICATION_START_FOLLOW
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.BaseCallback
import com.c0d3in3.finalproject.bean.NotificationModel
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.dashboard.DashboardActivity
import com.c0d3in3.finalproject.ui.post.comment.CommentsActivity
import com.c0d3in3.finalproject.ui.post.post_detailed.PostDetailedActivity
import com.c0d3in3.finalproject.ui.profile.ProfileActivity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.android.synthetic.main.fragment_notifications.view.notificationRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsFragment : BaseFragment(), BaseCallback {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var notificationsList = arrayListOf<NotificationModel>()
    private lateinit var notificationAdapter: NotificationAdapter

    override fun init() {
        //(activity as DashboardActivity).setToolbarTitle(getString(R.string.notifications))
    }

    override fun setUpFragment() {


        notificationAdapter = NotificationAdapter(this)

        rootView!!.notificationRecyclerView.adapter = notificationAdapter

        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        initializeNotificationsListener()
    }


    private fun initializeNotificationsListener() {
        val docRef = FirebaseHandler.getDatabase()
            .collection(FirebaseHandler.USERS_REF).document(App.getCurrentUser().userId)
            .collection("notifications").orderBy("notificationTimestamp", Query.Direction.DESCENDING)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("NotificationsListener", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                notificationsList = snapshot.toObjects(NotificationModel::class.java) as ArrayList
                notificationAdapter.setList(notificationsList)
            }

        }
    }

    override fun getLayout() = R.layout.fragment_notifications

    override fun onClickItem(position: Int) {
        when(notificationsList[position].notificationType.toInt()){
            NOTIFICATION_START_FOLLOW ->
                getUser(notificationsList[position].notificationSenderId, ProfileActivity())
            NOTIFICATION_LIKE_POST -> getPost(notificationsList[position].notificationPostId, PostDetailedActivity(), position)
            NOTIFICATION_COMMENT -> getPost(notificationsList[position].notificationPostId, CommentsActivity(), position)
            NOTIFICATION_LIKE_COMMENT -> getPost(notificationsList[position].notificationPostId, CommentsActivity(), position)
        }
    }

    private fun getUser(userId: String, act:Activity, model: PostModel? = null, commentId: String? = null){
        CoroutineScope(Dispatchers.IO).launch {
            UsersRepository().getUser(userId).collect { state->
                when(state){
                    is State.Success ->{
                        withContext(Dispatchers.Main){
                            if(model != null){
                                val intent = Intent(activity, act::class.java)
                                model.postAuthorModel = state.data
                                intent.putExtra("model", model)
                                intent.putExtra("commentId", commentId)
                                startActivity(intent)
                            }
                            else{
                                println("movida")
                                val intent = Intent(activity, ProfileActivity::class.java)
                                intent.putExtra("model", state.data)
                                startActivity(intent)
                            }
                        }
                    }

                    is State.Failed->
                        withContext(Dispatchers.Main) {
                            Utils.createDialog((activity as DashboardActivity), "Error", "Error while loading user profile")
                        }
                }
            }
        }
    }

    private fun getPost(postId: String, act: Activity, position: Int){
        CoroutineScope(Dispatchers.IO).launch {
            PostsRepository().getPost(postId).collect { state->
                when(state){
                    is State.Success ->{
                        state.data?.postAuthor?.let { getUser(it, act, state.data, notificationsList[position].notificationCommentId) }
                    }

                    is State.Failed->
                        withContext(Dispatchers.Main) {
                            Utils.createDialog((activity as DashboardActivity), "Error", "Error while loading post")
                        }
                }
            }
        }
    }
}