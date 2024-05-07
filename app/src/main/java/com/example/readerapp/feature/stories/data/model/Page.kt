package com.example.readerapp.feature.stories.data.model

import com.example.readerapp.feature.stories.ui.activity.ModifierSpannableString

class Page(
    mss: ModifierSpannableString,
    number: Int,
) {
    var number: Int = number
        private set
    var mss: ModifierSpannableString = mss
        private set

    companion object {
        const val PAGE_TEXT_SIZE = 1000   // 1000 letter per page
    }

}