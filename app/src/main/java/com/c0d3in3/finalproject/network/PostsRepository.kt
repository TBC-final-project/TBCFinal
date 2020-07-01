package com.c0d3in3.finalproject.network

import com.c0d3in3.finalproject.network.FirebaseHandler.POSTS_REF
import com.c0d3in3.finalproject.network.model.PostModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await


class PostsRepository {
    private val mPostsCollection = FirebaseHandler.getDatabase().collection(POSTS_REF)

    fun getAllPosts(limit:Long = 10, lastPostId: String? = null) = flow<State<ArrayList<PostModel>>> {

        // Emit loading state
        emit(State.loading())

        val snapshot : QuerySnapshot = if(lastPostId != null){
            println(lastPostId)
            mPostsCollection.orderBy("postId").startAfter(lastPostId).limit(limit).get().await()
        } else{
            mPostsCollection.limit(limit).orderBy("postId").get().await()
        }
        val posts = snapshot.toObjects(PostModel::class.java) as ArrayList

        // Emit success state with data
        emit(State.success(posts))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun addPost(post: PostModel) = flow<State<DocumentReference>> {

        // Emit loading state
        emit(State.loading())

        val snapshot = mPostsCollection.add(post).await()

        // Emit success state with data
        emit(State.success(snapshot))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

//    suspend fun getAllPosts() : ArrayList<PostModel>{
//        val arrayList = arrayListOf<PostModel>()
//        return {CoroutineScope(Dispatchers.IO).launch {
//            FirebaseHandler.getDatabase().collection(POSTS_REF).orderBy("postTimestamp").limit(10).get().addOnSuccessListener {
//                it.forEach {
//                    val post = it.toObject<PostModel>()
//                    arrayList.add(post)
//                }
//            }
//        }
//    }
//
//    fun getUserByID(userId: String, callback: FutureCallback) {
//        FirebaseHandler.getDatabase().collection(FirebaseHandler.USERS_REF).document(userId).get()
//            .addOnSuccessListener {
//                val user = it.toObject(UserModel::class.java)
//                callback.onResponse(user)
//            }.addOnFailureListener {
//            callback.onFail(it.toString())
//        }
//    }
//
//    fun getPostByID(postId: String, callback: FutureCallback) {
//        FirebaseHandler.getDatabase().collection(POSTS_REF).document(postId).get()
//            .addOnSuccessListener {
//                val post = it.toObject(PostModel::class.java)
//                callback.onResponse(post)
//            }.addOnFailureListener {
//            callback.onFail(it.toString())
//        }
//    }
    }