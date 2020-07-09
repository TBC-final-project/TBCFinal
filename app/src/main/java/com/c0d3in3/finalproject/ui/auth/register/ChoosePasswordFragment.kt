package com.c0d3in3.finalproject.ui.auth.register

import com.c0d3in3.finalproject.base.BaseFragment

import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.RegisterActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.choose_password_layout.view.*

class ChoosePasswordFragment : BaseFragment() {

    override fun init() {
        rootView!!.finishButton.setOnClickListener {
            val password = rootView!!.passwordET.text.toString()
            val repeatPassword = rootView!!.repeatPasswordET.text.toString()
            if(password.length < 6 || password.length > 32)return@setOnClickListener Utils.createDialog(requireActivity(), "Error", getString(R.string.password_length))
            if(password.isBlank() || repeatPassword.isBlank()) return@setOnClickListener Utils.createDialog(requireActivity(), "Error", getString(R.string.fields_are_empty))
            if(password != repeatPassword) return@setOnClickListener Utils.createDialog(requireActivity(), "Error", getString(R.string.passwords_not_match))
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