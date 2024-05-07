package com.example.readerapp.feature.stories.data.model

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.readerapp.core.navigation.Navigation
import com.example.readerapp.feature.stories.data.model.Page.Companion.PAGE_TEXT_SIZE
import com.example.readerapp.feature.stories.ui.activity.ModifierSpannableString
import com.google.gson.annotations.SerializedName
import kotlin.math.ceil
import kotlin.math.min

/**
 * no constructor because we don't create new obj of story
 * and the obj will creating by Gson
 */
class Story {

    /////////////////////  properties //////////////////////////
    var id: String? = null
    var title: String? = null
        private set
    var body: String? = null
        private set
    var cover: String? = null
        private set

    @SerializedName("titleModifiers")    // key name of this list in json
    lateinit var titleModifiers: List<Modifier>
        private set

    @SerializedName("bodyModifiers")     // key name of this list in json
    lateinit var bodyModifiers: List<Modifier>
        private set

    /////////////////////////////////////////////////////////////

    // no json, this variable to save instance of pages list
    private var pages: MutableList<Page>? = null

    // no json, this variable to save instance of currentQuery to able to cancel it
    private var currentQuery: Query? = null

    fun pages(recyclerView: RecyclerView?, navigation: Navigation?): MutableList<Page> {
        if (this.pages == null || this.pages!!.isEmpty()) {
            val pages = mutableListOf<Page>()
            // add the story title on first page
            pages.add(titleToPage())
            pages.addAll(splitBodyToPages(recyclerView, navigation))
            this.pages = pages
        }
        return this.pages!!
    }

    private fun titleToPage(): Page {
        val mss = ModifierSpannableString(SpannableString(title), mutableListOf())
        val titlePage = Page(mss, 1)
        // apply titleModifiers to this page
        titlePage.mss.applyModifiers(null, null, titleModifiers)
        return titlePage
    }

    private fun splitBodyToPages(recyclerView: RecyclerView?, navigation: Navigation?): List<Page> {
        val pages = mutableListOf<Page>()
        val numOfPages = ceil(body!!.length / PAGE_TEXT_SIZE.toDouble()).toInt()
        for (i in 0..<numOfPages) {
            val pageStart = (PAGE_TEXT_SIZE * i)
            val pageEnd = min((PAGE_TEXT_SIZE * (i + 1)), body!!.length)
            var pageBody = ""
            for (j in pageStart..<pageEnd) {
                pageBody += body!![j]
            }
            // add \n because tools hide the last lines
            // i + 2   ---> because the first page for title  (i start by 0)
            val mss = ModifierSpannableString(SpannableString(pageBody + "\n\n\n"), mutableListOf())
            val page = Page(mss, i + 2)
            // apply bodyModifiers to this page
            applyBodyModifiers(page, recyclerView, navigation)
            pages.add(page)
        }
        return pages
    }

    private fun applyBodyModifiers(
        page: Page,
        recyclerView: RecyclerView?,
        navigation: Navigation?
    ) {
        val modifiers = mutableListOf<Modifier>()
        val mss = page.mss
        for (i in bodyModifiers) {
            val pageNumber =
                i.start / PAGE_TEXT_SIZE + 2  // first page for title and the pages starting with number 2
            val newStart = i.start % PAGE_TEXT_SIZE
            val newEnd = i.end % PAGE_TEXT_SIZE
            if (pageNumber == page.number) {
                if (i.goTo == null) {
                    Log.i("TTTTT","${i.modifierName}")
                    modifiers.add(Modifier(newStart, newEnd, i.textColor, i.modifierName))
                }
                else {
                    if (i.goTo is String) modifiers.add(Modifier(newStart, newEnd, i.goTo as String))
                    else if (i.goTo is Int) modifiers.add(Modifier(newStart, newEnd, i.goTo as Int))
                }
            }
        }
        mss.applyModifiers(recyclerView, navigation, modifiers)
    }

    fun query(query: Query) {
        // cancel the current query then store this query in currentQuery
        cancelCurrentQuery()
        this.currentQuery = query

        for (i in query.indices) {
            val page = i / PAGE_TEXT_SIZE + 1    // because first page for title
            val start = i % PAGE_TEXT_SIZE
            val highlightSpan = BackgroundColorSpan(query.highlightColor)
            val pages = pages(null, null)
            pages[page].mss.spannableString.setSpan(
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
                val page = i / PAGE_TEXT_SIZE + 1    // because first page for title and base 0
                val start = i % PAGE_TEXT_SIZE
                val highlightSpan = BackgroundColorSpan(Color.TRANSPARENT)
                val pages = pages(null, null)
                pages[page].mss.spannableString.setSpan(
                    highlightSpan,
                    start,
                    start + wordSize,
                    0
                )
            }
        }
    }

    /*
        // if we need to get id for the new obj of story
        companion object {
            private var id = 0
            fun currentId() = (id++).toString()
        }
     */
}