package com.example.readerapp.feature.stories.data.model

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import com.example.readerapp.feature.stories.data.model.Page.Companion.PAGE_TEXT_SIZE
import com.example.readerapp.feature.stories.ui.ModifierSpannableString
import com.google.gson.annotations.SerializedName
import kotlin.math.ceil
import kotlin.math.min

class Story(title: String, body: String) {
    var id: String = currentId()
        private set
    var title: String = title
        private set
    var body: String = body
        private set

    @SerializedName("titleModifiers")
    lateinit var titleModifiers: List<Modifier>
        private set

    @SerializedName("bodyModifiers")
    lateinit var bodyModifiers: List<Modifier>
        private set


    // no json
    private var pages: MutableList<Page>? = null

    // no json
    private var currentQuery: Query? = null

    fun pages(): MutableList<Page> {
        if (this.pages == null || this.pages!!.isEmpty()) {
            val pages = mutableListOf<Page>()
            // add the story title on first page
            var mss = ModifierSpannableString(SpannableString(title), mutableListOf())
            val titlePage = Page(mss, 1)
            // apply titleModifiers to this page
            applyModifiers(titlePage)
            pages.add(titlePage)

            val numOfPages = ceil(body.length / PAGE_TEXT_SIZE.toDouble()).toInt()
            for (i in 0..<numOfPages) {
                val pageStart = (PAGE_TEXT_SIZE * i)
                val pageEnd = min((PAGE_TEXT_SIZE * (i + 1)), body.length)
                var pageBody = ""
                for (j in pageStart..<pageEnd) {
                    pageBody += body[j]
                }
                // add \n because tools hide the last lines
                // i + 2   ---> because the first page for title  (i start by 0)
                mss = ModifierSpannableString(SpannableString(pageBody + "\n\n\n"), mutableListOf())
                val page = Page(mss, i + 2)
                // apply bodyModifiers to this page
                applyModifiers(page)
                pages.add(page)
            }
            this.pages = pages
        }
        return this.pages!!
    }

    private fun applyModifiers(page: Page) {
        if (page.number == 1) {
            titleModifiers(page)
        } else {
            bodyModifiers(page)
        }
    }

    private fun titleModifiers(page: Page) {
        for (i in titleModifiers) {
            val mss = page.mss
            mss.modifiers.add(Modifier(i.start, i.end, i.modifierName, i.textColor))
            when (i.modifierName) {
                ModifierName.BOLD -> mss.bold()
                ModifierName.UNDERLINE -> mss.underline()
                ModifierName.HIGHLIGHTER -> mss.highlighter()
                ModifierName.CHANGE_SENTENCE_COLOR -> mss.changeSentenceColor(i.textColor!!)
                else -> {}
            }
        }
    }

    private fun bodyModifiers(page: Page) {
        for (i in bodyModifiers) {
            val mss = page.mss
            val newStart = i.start % PAGE_TEXT_SIZE
            val newEnd = i.end % PAGE_TEXT_SIZE
            mss.modifiers.add(Modifier(newStart, newEnd, i.modifierName, i.textColor))
            when (i.modifierName) {
                ModifierName.BOLD -> mss.bold()
                ModifierName.UNDERLINE -> mss.underline()
                ModifierName.HIGHLIGHTER -> mss.highlighter()
                ModifierName.CHANGE_SENTENCE_COLOR -> mss.changeSentenceColor(i.textColor!!)
                else -> {}
            }
        }
    }

    fun query(query: Query) {
        // cancel the current query then store this query in currentQuery
        cancelCurrentQuery()
        this.currentQuery = query

        for (i in query.indices) {
            val page = i / PAGE_TEXT_SIZE + 1    // because first page for title
            val start = i % PAGE_TEXT_SIZE
            val highlightSpan = BackgroundColorSpan(query.highlightColor)
            pages()[page].mss.spannableString.setSpan(
                highlightSpan,
                start,
                start + query.wordSize,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    fun cancelCurrentQuery() {
        currentQuery?.apply {
            for (i in indices) {
                val page = i / PAGE_TEXT_SIZE + 1    // because first page for title
                val start = i % PAGE_TEXT_SIZE
                val highlightSpan = BackgroundColorSpan(Color.TRANSPARENT)
                pages()[page].mss.spannableString.setSpan(
                    highlightSpan,
                    start,
                    start + wordSize,
                    0
                )
            }
        }
    }

    companion object {
        private var ID = 0
        fun currentId() = ID.toString()
    }
}