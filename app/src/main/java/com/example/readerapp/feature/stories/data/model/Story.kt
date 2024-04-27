package com.example.readerapp.feature.stories.data.model

import android.text.SpannableString
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
    val pages: MutableList<Page>
        get() = convertBodyToPages()

    private fun convertBodyToPages(): MutableList<Page> {
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
        return pages
    }

    companion object {
        private var ID = 0
        fun currentId() = ID.toString()
    }
}