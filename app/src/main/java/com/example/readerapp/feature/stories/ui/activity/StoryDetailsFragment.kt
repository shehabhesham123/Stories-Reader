package com.example.readerapp.feature.stories.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.readerapp.R
import com.example.readerapp.core.interation.UseCase
import com.example.readerapp.core.platform.BaseFragment
import com.example.readerapp.core.storage.TempStorage
import com.example.readerapp.databinding.FragmentStoryDetailsBinding
import com.example.readerapp.feature.speech.Speech
import com.example.readerapp.feature.stories.data.model.ModifierList
import com.example.readerapp.feature.stories.data.model.ModifierPage
import com.example.readerapp.feature.stories.data.model.Page
import com.example.readerapp.feature.stories.data.model.Query
import com.example.readerapp.feature.stories.data.model.Story
import com.example.readerapp.feature.stories.interation.RetrieveModifiers
import com.example.readerapp.feature.stories.interation.SaveModifiers
import com.example.readerapp.feature.stories.ui.adapter.SelectionListener
import com.example.readerapp.feature.stories.ui.adapter.StoryDetailAdapter
import com.example.readerapp.feature.stories.ui.viewmodel.StoryDetailsViewModel
import com.example.readerapp.feature.stories.viewstate.Success
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch


@OptIn(DelicateCoroutinesApi::class)
class StoryDetailsFragment : BaseFragment(), SelectionListener {
    private lateinit var mBinding: FragmentStoryDetailsBinding
    private lateinit var mStory: Story
    private lateinit var adapter: StoryDetailAdapter
    private lateinit var mStoryDetailViewModel: StoryDetailsViewModel
    private lateinit var speechButton: MenuItem
    private var textToSpeech: Speech? = null

    // this variable will contains the page where the user select the sentence to add modifier on it
    private var mSelectionPage: Page? = null

    // Special tools which can be implemented in a sentence like (bold, highlighter, ....)
    private var isToolVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // to create option menu to search
        setHasOptionsMenu(true)


        mStoryDetailViewModel = ViewModelProvider(this)[StoryDetailsViewModel::class.java]

        val currentStory = TempStorage.instance().currentStory
        if (currentStory == null) {
            onDetach()
        } else {
            mStory = currentStory
        }

        sendAction(RetrieveModifiers(requireContext(), mStory.id))
        render()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStoryDetailsBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViewPager()
        initializeTextSpeech()

