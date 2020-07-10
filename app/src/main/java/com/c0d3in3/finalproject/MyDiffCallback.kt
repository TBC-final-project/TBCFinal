package com.c0d3in3.finalproject

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.c0d3in3.finalproject.bean.PostModel
import com.c0d3in3.finalproject.bean.StoryModel


class MyDiffCallback<T>(private val newList: MutableList<T>, private val oldList: MutableList<T>) : DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return when {
            oldList[oldItemPosition] is PostModel -> (oldList[oldItemPosition] as PostModel).postId === (newList[newItemPosition] as PostModel).postId
            oldList[oldItemPosition] is StoryModel -> (oldList[oldItemPosition] as StoryModel).storyId === (newList[newItemPosition] as StoryModel).storyId
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

}