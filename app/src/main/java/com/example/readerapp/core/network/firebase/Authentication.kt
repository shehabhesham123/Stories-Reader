package com.example.readerapp.core.network.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

abstract class Authentication {
    val auth = FirebaseAuth.getInstance()
    abstract fun signOut()

    abstract fun currentUser(): FirebaseUser?
}

