package com.c0d3in3.finalproject.ui.auth.register

import com.c0d3in3.finalproject.base.BaseFragment

import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.RegisterActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.choose_email_layout.view.btnBack
import kotlinx.android.synthetic.main.choose_email_layout.view.btnNext
import kotlinx.android.synthetic.main.fragment_choose_username.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseUsernameFragment : BaseFragment() {

    override fun init() {
        rootView!!.btnNext.setOnClickListener {
            val username = rootView!!.usernameET.text.toString()
            if (username.isEmpty()) return@setOnClickListener Utils.createDialog(
                requireActivity(), "Error", getString(
                    R.string.username_is_empty
                )
            )
            checkUsername(username)

        }

        rootView!!.btnBack.setOnClickListener {
            (activity as RegisterActivity).registerViewPager.currentItem =
                (activity as RegisterActivity).registerViewPager.currentItem - 1
        }
    }

    override fun setUpFragment() {

    }

    private fun checkUsername(username: String) {
        CoroutineScope(Dispatchers.IO).launch {

            PostsRepository().checkUser(username).collect { state ->
                when (state) {
                    is State.Success -> {
                        val model = state.data
                        if (model != null) Utils.createDialog(
                            activity as RegisterActivity,
                            "Error",
                            getString(R.string.username_is_taken)
                        )
                        else {
                            (activity as RegisterActivity).getUsername(username)
                            (activity as RegisterActivity).registerViewPager.currentItem =
                                (activity as RegisterActivity).registerViewPager.currentItem + 1

                        }
                    }

                    is State.Failed -> withContext(Dispatchers.Main) {
                        Utils.createDialog(
                            activity as RegisterActivity,
                            "Error",
                            state.message
                        )
                    }
                } // END when
            } // END collect

        }
    }

    override fun getLayout() = R.layout.fragment_choose_username

}
