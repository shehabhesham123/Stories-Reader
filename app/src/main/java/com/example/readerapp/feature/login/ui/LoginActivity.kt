package com.example.readerapp.feature.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.readerapp.core.platform.BaseActivity

class LoginActivity:BaseActivity() {

    override fun fragment(): Fragment {
        return LoginFragment()
    }

    companion object{
        fun myIntent(context: Context) = Intent(context,LoginActivity::class.java)
    }
}