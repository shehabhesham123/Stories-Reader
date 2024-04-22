package com.example.readerapp.core.platform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.readerapp.R
import com.example.readerapp.databinding.BaseActivityBinding

/**
 * base class for all activities
 */
abstract class BaseActivity : AppCompatActivity() {
    private lateinit var mBinding: BaseActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = BaseActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val fragmentManager = supportFragmentManager

        needToAddFragment(fragmentManager)?.apply {
            addFragmentToContainer(this, fragmentManager)
        }
    }

    private fun needToAddFragment(fragmentManager: FragmentManager): Fragment? {
        val fragment = fragmentManager.findFragmentById(R.id.fragmentContainer)
        return if (fragment != null) null
        else fragment()
    }

    private fun addFragmentToContainer(fragment: Fragment, fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, fragment)
            .commit()
    }

    abstract fun fragment(): Fragment
}