//package com.c0d3in3.finalproject
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.c0d3in3.finalproject.bean.BaseCallback
//import com.c0d3in3.finalproject.bean.CustomInterface
//import com.c0d3in3.finalproject.bean.PostModel
//import com.c0d3in3.finalproject.databinding.CommentItemLayoutBinding
//import com.c0d3in3.finalproject.network.State
//import com.c0d3in3.finalproject.network.UsersRepository
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//abstract class BaseAdapter(private val callback: BaseCallback) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    private var adapterList = mutableListOf<CustomInterface>()
//    private val usersRepository = UsersRepository()
//
//    abstract fun getLayout() : Int
//
//    interface CommentAdapterCallback {
//        fun removeComment(position: Int)
//        fun likeComment(position: Int)
//    }
//
//    fun setList(list: ArrayList<CustomInterface>) {
//        if (list.isNotEmpty()) {
//            val mutableList = list.toMutableList()
//            val diffResult = DiffUtil.calculateDiff(
//                MyDiffCallback(mutableList, adapterList)
//            )
//            adapterList = mutableList
//            if(adapterList[0] is PostModel) {
//                for (idx in 0 until adapterList.size) {
//                    if ((adapterList[idx] as PostModel).postAuthorModel != null && idx != adapterList.size - 1) continue
//                    if (idx == adapterList.size - 1) getUser(adapterList[idx], true, diffResult)
//                    else getUser(adapterList[idx], false, null)
//                }
//            }
//        }
//        else{
//            comments.clear()
//            notifyDataSetChanged()
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding: CommentItemLayoutBinding = DataBindingUtil.inflate(
//            LayoutInflater.from(parent.context),
//            getLayout(),
//            parent,
//            false
//        )
//        return ViewHolder(binding)
//    }
//
//    override fun getItemCount() = adapterList.size
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.onBind()
//    }
//
//    abstract inner class ViewHolder(private val binding: CommentItemLayoutBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        abstract fun onBind()
//    }
//
//    private fun getUser(
//        model: CustomInterface,
//        isLast: Boolean,
//        diffResult: DiffUtil.DiffResult?
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            usersRepository.getUser(model.commentAuthor!!).collect { state ->
//                when (state) {
//                    is State.Success -> {
//                        model.commentAuthorModel = state.data!!
//                        if (isLast) withContext(Dispatchers.Main) {
//                            diffResult?.dispatchUpdatesTo(this@BaseAdapter
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}