        listeners()

    }

    private fun initializeViewPager() {
        adapter = StoryDetailAdapter(mStory, this)
        mBinding.viewPager.adapter = adapter
    }

    private fun initializeTextSpeech() {
        textToSpeech = Speech(requireContext())
        textToSpeech!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                if (utteranceId.equals("uniqueId")) {
                    GlobalScope.launch(Dispatchers.Main) {
                        hideProgress()
                    }
                }
            }

            override fun onDone(utteranceId: String?) {
                if (utteranceId.equals("uniqueId")) {
                    GlobalScope.launch(Dispatchers.Main) {
                        hideProgress()
                        speechButton.isEnabled = true
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                if (utteranceId.equals("uniqueId")) {
                    GlobalScope.launch(Dispatchers.Main) {
                        hideProgress()
                        speechButton.isEnabled = true
                    }
                }
            }
        })
    }

    private fun listeners() {
        // when click on any one
        // that's mean that the user select sentence
        // and the special tools is shown and the mSelectionPage contains of value
        mBinding.tools.zoomIn.setOnClickListener {
            zoomIn()
        }
        mBinding.tools.zoomOut.setOnClickListener {
            zoomOut()
        }
        mBinding.tools.highlighter.setOnClickListener {
            highlighter()
        }
        mBinding.tools.underline.setOnClickListener {
            underline()
        }
        mBinding.tools.bold.setOnClickListener {
            bold()
        }
        mBinding.tools.textColor.setOnClickListener {
            // there is two logic of this feature
            // first when click and special tools is hidden ---> so the user wants to change color of all text
            // second when click and special tools is shown ---> so the user wants to change color of specific sentence
            showColorPicker(false)
        }
        mBinding.tools.backgroundColor.setOnClickListener {
            showColorPicker(true)
        }
    }

    private fun zoomIn() {
        adapter.zoomIn()
    }

    private fun zoomOut() {
        adapter.zoomOut()
    }

    private fun underline() {
        mSelectionPage?.let { adapter.underline(it) }
    }

    private fun bold() {
        mSelectionPage?.let { adapter.bold(it) }
    }

    private fun highlighter() {
        mSelectionPage?.let { adapter.highlighter(it) }
    }

    private fun showColorPicker(isColorForBackground: Boolean) {
        // color picker will show in to cases
        // 1 - to change the background color
        // 2 - to change the text color

        ColorPickerDialog
            .Builder(requireContext())                            // Pass Activity Instance
            .setTitle("Pick Color")                // Default "Choose Color"
            .setColorShape(ColorShape.SQAURE)    // Default ColorShape.CIRCLE
            .setDefaultColor(R.color.black)        // Pass Default Color
            .setColorListener { color, _ ->

                if (isColorForBackground) changeBackgroundColor(color)
                else {
                    // the text change feature has two logic
                    // first when click and special tools is hidden ---> so the user wants to change color of all text
                    // second when click and special tools is shown ---> so the user wants to change color of specific sentence
                    if (isToolVisible) changeSelectionSentenceColor(color)
                    else changeTextColor(color)
                }
            }
            .show()
    }

    private fun changeBackgroundColor(color: Int) {
        adapter.changeBackgroundColor(color)
    }

    private fun changeTextColor(color: Int) {
        adapter.changeTextColor(color)
    }

    private fun changeSelectionSentenceColor(color: Int) {
        mSelectionPage?.let { adapter.changeSentenceColor(it, color) }
    }

    private fun doQuery(query: String?) {
        if (!query.isNullOrEmpty()) {
            val color = resources.getColor(R.color.blue_light, null)
            val queryObj =
                Query.query(query, mStory.body, color)
            adapter.query(queryObj)
        }
    }

    private fun cancelQuery() {
        adapter.cancelCurrentQuery()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search, menu)

        val item = menu.findItem(R.id.search)
        val save = menu.findItem(R.id.save)
        speechButton = menu.findItem(R.id.speech)
        val searchView = item?.actionView as SearchView
        updateSearchView(searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                doQuery(query)
                return false
            }

            override fun onQueryTextChange(newText: String?) = false
        })

        searchView.setOnCloseListener {
            cancelQuery()
            false
        }

        save.setOnMenuItemClickListener {
            val modifierList = ModifierList()
            for (i in mStory.pages()) {
                val pageEntity = ModifierPage(i.number, i.mss.modifiers)
                modifierList.list.add(pageEntity)
            }
            sendAction(SaveModifiers(requireContext(), modifierList, mStory.id))
            false
        }

        speechButton.setOnMenuItemClickListener {
            loadingState()
            speechButton.isEnabled = false
            speech()
            false
        }
    }

    private fun speech() {
        val currentPage = mStory.pages()[mBinding.viewPager.currentItem]
        textToSpeech?.startSpeech(currentPage.mss.spannableString.toString())
    }

    private fun updateSearchView(searchView: SearchView) {
        searchView.maxWidth = Int.MAX_VALUE
        searchView.isSubmitButtonEnabled = true
        // to change the icon
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        val icon = ContextCompat.getDrawable(requireActivity(), R.drawable.search)
        searchIcon.setImageDrawable(icon)
    }

    private fun showTools() {
        Log.i("Hello", "$isToolVisible")
        if (!isToolVisible) {
            isToolVisible = true
            mBinding.tools.bold.visibility = View.VISIBLE
            mBinding.tools.highlighter.visibility = View.VISIBLE
            mBinding.tools.underline.visibility = View.VISIBLE
            mBinding.tools.zoomIn.visibility = View.GONE
            mBinding.tools.zoomOut.visibility = View.GONE
            mBinding.tools.backgroundColor.visibility = View.GONE
        }
    }

    private fun hideTools() {
        if (isToolVisible) {
            isToolVisible = false
            mBinding.tools.zoomIn.visibility = View.VISIBLE
            mBinding.tools.zoomOut.visibility = View.VISIBLE
            mBinding.tools.backgroundColor.visibility = View.VISIBLE
            mBinding.tools.bold.visibility = View.GONE
            mBinding.tools.highlighter.visibility = View.GONE
            mBinding.tools.underline.visibility = View.GONE
        }
    }

    // SelectionListener
    override fun onSelectionListener(page: Page) {
        mSelectionPage = page
        showTools()
    }

    override fun onUnSelectionListener() {
        hideTools()
    }

    override fun clickOnBookmark() {
        // mSelectionPage should have value
        var errorMessage = "error"
        mSelectionPage?.let {
            errorMessage = if (it.number == 1) {
                "You can't add bookmark in title"
            } else if (mStory.pages().size == 2) {
                showBookmarkDialog(it, true)
                return
            } else {
                showBookmarkDialog(it)
                return
            }
        }
        Snackbar.make(
            mBinding.root,
            errorMessage,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showBookmarkDialog(page: Page, onlyExternal: Boolean = false) {
        ChoosePageNumberDialog(2, mStory.pages().size, onlyExternal)
            .setOnClickOnPositive { value ->
                if (value is String) {
                    // this value if external url
                    adapter.bookMark(page, value)
                } else if (value is Int) {
                    // this value if numberPage
                    adapter.bookMark(page, value - 1)
                }
            }
            .show(parentFragmentManager, null)
    }

    // BaseFragment
    // but here i don't get data
    override fun sendAction(interaction: UseCase) {
        lifecycleScope.launch(Dispatchers.IO) {
            mStoryDetailViewModel.interactionChannel.send(interaction)
        }
    }

    override fun render() {
        lifecycleScope.launch {
            mStoryDetailViewModel.viewStateChannel.consumeAsFlow().buffer().collect {
                if (it is Success) {
                    successState(it.data)
                }
            }
        }
    }

    override fun idleState() {}

    override fun failureState(message: String) {}

    @SuppressLint("NotifyDataSetChanged")
    override fun successState(data: Any) {
        if (data is String) {
            // SaveModifiers interaction
            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
        } else {
            val list = (data as ModifierList).list
            for (i in list) {
                mStory.pages()[i.number - 1].mss.applyModifiers(i.modifiers) // base 0
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun loadingState() {
        showProgress()
    }

    override fun onStop() {
        textToSpeech?.stopSpeech()
        super.onStop()
    }


}