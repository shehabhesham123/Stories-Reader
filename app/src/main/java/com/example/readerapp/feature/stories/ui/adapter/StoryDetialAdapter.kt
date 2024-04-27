package com.example.readerapp.feature.stories.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readerapp.R
import com.example.readerapp.core.navigation.Navigation
import com.example.readerapp.databinding.PageBinding
import com.example.readerapp.feature.auth.credentials.Authenticator
import com.example.readerapp.feature.stories.data.model.Modifier
import com.example.readerapp.feature.stories.data.model.ModifierName
import com.example.readerapp.feature.stories.data.model.Page
import com.example.readerapp.feature.stories.data.model.Query
import com.example.readerapp.feature.stories.data.model.Story
import com.example.readerapp.feature.stories.ui.activity.CustomActionMode

class StoryDetailAdapter(
    private val story: Story,
    private val listener: SelectionListener
) :
    RecyclerView.Adapter<StoryDetailAdapter.ViewHolder>() {

    private val pages = story.pages

    private var textSize = 16f
        set(value) {
            if (value in 16f..32f) field = value
        }

    private var textColor: Int? = null
    private var backgroundColor: Int? = null
    private var currentQuery: Query? = null
    private lateinit var context: Context
    private lateinit var recyclerView: RecyclerView
    private val navigation by lazy { Navigation(Authenticator.instance()) }

    class ViewHolder(
        private val mBinding: PageBinding,
        private val listener: SelectionListener,
    ) :
        RecyclerView.ViewHolder(mBinding.root) {


        fun bind(page: Page, textSize: Float, textColor: Int?, backgroundColor: Int?) {
            putData(page)
            setTextColor(page, textColor)
            setBackgroundColor(backgroundColor)
            setTextSize(textSize)
            textSelection(page)
            disableSelectionTools()
            addBookmarkToSelectionToolbar()
        }

        private fun putData(page: Page) {
            mBinding.pageBody.text = page.spannableString
            mBinding.pageNumber.text = page.number.toString()
            mBinding.pageBody.movementMethod = LinkMovementMethod()
        }

        private fun setTextColor(page: Page, color: Int?) {
            if (page.number == 1) {
                // that's mean that this page contain title, and the title don't apply to change text color
                mBinding.pageBody.text = page.spannableString
            } else {
                color?.let {
                    mBinding.pageBody.setTextColor(it)
                }
            }
        }

        private fun setBackgroundColor(color: Int?) {
            color?.let { mBinding.background.setBackgroundColor(it) }
        }

        private fun setTextSize(textSize: Float) {
            mBinding.pageBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        }

        private fun textSelection(page: Page) {
            mBinding.pageBody.viewTreeObserver.addOnPreDrawListener {

                mBinding.pageBody.post {
                    val start = mBinding.pageBody.selectionStart
                    val end = mBinding.pageBody.selectionEnd
                    if (start > -1 && end > 0 && start < end) {
                        val modifier = Modifier(start, end, null)
                        page.modifiers.add(modifier)
                        listener.onSelectionListener(page)
                    } else {
                        listener.onUnSelectionListener()
                    }
                }
                true // Return true to continue with the drawing process
            }
        }

        private fun disableSelectionTools() {
            mBinding.pageBody.setOnClickListener {
                listener.onUnSelectionListener()
            }
        }

        private fun addBookmarkToSelectionToolbar() {
            mBinding.pageBody.customSelectionActionModeCallback = CustomActionMode(listener)
        }

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = PageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(mBinding, listener)
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(pages[position], textSize, textColor, backgroundColor)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun zoomIn() {
        textSize += 4
        notifyData()
    }

    fun zoomOut() {
        textSize -= 4
        notifyData()
    }

    fun underline(page: Page) {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = page.modifiers[page.modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = ModifierName.UNDERLINE
        page.modifiers[page.modifiers.size - 1] = lastModifier
        val underlineSpan = underlineSpan()
        page.spannableString.setSpan(underlineSpan, lastModifier.start, lastModifier.end, 0)
        listener.onUnSelectionListener()
        notifyData()
    }

    private fun underlineSpan(): UnderlineSpan {
        return UnderlineSpan()
    }

    fun highlighter(page: Page) {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = page.modifiers[page.modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = ModifierName.HIGHLIGHTER
        page.modifiers[page.modifiers.size - 1] = lastModifier
        val highlightSpan = highlighterSpan(Color.YELLOW)
        page.spannableString.setSpan(highlightSpan, lastModifier.start, lastModifier.end, 0)
        listener.onUnSelectionListener()
        notifyData()
    }

    private fun highlighterSpan(color: Int): BackgroundColorSpan {
        return BackgroundColorSpan(color)
    }

    fun bold(page: Page) {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = page.modifiers[page.modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = ModifierName.BOLD
        page.modifiers[page.modifiers.size - 1] = lastModifier
        val boldSpan = boldSpan()
        page.spannableString.setSpan(boldSpan, lastModifier.start, lastModifier.end, 0)
        listener.onUnSelectionListener()
        notifyData()
    }

    private fun boldSpan(): StyleSpan {
        return StyleSpan(Typeface.BOLD)
    }

    fun changeSentenceColor(page: Page, color: Int) {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = page.modifiers[page.modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = ModifierName.CHANGE_SENTENCE_COLOR
        page.modifiers[page.modifiers.size - 1] = lastModifier
        val foregroundSpan = foregroundColorSpan(color)
        page.spannableString.setSpan(foregroundSpan, lastModifier.start, lastModifier.end, 0)
        listener.onUnSelectionListener()
        notifyData()
    }

    private fun foregroundColorSpan(color: Int): ForegroundColorSpan {
        return ForegroundColorSpan(color)
    }

    fun changeTextColor(color: Int) {
        textColor = color
        notifyData()
    }

    fun changeBackgroundColor(color: Int) {
        backgroundColor = color
        notifyData()
    }

    fun query(query: Query) {
        // cancel the current query then store this query in currentQuery
        cancelCurrentQuery()
        this.currentQuery = query

        for (i in query.indices) {
            val page = i / Page.PAGE_TEXT_SIZE + 1    // because first page for title
            val start = i % Page.PAGE_TEXT_SIZE
            val highlightSpan = BackgroundColorSpan(query.highlightColor)
            pages[page].spannableString.setSpan(
                highlightSpan,
                start,
                start + query.wordSize,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        notifyData()
    }

    fun cancelCurrentQuery() {
        currentQuery?.apply {
            for (i in indices) {
                val page = i / Page.PAGE_TEXT_SIZE + 1    // because first page for title
                val start = i % Page.PAGE_TEXT_SIZE
                val highlightSpan = highlighterSpan(Color.TRANSPARENT)
                pages[page].spannableString.setSpan(highlightSpan, start, start + wordSize, 0)
            }
        }
        notifyData()
    }

    fun bookMark(page: Page, goTo: Any) {
        // the last modifiers in page.modifiers has the start and end of selection sentence
        val lastModifier = page.modifiers[page.modifiers.size - 1]
        // to able to cache the modifiers
        lastModifier.modifierName = ModifierName.BOOK_MARK
        page.modifiers[page.modifiers.size - 1] = lastModifier
        val clickSpan = clickSpan(goTo)
        val iconSpan = iconSpan()
        page.spannableString.setSpan(clickSpan, lastModifier.start, lastModifier.end, 0)
        page.spannableString.setSpan(iconSpan, lastModifier.end, lastModifier.end + 1, 0)
        notifyData()
    }

    private fun clickSpan(goTo: Any): ClickableSpan {
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

                val color = context.resources.getColor(R.color.link, null)
                ds.color = color
            }
        }
    }

    private fun iconSpan(): ImageSpan {
        return ImageSpan(context, R.drawable.external_link)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyData() {
        textSize += 0.0001f
        notifyDataSetChanged()
    }

}