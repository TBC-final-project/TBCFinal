package com.c0d3in3.finalproject

import android.app.Application
import android.content.Context

class App : Application() {

    companion object{
        private lateinit var instance : App
        private lateinit var context: Context

        fun getInstance() = instance
        fun getContext() = context

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
    }

}