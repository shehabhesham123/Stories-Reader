package com.example.readerapp.feature.stories.ui.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.readerapp.core.platform.BaseActivity

class StoryDetailsActivity : BaseActivity() {
    override fun fragment(): Fragment {
        return StoryDetailsFragment()
    }
    companion object {
        fun myIntent(context: Context) = Intent(context, StoryDetailsActivity::class.java)
    }
}