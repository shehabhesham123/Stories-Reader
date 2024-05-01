package com.example.readerapp.feature.stories.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readerapp.core.navigation.Navigation
import com.example.readerapp.databinding.PageBinding
import com.example.readerapp.feature.auth.credentials.Authenticator
import com.example.readerapp.feature.stories.data.model.Modifier
import com.example.readerapp.feature.stories.data.model.Page
import com.example.readerapp.feature.stories.data.model.Query
import com.example.readerapp.feature.stories.data.model.Story
import com.example.readerapp.feature.stories.ui.activity.CustomActionMode

class StoryDetailAdapter(
    private val story: Story,
    private val listener: SelectionListener
) :
    RecyclerView.Adapter<StoryDetailAdapter.ViewHolder>() {

    private val pages = story.pages()

    private var textSize = 16f
        set(value) {
            if (value in 16f..32f) field = value
        }

    private var textColor: Int? = null
    private var backgroundColor: Int? = null
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
            mBinding.pageBody.text = page.mss.spannableString
            mBinding.pageNumber.text = page.number.toString()
            mBinding.pageBody.movementMethod = LinkMovementMethod()
        }

        private fun setTextColor(page: Page, color: Int?) {
            if (page.number == 1) {
                // that's mean that this page contain title, and the title don't apply to change text color
                mBinding.pageBody.text = page.mss.spannableString
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
                    val mss = page.mss
                    if (start > -1 && end > 0 && start < end) {
                        val lastModifier = try {
                            mss.modifiers[mss.modifiers.size - 1]
                        } catch (e: IndexOutOfBoundsException) {      // modifiers is empty
                            null
                        }
                        if (lastModifier == null || lastModifier.modifierName != null) {
                            Log.i("Hello", "1")
                            val modifier = Modifier(start, end, null, null)
                            mss.modifiers.add(modifier)
                        } else {
                            Log.i("Hello", "2")
                            lastModifier.start = start
                            lastModifier.end = end
                        }
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
        page.mss.underline()
        listener.onUnSelectionListener()
        notifyData()
    }

    fun highlighter(page: Page) {
        page.mss.highlighter()
        listener.onUnSelectionListener()
        notifyData()
    }

    fun bold(page: Page) {
        page.mss.bold()
        listener.onUnSelectionListener()
        notifyData()
    }

    fun changeSentenceColor(page: Page, color: Int) {
        page.mss.changeSentenceColor(color)
        listener.onUnSelectionListener()
        notifyData()
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
        story.query(query)
        notifyData()
    }

    fun cancelCurrentQuery() {
        story.cancelCurrentQuery()
        notifyData()
    }

    fun bookMark(page: Page, goTo: Any) {
        page.mss.bookMark(recyclerView, navigation, goTo)
        notifyData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyData() {
        textSize += 0.0001f
        notifyDataSetChanged()
    }

}