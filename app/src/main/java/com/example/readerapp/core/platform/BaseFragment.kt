package com.example.readerapp.core.platform

import android.view.View
import androidx.fragment.app.Fragment
import com.example.readerapp.core.interation.UseCase

abstract class BaseFragment : Fragment() {

    fun showProgress() = progressStatus(View.VISIBLE)

    fun hideProgress() = progressStatus(View.GONE)

    private fun progressStatus(viewStatus: Int) = with(activity) {
        if (this is BaseActivity) this.progressBar().visibility = viewStatus
    }

    // MVI
    abstract fun sendAction(interaction: UseCase)

    abstract fun render()

    abstract fun idleState()

    abstract fun failureState(message: String)

    abstract fun successState(data: Any)

    abstract fun loadingState()
}