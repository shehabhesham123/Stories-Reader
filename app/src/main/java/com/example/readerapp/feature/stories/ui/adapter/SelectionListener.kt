package com.example.readerapp.feature.stories.ui.adapter

import com.example.readerapp.feature.stories.data.model.Page

interface SelectionListener {
    fun onSelectionListener(page: Page)
    fun onUnSelectionListener()

    fun clickOnBookmark()
}