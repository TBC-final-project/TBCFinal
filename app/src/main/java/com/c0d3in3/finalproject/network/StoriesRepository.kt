package com.c0d3in3.finalproject.network

import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.network.FirebaseHandler.STORIES_REF
import com.c0d3in3.finalproject.network.FirebaseHandler.USERS_REF
import com.c0d3in3.finalproject.ui.auth.UserInfo
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class StoriesRepository {

    fun getAllStories(limit:Long = 10, lastStory: Int? = null) = flow<State<ArrayList<ArrayList<StoryModel>>>> {

        emit(State.loading())

        val searchList = UserInfo.userInfo.userFollowing

        val resultList = arrayListOf<ArrayList<StoryModel>>()
        var counter = 0
        if(lastStory != null) counter  = lastStory+1
        else{
            val mStoriesCollection = FirebaseHandler.getDatabase().collection("$USERS_REF/${UserInfo.userInfo.userId}/$STORIES_REF")
            val snapshot : QuerySnapshot = mStoriesCollection.orderBy("storyValidUntil").orderBy("storyCreatedAt", Query.Direction.DESCENDING).whereGreaterThan("storyValidUntil", System.currentTimeMillis()).get().await()
            val result = snapshot.toObjects(StoryModel::class.java) as ArrayList<StoryModel>
            if(result.isNotEmpty()) resultList.add(result)
        }

        if (searchList != null) {
            while(resultList.size <= 10 && counter < searchList.size){
                val mStoriesCollection = FirebaseHandler.getDatabase().collection("$USERS_REF/${UserInfo.userInfo.userFollowing?.get(counter)}/$STORIES_REF")
                val snapshot : QuerySnapshot = mStoriesCollection.orderBy("storyValidUntil").orderBy("storyCreatedAt", Query.Direction.DESCENDING).whereGreaterThan("storyValidUntil", System.currentTimeMillis()).get().await()
                val result = snapshot.toObjects(StoryModel::class.java) as ArrayList<StoryModel>
                if(result.isNotEmpty()) resultList.add(result)
                counter++
            }
        }

        emit(State.success(resultList))

    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

//    fun addStory(story: StoryModel) = flow<State<DocumentReference>> {
//        emit(State.loading())
//
//        val snapshot = mStoriesCollection.add(story).await()
//
//        snapshot.update("storyId", snapshot.id)
//        emit(State.success(snapshot))
//
//    }.catch {
//        emit(State.failed(it.message.toString()))
//    }.flowOn(Dispatchers.IO)

}