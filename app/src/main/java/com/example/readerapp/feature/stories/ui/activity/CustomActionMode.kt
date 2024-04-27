package com.example.readerapp.feature.stories.ui.activity

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.example.readerapp.R
import com.example.readerapp.feature.stories.ui.adapter.SelectionListener

class CustomActionMode(private val listener: SelectionListener) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        menu.clear()
        mode.menuInflater.inflate(R.menu.bookmark, menu)
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bookmark -> {
                // ensure that listener.onSelectionListener(page) is called
                // and fragment store this page
                listener.clickOnBookmark()
                true
            }

            else -> false
        }
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode) {}
}