package com.c0d3in3.finalproject.ui.dashboard.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.databinding.SearchItemLayoutBinding
import com.c0d3in3.finalproject.network.model.UserModel

class SearchAdapter(private val callback: SearchAdapterCallback) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    interface SearchAdapterCallback{
        fun onSearchItemClick(position: Int)
    }

    private var searchList = arrayListOf<UserModel>()


    fun setList(list: ArrayList<UserModel>){
        searchList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : SearchItemLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.search_item_layout, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = searchList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(private val binding: SearchItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        private lateinit var model: UserModel

        fun onBind(){
            model = searchList[adapterPosition]
            binding.userModel = model
        }
    }
}