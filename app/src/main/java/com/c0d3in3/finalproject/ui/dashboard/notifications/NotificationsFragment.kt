
package com.c0d3in3.finalproject.ui.dashboard.notifications

import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.R
import kotlinx.android.synthetic.main.fragment_notifications.view.*

class NotificationsFragment : BaseFragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun init() {
        //(activity as DashboardActivity).setToolbarTitle(getString(R.string.notifications))
    }

    override fun setUpFragment() {


        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val textView: TextView = rootView!!.text_notifications
        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
    }

    override fun getLayout() = R.layout.fragment_notifications
}