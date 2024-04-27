package com.example.readerapp.feature.stories.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.readerapp.core.platform.BaseActivity

class StoriesActivity : BaseActivity() {
    override fun fragment(): Fragment {
        return StoriesFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    companion object {
        fun myIntent(context: Context) = Intent(context, StoriesActivity::class.java)
    }

}