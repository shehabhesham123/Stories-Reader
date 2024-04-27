package com.example.readerapp.feature.auth.credentials

/**
 * This class contains authentication permissions and whether the user has navigation permission
 */
class Authenticator private constructor() {
    fun isUserLogin(): Boolean {
        return true
    }

    companion object {
        private val auth = Authenticator()
        fun instance() = auth
    }
}