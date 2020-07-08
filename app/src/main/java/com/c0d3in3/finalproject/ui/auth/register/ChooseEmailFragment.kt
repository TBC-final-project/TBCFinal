package com.c0d3in3.finalproject.ui.auth.register

import com.c0d3in3.finalproject.base.BaseFragment
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.RegisterActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.choose_email_layout.view.*

class ChooseEmailFragment : BaseFragment() {

    override fun init() {
        rootView!!.btnNext.setOnClickListener {
            val email = rootView!!.emailET.text.toString()
            if(email.isEmpty()) return@setOnClickListener Utils.createDialog(requireActivity(), "Error", getString(R.string.email_is_empty))
            if(!Utils.isValidEmail(email)) return@setOnClickListener Utils.createDialog(requireActivity(), "Error", getString(R.string.email_is_not_valid))
            (activity as RegisterActivity).getEmail(email)
            (activity as RegisterActivity).registerViewPager.currentItem = (activity as RegisterActivity).registerViewPager.currentItem + 1
        }

        rootView!!.btnBack.setOnClickListener {
            (activity as RegisterActivity).registerViewPager.currentItem = (activity as RegisterActivity).registerViewPager.currentItem - 1
        }
    }

    override fun setUpFragment() {

    }

    override fun getLayout() = R.layout.choose_email_layout


}