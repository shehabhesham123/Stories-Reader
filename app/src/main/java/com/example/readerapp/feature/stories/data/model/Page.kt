package com.example.readerapp.feature.stories.data.model

import android.text.SpannableString

class Page(
    spannableString: SpannableString,
    number: Int,
    modifiers: MutableList<Modifier>
) {
    var number: Int = number
        private set
    var spannableString: SpannableString = spannableString
        private set
    var modifiers: MutableList<Modifier> = modifiers
        private set

    companion object {
        const val PAGE_TEXT_SIZE = 1237   // 1237 letter per page
    }
}

enum class ModifierName {
    HIGHLIGHTER,
    BOLD,
    UNDERLINE,
    CHANGE_SENTENCE_COLOR,
    CHANGE_ALL_TEXT_COLOR,
    CHANGE_BACKGROUND_COLOR,
    BOOK_MARK,
}

data class Modifier(val start: Int, val end: Int, var modifierName: ModifierName?)