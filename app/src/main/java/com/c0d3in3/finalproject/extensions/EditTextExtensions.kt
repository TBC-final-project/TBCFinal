package com.c0d3in3.finalproject.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.tools.Utils

fun EditText.isEmailValid() {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            tag = if (Utils.isValidEmail(p0.toString())) {
                setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, App.getInstance().applicationContext.getDrawable(
                    R.drawable.valid_drawable), null)
                "1"
            }else
                "0"
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    })
}
