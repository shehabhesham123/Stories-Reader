package com.example.readerapp.feature.login.ui

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.readerapp.core.platform.BaseActivity

class SubscribeActivity :BaseActivity(){
    override fun fragment(): Fragment {
        return SubscribeFragment()
    }

    companion object {
        fun myIntent(context: Context) = Intent(context,SubscribeActivity::class.java)
    }
}