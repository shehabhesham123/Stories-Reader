package com.example.readerapp.feature.stories.data.model

import com.example.readerapp.feature.stories.data.model.Modifier.ModifierName.Companion.BOLD
import com.example.readerapp.feature.stories.data.model.Modifier.ModifierName.Companion.BOOK_MARK
import com.example.readerapp.feature.stories.data.model.Modifier.ModifierName.Companion.CHANGE_SENTENCE_COLOR
import com.example.readerapp.feature.stories.data.model.Modifier.ModifierName.Companion.HIGHLIGHTER
import com.example.readerapp.feature.stories.data.model.Modifier.ModifierName.Companion.UNDERLINE


class Modifier {

    /** this constructor for HIGHLIGHTER, BOLD ,UNDERLINE and when select new sentence*/
    constructor(start: Int, end: Int, modifierName: Char?) {
        this.start = start
        this.end = end
        this.modifierName = modifierName
    }

    /** this constructor for CHANGE_SENTENCE_COLOR
     * or when i don't know the modifier name*/
    constructor(
        start: Int,
        end: Int,
        textColor: Int?,
        modifierName: Char?
    ) {
        this.start = start
        this.end = end
        this.modifierName = modifierName
        this.textColor = textColor
    }

    /** this constructor for BOOK_MARK (page number)*/
    constructor(start: Int, end: Int, goTo: Int) {
        this.start = start
        this.end = end
        this.modifierName = BOOK_MARK
        this.goTo = goTo
    }

    /** this constructor for BOOK_MARK (link)*/

    constructor(start: Int, end: Int, goTo: String) {
        this.start = start
        this.end = end
        this.modifierName = BOOK_MARK
        this.goTo = goTo
    }

    var start: Int = 0
        set(value) {
            if (value > -1 && value < Int.MAX_VALUE) {
                field = value
            }
        }
    var end: Int = 0
        set(value) {
            if (value > -1 && value < Int.MAX_VALUE) {
                field = value
            }
        }
    var modifierName: Char? = null
        set(value) {
            if (value == BOOK_MARK || value == UNDERLINE || value == HIGHLIGHTER || value == BOLD || value == CHANGE_SENTENCE_COLOR) {
                field = value
            }
        }
    var textColor: Int? = null       // this variable for CHANGE_SENTENCE_COLOR modifier
        set(value) {
            if (value in Int.MIN_VALUE..<Int.MAX_VALUE || value == null)
                field = value
        }

    var goTo: Any? = null
        set(value) {
            if (value is Int) field = value
            else if (value is String) field = value
        }

    // we use static variables instead of using enum class
    class ModifierName {
        companion object {
            const val HIGHLIGHTER = 'H'
            const val BOLD = 'B'
            const val UNDERLINE = 'U'
            const val CHANGE_SENTENCE_COLOR = 'C'
            const val BOOK_MARK = 'M'
        }
    }
}