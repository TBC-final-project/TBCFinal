package com.c0d3in3.finalproject.ui.dashboard.notifications

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
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.ui.profile.ProfileActivity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.android.synthetic.main.fragment_notifications.view.notificationRecyclerView

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
            .collection("notifications")
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
            NOTIFICATION_START_FOLLOW ->{
                val intent = Intent(activity, ProfileActivity::class.java)
                intent
            }
            NOTIFICATION_LIKE_POST ->{
                val intent = Intent(activity, ProfileActivity::class.java)
                intent
            }
            NOTIFICATION_COMMENT ->{
                val intent = Intent(activity, ProfileActivity::class.java)
                intent
            }
            NOTIFICATION_LIKE_COMMENT ->{
                val intent = Intent(activity, ProfileActivity::class.java)
                intent
            }
        }
    }
}