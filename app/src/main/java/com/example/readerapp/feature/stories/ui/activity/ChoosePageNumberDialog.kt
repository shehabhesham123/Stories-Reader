package com.example.readerapp.feature.stories.ui.activity

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.readerapp.databinding.BookmarkDialogBinding

class ChoosePageNumberDialog(
    private val mnNumber: Int,
    private val mxNumber: Int,
    private val onlyExternal: Boolean
) :
    DialogFragment() {

    private lateinit var clickListener: (value: Any) -> Unit
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = BookmarkDialogBinding.inflate(LayoutInflater.from(requireContext()), null, false)

        if (onlyExternal) {
            view.numberPicker.isEnabled = false
        } else {
            view.numberPicker.maxValue = mxNumber
            view.numberPicker.minValue = mnNumber
        }

        view.ok.setOnClickListener {
            val url = view.externalLink.text.toString().trim()
            if (onlyExternal) {
                showToast("You can't enter the page number, only the url can enter it")
                if (url.isNotEmpty() && (url.contains("http://") || url.contains("https://"))) {
                    clickListener(url)
                    dismiss()
                }
            } else {
                val value =
                    if (url.isNotEmpty() && (url.contains("http://") || url.contains("https://"))) {
                        url
                    } else {
                        showToast("You entered the page number, not the url")
                        view.numberPicker.value
                    }
                clickListener(value)
                dismiss()
            }

        }
        view.cancel.setOnClickListener {
            dismiss()
        }
        return AlertDialog.Builder(requireContext())
            .setView(view.root)
            .create()
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    // fluent interface
    fun setOnClickOnPositive(clickListener: (value: Any) -> Unit): ChoosePageNumberDialog {
        this.clickListener = clickListener
        return this
    }
}