package com.example.readerapp.feature.login.ui

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.readerapp.core.platform.BaseActivity

class RegisterActivity : BaseActivity() {
    override fun fragment(): Fragment {
        return RegisterFragment()
    }

    companion object {
        fun myIntent(context: Context) = Intent(context, RegisterActivity::class.java)
    }
}