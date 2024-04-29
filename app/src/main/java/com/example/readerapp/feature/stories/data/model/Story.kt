package com.example.readerapp.feature.stories.data.model

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import com.example.readerapp.feature.stories.data.model.Page.Companion.PAGE_TEXT_SIZE
import kotlin.math.ceil
import kotlin.math.min

class Story(title: String, body: String) {
    var id: String = currentId()
        private set
    var title: String = title
        private set
    var body: String = body
        private set

    private var pages: MutableList<Page>? = null

    private var currentQuery: Query? = null


    fun pages(): MutableList<Page> {
        if (this.pages == null) {
            val pages = mutableListOf<Page>()
            // add the story title on first page
            pages.add(Page(SpannableString(title), 1, mutableListOf()))

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
                val page = Page(SpannableString(pageBody + "\n\n\n"), i + 2, mutableListOf())
                pages.add(page)
            }
            this.pages = pages
        }
        return this.pages!!
    }

    fun query(query: Query) {
        // cancel the current query then store this query in currentQuery
        cancelCurrentQuery()
        this.currentQuery = query

        for (i in query.indices) {
            val page = i / PAGE_TEXT_SIZE + 1    // because first page for title
            val start = i % PAGE_TEXT_SIZE
            val highlightSpan = BackgroundColorSpan(query.highlightColor)
            pages()[page].spannableString.setSpan(
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
                pages()[page].spannableString.setSpan(highlightSpan, start, start + wordSize, 0)
            }
        }
    }

    companion object {
        private var ID = 0
        fun currentId() = ID.toString()
    }
}