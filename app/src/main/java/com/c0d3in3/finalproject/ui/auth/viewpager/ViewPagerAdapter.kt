package com.c0d3in3.finalproject.ui.auth.viewpager

import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentsList = arrayListOf<Fragment>()

    fun addFragment(fragment: Fragment) {
        fragmentsList.add(fragment)
    }

    override fun getItemCount() = fragmentsList.size

    override fun createFragment(position: Int) = fragmentsList[position]

}