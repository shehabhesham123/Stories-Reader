package com.example.readerapp.feature.stories.ui.activity

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.MetricAffectingSpan
import android.text.style.UpdateAppearance
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.readerapp.R
import com.example.readerapp.core.navigation.Navigation
import com.example.readerapp.feature.stories.data.model.Modifier
import com.example.readerapp.feature.stories.data.model.Modifier.ModifierName

/**
 * this class this spannable class and i can save the modifiers which the user did them on the text
 * to able to save and retrieve them
 */
class ModifierSpannableString(
    spannableString: SpannableString,
    modifiers: MutableList<Modifier>
) {
    var spannableString: SpannableString = spannableString
        private set
    var modifiers: MutableList<Modifier> = modifiers
        private set

    // we need to recyclerView and navigation to bookmark
    fun applyModifiers(
        recyclerView: RecyclerView?,
        navigation: Navigation?,
        modifier: List<Modifier>
    ) {
        for (i in modifier) {
            when (i.modifierName) {
                ModifierName.BOLD -> {
                    modifiers.add(i)
                    bold(false)
                }

                ModifierName.UNDERLINE -> {
                    modifiers.add(i)
                    underline(false)
                }

                ModifierName.HIGHLIGHTER -> {
                    modifiers.add(i)
                    highlighter(false)
                }

                ModifierName.CHANGE_SENTENCE_COLOR -> {
                    modifiers.add(i)
                    changeSentenceColor(i.textColor!!)
                }

                ModifierName.BOOK_MARK -> {
                    modifiers.add(i)
                    bookMark(recyclerView!!, navigation!!, i.goTo!!)
                }

                else -> {}
            }
        }
    }

    // isClicked mean that the user click on tool
    // because this fun called by code in the first to apply the saved modifiers
    fun underline(isClicked: Boolean) {
        val lastModifier: Modifier
        // we use isClicked, because when applyModifiers, the spanIdx will have value so will removeSpan and this is error
        val underlineSpan: CharacterStyle = if (isClicked) {
            val spanIdx = idxOfSelectionSentenceSpan(ModifierName.UNDERLINE)
            if (spanIdx != -1) {
                lastModifier = updateModifier(null)
                remove(spanIdx, lastModifier, ModifierName.UNDERLINE)
                removeUnderlineSpan()
            } else {
                lastModifier = updateModifier(ModifierName.UNDERLINE)
                underlineSpan()
            }
        } else {
            lastModifier = updateModifier(ModifierName.UNDERLINE)
            underlineSpan()
        }
        spannableString.setSpan(underlineSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun underlineSpan(): CharacterStyle {
        return CustomUnderlineSpan(true)
    }

    private fun removeUnderlineSpan(): CharacterStyle {
        return CustomUnderlineSpan(false)
    }

    fun highlighter(isClicked: Boolean) {
        val lastModifier: Modifier
        // we use isClicked, because when applyModifiers, the spanIdx will have value so will removeSpan and this is error
        val highlightSpan: CharacterStyle = if (isClicked) {
            val spanIdx = idxOfSelectionSentenceSpan(ModifierName.HIGHLIGHTER)
            if (spanIdx != -1) {
                lastModifier = updateModifier(null)
                remove(spanIdx, lastModifier, ModifierName.HIGHLIGHTER)
                removeHighlighterSpan()
            } else {
                lastModifier = updateModifier(ModifierName.HIGHLIGHTER)
                highlighterSpan(Color.YELLOW)
            }
        } else {
            lastModifier = updateModifier(ModifierName.HIGHLIGHTER)
            highlighterSpan(Color.YELLOW)
        }
        spannableString.setSpan(highlightSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun highlighterSpan(color: Int): CharacterStyle {
        return BackgroundColorSpan(color)
    }

    private fun removeHighlighterSpan(): CharacterStyle {
        return highlighterSpan(android.R.color.transparent)
    }

    fun bold(isClicked: Boolean) {
        val lastModifier: Modifier
        // we use isClicked, because when applyModifiers, the spanIdx will have value so will removeSpan and this is error
        val boldSpan: CharacterStyle = if (isClicked) {
            val spanIdx = idxOfSelectionSentenceSpan(ModifierName.BOLD)
            if (spanIdx != -1) {
                lastModifier = updateModifier(null)
                remove(spanIdx, lastModifier, ModifierName.BOLD)
                removeBoldSpan()
            } else {
                lastModifier = updateModifier(ModifierName.BOLD)
                boldSpan()
            }
        } else {
            lastModifier = updateModifier(ModifierName.BOLD)
            boldSpan()
        }

        spannableString.setSpan(boldSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun boldSpan(): CharacterStyle {
        return CustomStyleSpan(true)
    }

    private fun removeBoldSpan(): CharacterStyle {
        return CustomStyleSpan(false)
    }

    fun changeSentenceColor(color: Int) {
        val lastModifier = updateModifier(ModifierName.CHANGE_SENTENCE_COLOR)
        lastModifier.textColor = color
        val foregroundSpan = foregroundColorSpan(color)
        spannableString.setSpan(foregroundSpan, lastModifier.start, lastModifier.end, 0)
    }

    private fun foregroundColorSpan(color: Int): CharacterStyle {
        return ForegroundColorSpan(color)
    }

    private fun updateModifier(modifierName: Char?): Modifier {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = modifiers[modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = modifierName
        modifiers[modifiers.size - 1] = lastModifier
        return lastModifier
    }

    fun bookMark(recyclerView: RecyclerView, navigation: Navigation, goTo: Any) {
        val lastModifier = updateModifier(ModifierName.BOOK_MARK)
        lastModifier.goTo = goTo
        val clickSpan = clickSpan(recyclerView, navigation, goTo)
        val iconSpan = iconSpan(recyclerView.context)
        spannableString.setSpan(clickSpan, lastModifier.start, lastModifier.end, 0)
        spannableString.setSpan(iconSpan, lastModifier.end, lastModifier.end + 1, 0)
    }

    private fun clickSpan(
        recyclerView: RecyclerView,
        navigation: Navigation,
        goTo: Any
    ): CharacterStyle {
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

    private fun iconSpan(context: Context): CharacterStyle {
        return ImageSpan(context, R.drawable.external_link)
    }

    fun haveSelectionSentenceSpans(): List<Char?> {
        val lastModifier = modifiers[modifiers.size - 1]
        val start = lastModifier.start
        val end = lastModifier.end
        val result = mutableListOf<Char?>()
        for (i in modifiers) {
            if (start >= i.start && end <= i.end && i.modifierName != null) {
                result.add(i.modifierName)
            }
        }
        return result
    }

    private fun idxOfSelectionSentenceSpan(modifierName: Char): Int {
        val lastModifier = modifiers[modifiers.size - 1]
        val start = lastModifier.start
        val end = lastModifier.end
        var idx = -1
        for ((j, i) in modifiers.withIndex()) {
            if (start >= i.start && end <= i.end && i.modifierName == modifierName) {
                idx = j
            }
        }
        return idx
    }

    private fun remove(spanIdx: Int, lastModifier: Modifier, modifierName: Char) {
        val span = modifiers[spanIdx]
        if (span.start == lastModifier.start && span.end == lastModifier.end) {
            modifiers.removeAt(spanIdx)
        } else {
            val list = mutableListOf<Modifier>()
            if (span.start < lastModifier.start) {
                list.add(
                    Modifier(
                        span.start,
                        lastModifier.start,
                        modifierName
                    )
                )
            }
            if (span.end > lastModifier.end) {
                list.add(
                    Modifier(
                        lastModifier.end,
                        span.end,
                        modifierName
                    )
                )
            }

            modifiers.removeAt(spanIdx)
            modifiers.addAll(spanIdx, list)
        }
    }


    // to able to set underline span and remove it
    private class CustomUnderlineSpan(private val shouldUnderline: Boolean) : CharacterStyle(),
        UpdateAppearance {

        override fun updateDrawState(tp: TextPaint) {
            tp.isUnderlineText = shouldUnderline
        }
    }

    // to able to set Bold span and remove it
    private class CustomStyleSpan(private val shouldApplyStyle: Boolean) : MetricAffectingSpan() {
        override fun updateDrawState(paint: TextPaint) {
            paint.isFakeBoldText = shouldApplyStyle
        }

        override fun updateMeasureState(paint: TextPaint) {
            paint.isFakeBoldText = shouldApplyStyle
        }
    }
}
