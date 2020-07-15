package com.c0d3in3.finalproject.ui.dashboard.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.c0d3in3.finalproject.MyDiffCallback
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.BaseCallback
import com.c0d3in3.finalproject.bean.NotificationModel
import com.c0d3in3.finalproject.databinding.NotificationItemLayoutBinding
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.UsersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationAdapter(private val callback: BaseCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var notificationList = mutableListOf<NotificationModel>()
    private var usersRepository = UsersRepository()

    fun setList(list: ArrayList<NotificationModel>){
        if(list.isNotEmpty()){
            val mutableList = list.toMutableList()
            val diffResult = DiffUtil.calculateDiff(MyDiffCallback(mutableList,notificationList))
            notificationList = mutableList
            for (idx in 0 until notificationList.size) {
                if (notificationList[idx].notificationSenderModel != null) continue
                if (idx == notificationList.size - 1) getUser(notificationList[idx], true, diffResult)
                else getUser(notificationList[idx], false, null)
            }
        }
        else{
            notificationList.clear()
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(private val binding: NotificationItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){

        fun onBind(){
            binding.notificationModel = notificationList[adapterPosition]

            binding.notificationItemLayout.setOnClickListener {
                callback.onClickItem(adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.notification_item_layout, parent, false))
    }

    override fun getItemCount() = notificationList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolder -> holder.onBind()
        }
    }


    private fun getUser(
        model: NotificationModel,
        isLast: Boolean,
        diffResult: DiffUtil.DiffResult?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            usersRepository.getUser(model.notificationSenderId).collect { state ->
                when (state) {
                    is State.Success -> {
                        model.notificationSenderModel = state.data!!
                        if(isLast) withContext(Dispatchers.Main){
                            diffResult?.dispatchUpdatesTo(this@NotificationAdapter)
                            //callback.scrollToPosition(storyList.size-1)
                        }
                    }
                }
            }
        }
    }
}