package com.c0d3in3.finalproject.tools

import android.app.Dialog

interface DialogCallback {
    fun onResponse(dialog: Dialog)
    fun onCancel()
}