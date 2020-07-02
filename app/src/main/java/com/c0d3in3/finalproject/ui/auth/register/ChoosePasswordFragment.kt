package com.c0d3in3.finalproject.ui.auth.register

import com.c0d3in3.finalproject.BaseFragment

import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.ui.auth.RegisterActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.choose_password_layout.view.*

class ChoosePasswordFragment : BaseFragment() {

    override fun init() {
        rootView!!.finishButton.setOnClickListener {
            (activity as RegisterActivity).getPassword(rootView!!.passwordET.text.toString())
        }

        rootView!!.btnBack.setOnClickListener {
            (activity as RegisterActivity).registerViewPager.currentItem = (activity as RegisterActivity).registerViewPager.currentItem - 1
        }
    }

    override fun setUpFragment() {

    }

    override fun getLayout() = R.layout.choose_password_layout


}