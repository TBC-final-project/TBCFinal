package com.c0d3in3.finalproject.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.tools.Utils
import kotlinx.android.synthetic.main.activity_comments.*

fun EditText.isEmailValid() {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            tag = if (Utils.isValidEmail(p0.toString())) {
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null, null, App.getInstance().applicationContext.getDrawable(
                        R.drawable.valid_drawable
                    ), null
                )
                "1"
            } else
                "0"
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    })
}

fun EditText.setListenerColor(textView: TextView, defaultColor: Int, fillColor: Int) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.toString().isNotEmpty()) textView.setTextColor(ContextCompat.getColor(App.getContext(), fillColor))
            else textView.setTextColor(ContextCompat.getColor(App.getContext(), defaultColor))
        }
    })
}
