package com.c0d3in3.finalproject.network

import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.network.FirebaseHandler.POSTS_REF
import com.c0d3in3.finalproject.bean.PostModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await


class PostsRepository {
    private val mPostsCollection = FirebaseHandler.getDatabase().collection(POSTS_REF)


    fun getAllPosts(limit: Long = 10, lastPost: PostModel? = null) =
        flow<State<ArrayList<PostModel>>> {

            emit(State.loading())

            val collectionSize = mPostsCollection.get().await().documents.size
            var counter = 0
            var lastPostModel: PostModel? = lastPost

            val searchList = App.getCurrentUser().userFollowing

            val resultList = arrayListOf<PostModel>()

            while (counter < collectionSize) {
                if (resultList.size >= limit) {
                    emit(State.success(resultList))
                    break
                }
                val snapshot: QuerySnapshot = if (lastPostModel != null)
                    mPostsCollection.orderBy("postTimestamp", Query.Direction.DESCENDING)
                        .startAfter(lastPostModel.postTimestamp)
                        .limit(limit).get()
                        .await()
                else
                    mPostsCollection.orderBy("postTimestamp", Query.Direction.DESCENDING)
                        .limit(limit).get()
                        .await()
                val result =
                    snapshot.toObjects(PostModel::class.java) as ArrayList<PostModel>
                result.forEach {
                    if (it.postAuthor == App.getCurrentUser().userId || searchList?.contains(
                            it.postAuthor.toString()
                        )!!
                    )
                        resultList.add(it)
                }
                if (result.isNotEmpty()) lastPostModel = result[result.size - 1]
                counter += limit.toInt()
                if (counter >= collectionSize) {
                    emit(State.Success(resultList))
                    break
                }
            }

        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    fun getUserPosts(limit: Long = 10, lastPost: PostModel? = null, userId: String) =
        flow<State<ArrayList<PostModel>>> {

            emit(State.loading())

            val collectionSize = mPostsCollection.get().await().documents.size
            var counter = 0
            var lastPostModel: PostModel? = lastPost

            val resultList = arrayListOf<PostModel>()

            while (counter < collectionSize) {
                if (resultList.size >= 10) {
                    emit(State.success(resultList))
                    break
                }
                val snapshot: QuerySnapshot = if (lastPostModel != null)
                    mPostsCollection.orderBy("postTimestamp", Query.Direction.DESCENDING)
                        .startAfter(lastPostModel.postTimestamp)
                        .limit(limit).get()
                        .await()
                else
                    mPostsCollection.orderBy("postTimestamp", Query.Direction.DESCENDING)
                        .limit(limit).get()
                        .await()
                val result =
                    snapshot.toObjects(PostModel::class.java) as ArrayList<PostModel>
                result.forEach {
                    if (it.postAuthor == userId)
                        resultList.add(it)
                }
                if (result.isNotEmpty()) lastPostModel = result[result.size - 1]
                counter += limit.toInt()
                if (counter >= collectionSize) {
                    emit(State.Success(resultList))
                    break
                }
            }

        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    fun getPost(postId: String) = flow<State<PostModel?>> {

        emit(State.loading())

        val snapshot = mPostsCollection.document(postId).get().await()
        val post = snapshot.toObject(PostModel::class.java)

        emit(State.success(post))

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

}