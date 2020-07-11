package com.c0d3in3.finalproject.ui.auth.register

import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.RegisterActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.choose_email_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseEmailFragment : BaseFragment() {

    private val usersRepository = UsersRepository()

    override fun init() {
        rootView!!.btnNext.setOnClickListener {
            val email = rootView!!.emailET.text.toString()
            if (email.isBlank()) return@setOnClickListener Utils.createDialog(
                requireActivity(),
                "Error",
                getString(R.string.email_is_empty)
            )
            if (!Utils.isValidEmail(email)) return@setOnClickListener Utils.createDialog(
                requireActivity(),
                "Error",
                getString(R.string.email_is_not_valid)
            )
            checkEmail(email)
        }

        rootView!!.btnBack.setOnClickListener {
            (activity as RegisterActivity).registerViewPager.currentItem =
                (activity as RegisterActivity).registerViewPager.currentItem - 1
        }
    }

    override fun setUpFragment() {

    }

    override fun getLayout() = R.layout.choose_email_layout

    private fun checkEmail(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            usersRepository.checkEmail(email).collect { state ->
                when (state) {
                    is State.Success -> {
                        val newUser = state.data
                        if (!newUser)
                            withContext(Dispatchers.Main) {
                                Utils.createDialog(
                                    activity as RegisterActivity,
                                    "Error",
                                    getString(R.string.email_is_taken)
                                )
                            }
                        else withContext(Dispatchers.Main) {(activity as RegisterActivity).getEmail(email)}
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


}