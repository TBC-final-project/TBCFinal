package com.c0d3in3.finalproject.ui.auth

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.ui.auth.viewpager.ChooseEmailFragment
import com.c0d3in3.finalproject.ui.auth.viewpager.ChooseNameFragment
import com.c0d3in3.finalproject.ui.auth.viewpager.ChoosePasswordFragment
import com.c0d3in3.finalproject.ui.auth.viewpager.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : FragmentActivity() {

    lateinit var adapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        adapter = ViewPagerAdapter(this)
        adapter.addFragment(ChooseNameFragment())
        adapter.addFragment(ChooseEmailFragment())
        adapter.addFragment(ChoosePasswordFragment())
        registerViewPager.adapter = adapter


    }


}
