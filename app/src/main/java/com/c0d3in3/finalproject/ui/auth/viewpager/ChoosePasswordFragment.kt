package com.c0d3in3.finalproject.ui.auth.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.ui.auth.RegisterActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.choose_password_layout.view.*

/**
 * A simple [Fragment] subclass.
 */
class ChoosePasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val itemView =  inflater.inflate(R.layout.choose_password_layout, container, false)
        itemView.btnBack.setOnClickListener {
            (activity as RegisterActivity).registerViewPager.currentItem = (activity as RegisterActivity).registerViewPager.currentItem - 1
        }
        return itemView
    }

}
