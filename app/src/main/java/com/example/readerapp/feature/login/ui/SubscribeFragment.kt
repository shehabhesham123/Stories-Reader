package com.example.readerapp.feature.login.ui

import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.readerapp.R
import com.example.readerapp.core.interation.UseCase
import com.example.readerapp.core.navigation.Navigation
import com.example.readerapp.core.platform.BaseFragment
import com.example.readerapp.databinding.FragmentSubscribeBinding
import com.example.readerapp.feature.auth.credentials.Authenticator

class SubscribeFragment : BaseFragment() {
    private lateinit var mBinding: FragmentSubscribeBinding
    private val navigation by lazy { Navigation(Authenticator(requireContext())) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSubscribeBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        mBinding.signIn.setOnClickListener {
            navigation.showLogin(requireContext())
            finish()
        }
    }

    private fun updateUI() {
        hideAppbar()
        prepareSignUpSentence()
    }

    private fun prepareSignUpSentence() {
        val spannableString = SpannableString(resources.getString(R.string.SignUp))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                navigation.showRegisterActivity(requireContext())
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                val color = resources.getColor(R.color.link, null)
                ds.color = color
            }
        }
        spannableString.setSpan(clickableSpan, 24, 31, 0)
        mBinding.signUp.text = spannableString
        mBinding.signUp.movementMethod = LinkMovementMethod()
    }

    override fun sendAction(interaction: UseCase) {

    }

    override fun render() {
    }

    override fun idleState() {
    }

    override fun failureState(message: String) {
    }

    override fun successState(data: Any) {
    }

    override fun loadingState() {
    }
}