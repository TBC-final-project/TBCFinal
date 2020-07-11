package com.c0d3in3.finalproject.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class BasePagerAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm) {

    private var isFirst = false

    private val fragmentsList = arrayListOf<Fragment>()

    fun addFragment(fragment: Fragment) {
        fragmentsList.add(fragment)
    }


    override fun getItem(position: Int) = fragmentsList[position]

    override fun getCount() = fragmentsList.size


}