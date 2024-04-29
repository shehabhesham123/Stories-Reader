package com.example.readerapp.feature.stories.data.model


enum class ModifierName {
    HIGHLIGHTER,
    BOLD,
    UNDERLINE,
    CHANGE_SENTENCE_COLOR,
    BOOK_MARK,
    CHANGE_ALL_TEXT_COLOR,
    CHANGE_BACKGROUND_COLOR,
}

data class Modifier(
    var start: Int,
    var end: Int,
    var modifierName: ModifierName?,
    var textColor: Int?
)