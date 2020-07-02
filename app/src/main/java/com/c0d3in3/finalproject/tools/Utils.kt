package com.c0d3in3.finalproject.tools

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.Window
import android.view.WindowManager
import com.c0d3in3.finalproject.R
import kotlinx.android.synthetic.main.dialog_error_layout.*

object Utils {
    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!)
            .matches()
    }

    fun createDialog(ctx: Context, title: String, description : String){
        val dialog = Dialog(ctx)

        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(R.layout.dialog_error_layout)

        val params = dialog.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params

        dialog.dialogDescriptionTV.text = description
        dialog.dialogTitleTV.text = title

        dialog.dialogOkButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }
}