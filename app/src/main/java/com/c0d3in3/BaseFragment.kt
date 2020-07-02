package com.c0d3in3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(rootView == null){
            rootView = inflater.inflate(getLayout(), container, false)
            setUpFragment()
        }
        init()
        return rootView
    }

    abstract fun init()

    abstract fun setUpFragment()

    abstract fun getLayout() : Int
}