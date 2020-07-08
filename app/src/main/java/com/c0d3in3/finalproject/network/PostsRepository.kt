package com.c0d3in3.finalproject.network

import com.c0d3in3.finalproject.network.FirebaseHandler.POSTS_REF
import com.c0d3in3.finalproject.network.FirebaseHandler.USERS_REF
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.network.model.UserModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await


class PostsRepository {
    private val mPostsCollection = FirebaseHandler.getDatabase().collection(POSTS_REF)


    fun getAllPosts(limit:Long = 10, lastPostId: String? = null) = flow<State<ArrayList<PostModel>>> {

        emit(State.loading())

        val snapshot : QuerySnapshot = if(lastPostId != null){
            println(lastPostId)
            mPostsCollection.orderBy("postId").startAfter(lastPostId).limit(limit).orderBy("postTimestamp", Query.Direction.DESCENDING).get().await()
        } else{
            mPostsCollection.limit(limit).orderBy("postTimestamp", Query.Direction.DESCENDING).get().await()
        }
        val posts = snapshot.toObjects(PostModel::class.java) as ArrayList

        emit(State.success(posts))

    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun addPost(post: PostModel) = flow<State<DocumentReference>> {

        // Emit loading state
        emit(State.loading())

        val snapshot = mPostsCollection.add(post).await()

        snapshot.update("postId", snapshot.id)

        // Emit success state with data
        emit(State.success(snapshot))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun updatePost(){

    }
}