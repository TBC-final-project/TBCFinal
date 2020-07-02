package com.c0d3in3.finalproject.ui.auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.c0d3in3.finalproject.BaseFragment

import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.ui.auth.RegisterActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.choose_email_layout.view.*

class ChooseEmailFragment : BaseFragment() {

    override fun init() {
        rootView!!.btnNext.setOnClickListener {
            (activity as RegisterActivity).getEmail(rootView!!.emailET.text.toString())
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
