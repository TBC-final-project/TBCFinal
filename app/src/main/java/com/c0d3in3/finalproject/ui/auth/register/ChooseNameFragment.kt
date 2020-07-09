package com.c0d3in3.finalproject.ui.auth.register

import android.content.Intent
import com.c0d3in3.finalproject.base.BaseFragment

import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.tools.Utils
import com.c0d3in3.finalproject.ui.auth.PreAuthActivity
import com.c0d3in3.finalproject.ui.auth.RegisterActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.choose_name_layout.view.*

class ChooseNameFragment : BaseFragment() {

    override fun init() {
        rootView!!.btnNext.setOnClickListener {
            val firstName = rootView!!.firstNameET.text.toString()
            val lastName = rootView!!.lastNameET.text.toString()
            if(firstName.isBlank() || lastName.isBlank()) return@setOnClickListener Utils.createDialog(requireActivity(), "Error", getString(R.string.fields_are_empty))
            (activity as RegisterActivity).getName(firstName, lastName)
        }

        rootView!!.btnBack.setOnClickListener {
            val intent = Intent(activity, PreAuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun setUpFragment() {

    }

    override fun getLayout() = R.layout.choose_name_layout


}