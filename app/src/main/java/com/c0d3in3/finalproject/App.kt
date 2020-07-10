package com.c0d3in3.finalproject

import android.app.Application
import android.content.Context
import android.content.Intent
import com.c0d3in3.finalproject.ui.splash.SplashActivity


class App : Application() {

    companion object{
        private lateinit var instance : App
        private lateinit var context: Context

        fun getInstance() = instance
        fun getContext() = context

    }

    override fun onCreate() {
        super.onCreate()

//        Thread.setDefaultUncaughtExceptionHandler { _, _ ->
//            val intent = Intent(applicationContext, SplashActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent)
//        }
        instance = this
        context = applicationContext

    }

}