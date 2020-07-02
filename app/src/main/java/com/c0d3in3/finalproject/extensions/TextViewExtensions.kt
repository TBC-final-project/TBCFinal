package com.c0d3in3.finalproject.extensions

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView

fun TextView.setColor(text: String, color: Int) {
    val word: Spannable = SpannableString(text)
    word.setSpan(ForegroundColorSpan(color), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(word)
}