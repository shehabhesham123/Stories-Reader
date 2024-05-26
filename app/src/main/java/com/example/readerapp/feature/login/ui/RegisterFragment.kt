package com.example.readerapp.feature.login.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.readerapp.R
import com.example.readerapp.core.interation.UseCase
import com.example.readerapp.core.navigation.Navigation
import com.example.readerapp.core.network.firebase.NormalAuth
import com.example.readerapp.core.platform.BaseFragment
import com.example.readerapp.core.validation.Validation
import com.example.readerapp.databinding.FragmentRegisterBinding
import com.example.readerapp.feature.auth.credentials.Authenticator
import com.example.readerapp.feature.login.data.model.User
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : BaseFragment() {

    private lateinit var mBinding: FragmentRegisterBinding
    private lateinit var navigation: Navigation
    private lateinit var normalAuth: NormalAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        normalAuth = NormalAuth(requireContext())
        navigation = Navigation(Authenticator(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRegisterBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        mBinding.register.setOnClickListener {
            val user = getUserData()
            user?.apply {
                loadingState()
                normalAuth.register(email!!, password!!, {
                    successState("Success")
                }, {
                    failureState(it)
                })
            }
        }
    }

    private fun getUserData(): User? {
        val email = mBinding.email.text.toString()
        val password = mBinding.password.text.toString()
        return if (isValid(email, password)) {
            User(email, password)
        } else {
            null
        }
    }

    private fun isValid(email: String, password: String): Boolean {
        val isEmailValid = Validation.isEmailValid(email)
        val isPasswordValid = Validation.isPasswordValid(password)

        if (!isEmailValid) mBinding.email.error = "your email is not valid"
        if (!isPasswordValid) mBinding.password.error =
            "your password must be at least 8 letters and contain \n" +
                    "- at least one small letter \n" +
                    "- at least one capital letter \n" +
                    "- at least one number \n" +
                    "- at least one of special character !@#$%^&*()_+-=[]{};':"

        return isEmailValid && isPasswordValid
    }


    private fun updateUI() {
        hideAppbar()
        prepareLoginSentence()
        prepareTermSentence()
    }

    private fun prepareLoginSentence() {
        val spannableString = SpannableString(resources.getString(R.string.HaveAccount))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                navigation.showLogin(requireContext())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                val color = resources.getColor(R.color.link, null)
                ds.color = color
            }
        }
        spannableString.setSpan(clickableSpan, 18, 25, 0)
        mBinding.login.text = spannableString
        mBinding.login.movementMethod = LinkMovementMethod()
    }

    private fun prepareTermSentence() {
        val spannableString = SpannableString(resources.getString(R.string.terms))
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(boldSpan, 40, 61, 0)
        mBinding.terms.text = spannableString
    }

    override fun sendAction(interaction: UseCase) {}
    override fun render() {}
    override fun idleState() {}
    override fun failureState(message: String) {
        mBinding.loading.visibility = View.GONE
        mBinding.register.visibility = View.VISIBLE
        Snackbar.make(mBinding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun successState(data: Any) {
        mBinding.loading.visibility = View.GONE
        mBinding.register.visibility = View.VISIBLE
        navigation.showStoryDetails(requireContext(), true)
        finish()
    }

    override fun loadingState() {
        mBinding.loading.visibility = View.VISIBLE
        mBinding.register.visibility = View.GONE
    }

}