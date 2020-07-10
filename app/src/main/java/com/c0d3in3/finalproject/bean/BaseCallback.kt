package com.c0d3in3.finalproject.bean

interface BaseCallback<T> {
    fun onClickItem(data: T)
}