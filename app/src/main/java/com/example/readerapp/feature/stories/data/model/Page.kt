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
import android.util.Log
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

    fun applyModifiers(modifier: List<Modifier>) {
        for (i in modifier) {
            modifiers.add(Modifier(i.start, i.end, i.modifierName, i.textColor))
            when (i.modifierName) {
                ModifierName.BOLD -> bold()
                ModifierName.UNDERLINE -> underline()
                ModifierName.HIGHLIGHTER -> highlighter()
                ModifierName.CHANGE_SENTENCE_COLOR -> changeSentenceColor(i.textColor!!)
                else -> {}
            }
        }
    }

    fun underline() {
        val lastModifier = updateModifier(ModifierName.UNDERLINE)
        val underlineSpan = underlineSpan()
        spannableString.setSpan(underlineSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun underlineSpan(): UnderlineSpan {
        return UnderlineSpan()
    }

    fun highlighter() {
        val lastModifier = updateModifier(ModifierName.HIGHLIGHTER)
        val highlightSpan = highlighterSpan(Color.YELLOW)
        spannableString.setSpan(highlightSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun highlighterSpan(color: Int): BackgroundColorSpan {
        return BackgroundColorSpan(color)
    }

    fun bold() {
        val lastModifier = updateModifier(ModifierName.BOLD)
        val boldSpan = boldSpan()
        spannableString.setSpan(boldSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun boldSpan(): StyleSpan {
        return StyleSpan(Typeface.BOLD)
    }

    fun changeSentenceColor(color: Int) {
        val lastModifier = updateModifier(ModifierName.CHANGE_SENTENCE_COLOR)
        lastModifier.textColor = color
        val foregroundSpan = foregroundColorSpan(color)
        spannableString.setSpan(foregroundSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun foregroundColorSpan(color: Int): ForegroundColorSpan {
        return ForegroundColorSpan(color)
    }

    private fun updateModifier(modifierName: ModifierName): Modifier {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = modifiers[modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = modifierName
        modifiers[modifiers.size - 1] = lastModifier
        return lastModifier
    }

    fun bookMark(recyclerView: RecyclerView, navigation: Navigation, goTo: Any) {
        val lastModifier = updateModifier(ModifierName.BOOK_MARK)
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