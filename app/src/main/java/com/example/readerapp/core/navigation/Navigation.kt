package com.example.readerapp.core.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.readerapp.feature.auth.credentials.Authenticator
import com.example.readerapp.feature.stories.ui.activity.StoriesActivity
import com.example.readerapp.feature.stories.ui.activity.StoryDetailsActivity


/**
 * this class contains of navigation operations ( intent (explicit, implicit) )
 */
class Navigation(private val auth: Authenticator) {

    fun showMain(context: Context) {
        if (auth.isUserLogin()) {
            showStories(context)
        } else {
            showLogin()
        }
    }

    private fun showLogin() {}

    private fun showStories(context: Context) {
        val intent = StoriesActivity.myIntent(context)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun showStoryDetails(context: Context) {
        val intent = StoryDetailsActivity.myIntent(context)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun getContentIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "*/*"
        return intent
    }

    fun showExternalUrl(url: String, context: Context) {
        val i = Intent(Intent.ACTION_VIEW)
        i.setData(Uri.parse(url))
        context.startActivity(i)
    }

}