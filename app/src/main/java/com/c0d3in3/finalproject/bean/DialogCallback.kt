package com.c0d3in3.finalproject.bean

import android.app.Dialog

interface DialogCallback {
    fun onResponse(dialog: Dialog){}
    fun onCancel(){}
    fun onDeletePost(){}
    fun onEditPost(){}
}