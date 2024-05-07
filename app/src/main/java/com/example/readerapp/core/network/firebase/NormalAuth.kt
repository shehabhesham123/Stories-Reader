package com.example.readerapp.core.network.firebase

import android.content.Context
import com.example.readerapp.core.network.NetworkHandler
import com.google.firebase.auth.FirebaseUser

class NormalAuth (private val context: Context) : Authentication() {

    fun register(
        email: String,
        password: String,
        onSuccess: (currentUser: FirebaseUser?) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (NetworkHandler.isNetworkAvailable(context)) {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                onSuccess(it.user)
            }.addOnFailureListener {
                onFailure("${it.message}")
            }
        } else onFailure("you are not connected to the Internet")
    }

    fun login(
        email: String,
        password: String,
        onSuccess: (currentUser: FirebaseUser?) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (NetworkHandler.isNetworkAvailable(context)) {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                onSuccess(it.user)
            }.addOnFailureListener {
                onFailure("${it.message}")
            }
        } else onFailure("you are not connected to the Internet")
    }

    override fun signOut() {
        if (NetworkHandler.isNetworkAvailable(context)) auth.signOut()
    }

    override fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun updatePassword(
        currentUser: FirebaseUser,
        newPassword: String,
        onSuccess: (msg: String) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (NetworkHandler.isNetworkAvailable(context)) {
            currentUser.updatePassword(newPassword).addOnSuccessListener {
                onSuccess("Update successfully")
            }.addOnFailureListener {
                onFailure("${it.message}")
            }
        } else onFailure("you are not connected to the Internet")
    }

    /*
    can't create singleton in this class because it require context
         and in static variable ---> the value of it store in memory one time and don't change again
         and the context is always change

        companion object {

            private var auth: NormalAuth? = null
            fun getInstance(context: Context): NormalAuth {
                if (auth == null) auth = NormalAuth(context)
                return auth
            }
        }
     */
}