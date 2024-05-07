package com.example.readerapp.core.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.readerapp.feature.auth.credentials.Authenticator

/**
 * launcher activity which specifies going to Login activity or main activity
 */
class RouteActivity : AppCompatActivity() {
    private lateinit var navigation: Navigation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigation = Navigation(Authenticator(baseContext))

        navigation.showMain(baseContext)
        finish()
    }
}