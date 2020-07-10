package com.c0d3in3.finalproject.network

import com.c0d3in3.finalproject.bean.UserModel
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlin.collections.ArrayList

class UsersRepository {
    private val mUsersCollection =
        FirebaseHandler.getDatabase().collection(FirebaseHandler.USERS_REF)

    fun getUser(userId: String) = flow<State<UserModel?>> {
        emit(State.loading())

        val snapshot = mUsersCollection.document(userId).get().await()

        val userModel = snapshot.toObject(UserModel::class.java)
        emit(State.success(userModel))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)


    fun getUsers(usersList: ArrayList<String>) = flow<State<ArrayList<UserModel>>> {
        emit(State.loading())

        val usersModelList = arrayListOf<UserModel>()
        usersList.forEach {
            val snapshot = mUsersCollection.document(it).get().await()

            snapshot.toObject(UserModel::class.java)?.let { it1 -> usersModelList.add(it1) }
        }

        emit(State.success(usersModelList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun checkUser(username: String) = flow<State<UserModel?>> {
        emit(State.loading())

        var userModel: UserModel? = null
        mUsersCollection.get().addOnSuccessListener {
            for (doc in it) {
                if (doc.get("username") == username) userModel = doc.toObject()
            }
        }.await()

        emit(State.success(userModel))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun checkEmail(email: String) = flow<State<Boolean>> {
        emit(State.loading())
        val task =
            FirebaseHandler.getAuth().fetchSignInMethodsForEmail(email).addOnCompleteListener {}
                .await()


        val isNewUser = task.signInMethods?.isEmpty()

        if (isNewUser != null && isNewUser) emit(State.success(true))
        else emit(State.success(false))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun searchUser(name: String) = flow<State<ArrayList<UserModel>>> {
        emit(State.loading())

        val querySnapshot =
            mUsersCollection.orderBy("userFullNameToLowerCase").startAt(name).endAt(name + "\uf8ff").limit(10).get().addOnCompleteListener { }.await()
        val list = querySnapshot.toObjects(UserModel::class.java) as ArrayList
        emit(State.success(list))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}