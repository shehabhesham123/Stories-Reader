package com.example.readerapp.feature.auth.credentials

import android.content.Context
import android.util.Log
import com.example.readerapp.core.network.firebase.Authentication
import com.example.readerapp.core.network.firebase.NormalAuth

/**
 * This class contains authentication permissions and whether the user has navigation permission
 */
class Authenticator(private val context: Context) {

    private val auth: Authentication = NormalAuth(context)

    fun isUserLogin(): Boolean {
        return auth.currentUser() != null
    }

}