package com.example.readerapp.core.validation

import android.util.Log
import androidx.core.util.PatternsCompat
import java.util.regex.Pattern

class Validation {
    companion object {
        fun isEmailValid(email: String): Boolean {
            return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isPasswordValid(password: String): Boolean {
            val passwordPattern = Pattern.compile(
                "^(?=.*[0-9])" +           // At least one digit
                        "(?=.*[a-z])" +            // At least one lowercase letter
                        "(?=.*[A-Z])" +            // At least one uppercase letter
                        "(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])" + // At least one special character
                        "(?=\\S+\$)" +             // No whitespace allowed
                        ".{8,}" +                  // Minimum 8 characters
                        "$"
            )
            return passwordPattern.matcher(password).matches()
        }

        fun isPhoneValid(phone: String): Boolean {
            val phonePattern = Pattern.compile(
                "^\\+201([0125])[0-9]{8}$"
            )
            return phonePattern.matcher(phone).matches()
        }
    }
}