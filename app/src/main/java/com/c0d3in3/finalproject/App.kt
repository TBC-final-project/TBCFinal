package com.c0d3in3.finalproject

import android.app.Application

class App : Application() {

    companion object{
        private lateinit var instance : App

        fun getInstance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}