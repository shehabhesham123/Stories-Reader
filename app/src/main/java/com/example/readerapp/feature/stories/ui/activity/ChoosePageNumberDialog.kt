package com.example.readerapp.feature.stories.ui.activity

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.readerapp.databinding.BookmarkDialogBinding

class ChoosePageNumberDialog(private val mnNumber: Int, private val mxNumber: Int) :
    DialogFragment() {

    private lateinit var clickListener: (value: Any) -> Unit
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = BookmarkDialogBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        view.numberPicker.maxValue = mxNumber
        view.numberPicker.minValue = mnNumber

        view.ok.setOnClickListener {
            val url = view.externalLink.text.toString().trim()
            val value =
                if (url.isNotEmpty() && (url.contains("http://") || url.contains("https://"))) {
                    url
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You entered the page number, not the url",
                        Toast.LENGTH_SHORT
                    ).show()
                    view.numberPicker.value
                }
            clickListener(value)
            dismiss()
        }
        view.cancel.setOnClickListener {
            dismiss()
        }
        return AlertDialog.Builder(requireContext())
            .setView(view.root)
            .create()
    }

    // fluent interface
    fun setOnClickOnPositive(clickListener: (value: Any) -> Unit): ChoosePageNumberDialog {
        this.clickListener = clickListener
        return this
    }
}