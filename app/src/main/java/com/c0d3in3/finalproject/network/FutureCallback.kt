package com.c0d3in3.finalproject.network

interface FutureCallback {
    fun <T> onResponse(data: T)
    fun onFail(response: String)
}