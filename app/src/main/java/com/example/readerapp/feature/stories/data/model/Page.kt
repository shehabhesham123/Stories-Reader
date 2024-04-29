package com.example.readerapp.feature.stories.data.model

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.readerapp.R
import com.example.readerapp.core.navigation.Navigation

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
        const val PAGE_TEXT_SIZE = 1000   // 1000 letter per page
    }

    fun underline() {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = modifiers[modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = ModifierName.UNDERLINE
        modifiers[modifiers.size - 1] = lastModifier
        val underlineSpan = underlineSpan()
        spannableString.setSpan(underlineSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun underlineSpan(): UnderlineSpan {
        return UnderlineSpan()
    }

    fun highlighter() {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = modifiers[modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = ModifierName.HIGHLIGHTER
        modifiers[modifiers.size - 1] = lastModifier
        val highlightSpan = highlighterSpan(Color.YELLOW)
        spannableString.setSpan(highlightSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun highlighterSpan(color: Int): BackgroundColorSpan {
        return BackgroundColorSpan(color)
    }

    fun bold() {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = modifiers[modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = ModifierName.BOLD
        modifiers[modifiers.size - 1] = lastModifier
        val boldSpan = boldSpan()
        spannableString.setSpan(boldSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun boldSpan(): StyleSpan {
        return StyleSpan(Typeface.BOLD)
    }

    fun changeSentenceColor(color: Int) {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = modifiers[modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = ModifierName.CHANGE_SENTENCE_COLOR
        modifiers[modifiers.size - 1] = lastModifier
        val foregroundSpan = foregroundColorSpan(color)
        spannableString.setSpan(foregroundSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun foregroundColorSpan(color: Int): ForegroundColorSpan {
        return ForegroundColorSpan(color)
    }

    fun bookMark(recyclerView: RecyclerView, navigation: Navigation, goTo: Any) {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = modifiers[modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = ModifierName.BOOK_MARK
        modifiers[modifiers.size - 1] = lastModifier
        val clickSpan = clickSpan(recyclerView, navigation, goTo)
        val iconSpan = iconSpan(recyclerView.context)
        spannableString.setSpan(clickSpan, lastModifier.start, lastModifier.end, 0)
        spannableString.setSpan(iconSpan, lastModifier.end, lastModifier.end + 1, 0)
    }

    private fun clickSpan(
        recyclerView: RecyclerView,
        navigation: Navigation,
        goTo: Any
    ): ClickableSpan {
        return object : ClickableSpan() {
            override fun onClick(p0: View) {
                if (goTo is Int)
                    recyclerView.smoothScrollToPosition(goTo)
                else if (goTo is String) {
                    navigation.showExternalUrl(goTo, recyclerView.context)
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                val color = recyclerView.context.resources.getColor(R.color.link, null)
                ds.color = color
            }
        }
    }

    private fun iconSpan(context: Context): ImageSpan {
        return ImageSpan(context, R.drawable.external_link)
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