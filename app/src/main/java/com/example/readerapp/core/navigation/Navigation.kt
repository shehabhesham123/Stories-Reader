package com.example.readerapp.core.navigation

import android.content.Context
import com.example.readerapp.feature.auth.credentials.Authenticator

/**
 * this class will contain of all intent (explicit, implicit)
 */
class Navigation(private val auth: Authenticator) {

    fun showMain(context: Context) {
        if (auth.isUserLogin()) {

        } else {

        }
    }

}