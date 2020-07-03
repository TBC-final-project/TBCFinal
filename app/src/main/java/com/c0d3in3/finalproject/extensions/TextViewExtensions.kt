package com.c0d3in3.finalproject.extensions

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.widget.TextView

fun TextView.setColor(text: String, color: Int) {
    val word: Spannable = SpannableString(text)
    word.setSpan(ForegroundColorSpan(color), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(word)
}

fun TextView.setUnderline(text: String) {
    val word: Spannable = SpannableString(text)
    word.setSpan(UnderlineSpan(), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(word)
}

fun TextView.setBold(text: String) {
    val word: Spannable = SpannableString(text)
    word.setSpan(StyleSpan(Typeface.BOLD), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(word)
}