package com.example.readerapp.feature.stories.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
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
import androidx.viewpager2.widget.ViewPager2
import com.example.readerapp.R
import com.example.readerapp.core.interation.UseCase
import com.example.readerapp.core.navigation.Navigation
import com.example.readerapp.core.network.NetworkHandler
import com.example.readerapp.core.platform.BaseFragment
import com.example.readerapp.core.storage.TempStorage
import com.example.readerapp.databinding.FragmentStoryDetailsBinding
import com.example.readerapp.feature.auth.credentials.Authenticator
import com.example.readerapp.feature.speech.Speech
import com.example.readerapp.feature.stories.data.model.Modifier.ModifierName
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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(DelicateCoroutinesApi::class)
class StoryDetailsFragment : BaseFragment(), SelectionListener {
    private lateinit var mBinding: FragmentStoryDetailsBinding
    private lateinit var mStory: Story
    private lateinit var mAdapter: StoryDetailAdapter
    private lateinit var mStoryDetailViewModel: StoryDetailsViewModel
    private lateinit var mAuthenticator: Authenticator
    private lateinit var mNavigation: Navigation
    private lateinit var mSpeechButton: MenuItem
    private var mTTs: Speech? = null

    // this variable will contains the page where the user select the sentence to add modifier on it
    private var mSelectionPage: Page? = null

    // Special tools which can be implemented in a sentence like (bold, highlighter, ....)
    private var isToolVisible = false

    private var inFullScreenMode = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mStoryDetailViewModel = ViewModelProvider(this)[StoryDetailsViewModel::class.java]

        // to create option menu to search
        setHasOptionsMenu(true)

        mAuthenticator = Authenticator(requireContext())
        mNavigation = Navigation(mAuthenticator)

        getSelectedStory()

        sendAction(RetrieveModifiers(requireContext(), mStory.id!!))
        render()
    }

    private fun getSelectedStory() {
        val currentStory = TempStorage.instance().currentStory
        if (currentStory == null) {
            finish()
        } else {
            mStory = currentStory
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        mAdapter = StoryDetailAdapter(mStory, this)
        mBinding.viewPager.adapter = mAdapter
    }

    private fun initializeTextSpeech() {
        if (NetworkHandler.isNetworkAvailable(requireContext())) {
            mTTs = Speech(requireContext())
            mTTs!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    if (utteranceId.equals("uniqueId")) {
                        GlobalScope.launch(Dispatchers.Main) {
                            idleState()
                        }
                    }
                }

                override fun onDone(utteranceId: String?) {
                    if (utteranceId.equals("uniqueId")) {
                        GlobalScope.launch(Dispatchers.Main) {
                            idleState()
                            mSpeechButton.setIcon(R.drawable.speech)

                        }
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    if (utteranceId.equals("uniqueId")) {
                        GlobalScope.launch(Dispatchers.Main) {
                            failureState("Error")
                            mSpeechButton.setIcon(R.drawable.speech)
                        }
                    }
                }
            })
        }
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
        mBinding.fullScreen.setOnClickListener {
            if (!inFullScreenMode)
                enableFullScreenMode()
            else disableFullScreenMode()
        }
    }

    private fun enableFullScreenMode() {
        inFullScreenMode = true
        hideAppbar()
        hideAllTools()
        mBinding.fullScreen.setImageResource(R.drawable.normal_screen_com)
        mAdapter.fullScreenMode(true)
        cancelQuery()
    }

    private fun disableFullScreenMode() {
        inFullScreenMode = false
        showAppbar()
        showAllTools()
        mBinding.fullScreen.setImageResource(R.drawable.full_screen)
        mAdapter.fullScreenMode(false)

    }

    private fun zoomIn() {
        mAdapter.zoomIn()
    }

    private fun zoomOut() {
        mAdapter.zoomOut()
    }

    private fun underline() {
        mSelectionPage?.let { mAdapter.underline(it) }
    }

    private fun bold() {
        mSelectionPage?.let { mAdapter.bold(it) }
    }

    private fun highlighter() {
        mSelectionPage?.let { mAdapter.highlighter(it) }
    }

    private fun showColorPicker(isColorForBackground: Boolean) {
        // color picker will show in to cases
        // 1 - to change the background color
        // 2 - to change the text color

        ColorPickerDialog.Builder(requireContext())                            // Pass Activity Instance
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
            }.show()
    }

    private fun changeBackgroundColor(color: Int) {
        mAdapter.changeBackgroundColor(color)
    }

    private fun changeTextColor(color: Int) {
        mAdapter.changeTextColor(color)
    }

    private fun changeSelectionSentenceColor(color: Int) {
        mSelectionPage?.let { mAdapter.changeSentenceColor(it, color) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search, menu)

        val searchView = menu.findItem(R.id.search)?.actionView as SearchView
        val save = menu.findItem(R.id.save)
        // store speech item in property to enable and disable it
        mSpeechButton = menu.findItem(R.id.speech)

        onChangePageListener()

        updateSearchMenuItem(searchView)
        searchMenuItemListeners(searchView)

        save.setOnMenuItemClickListener {
            if (mAuthenticator.isUserLogin()) {
                saveModifiers()
            } else {
                mNavigation.showSubscribeActivity(requireContext())
            }
            false
        }

        mSpeechButton.setOnMenuItemClickListener {
            if (mTTs!!.isSpeaking) {
                mTTs?.stopSpeech()
                mSpeechButton.setIcon(R.drawable.speech)
            }   // that's mean that the user click on pause speech
            else {
                if (mAuthenticator.isUserLogin()) {
                    if (NetworkHandler.isNetworkAvailable(requireContext())) {
                        speech()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No internet connection",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    mNavigation.showSubscribeActivity(requireContext())
                }
            }
            false
        }
    }

    private fun updateSearchMenuItem(searchView: SearchView) {
        searchView.maxWidth = Int.MAX_VALUE
        searchView.isSubmitButtonEnabled = true
        // to change the icon
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        val icon = ContextCompat.getDrawable(requireActivity(), R.drawable.search)
        searchIcon.setImageDrawable(icon)
    }

    private fun searchMenuItemListeners(searchView: SearchView) {
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
    }

    private fun saveModifiers() {
        val modifierList = ModifierList()
        for (i in mStory.pages(null, null)) {
            val pageEntity = ModifierPage(i.number, i.mss.modifiers)
            modifierList.list.add(pageEntity)
        }
        sendAction(SaveModifiers(requireContext(), modifierList, mStory.id!!))
    }

    private fun doQuery(query: String?) {
        if (query!!.trim().isNotEmpty()) {
            val color = resources.getColor(R.color.blue_light, null)
            GlobalScope.launch(Dispatchers.IO) {
                val queryObj = GlobalScope.async {
                    Query.query(query, mStory.body!!, color)
                }
                withContext(Dispatchers.Main){
                    mAdapter.query(queryObj.await())
                }
            }
        }
    }

    private fun cancelQuery() {
        mAdapter.cancelCurrentQuery()
    }

    private fun speech() {
        loadingState()
        mSpeechButton.setIcon(R.drawable.pause_speech)
        val pages = mStory.pages(null, null)
        val currentPage = pages[mBinding.viewPager.currentItem]
        mTTs?.startSpeech(currentPage.mss.spannableString.toString())
    }

    private fun showTools(result: List<Char?>) {
        if (!isToolVisible) {
            isToolVisible = true
            mBinding.tools.bold.visibility = View.VISIBLE
            mBinding.tools.highlighter.visibility = View.VISIBLE
            mBinding.tools.underline.visibility = View.VISIBLE
            mBinding.tools.zoomIn.visibility = View.GONE
            mBinding.tools.zoomOut.visibility = View.GONE
            mBinding.tools.backgroundColor.visibility = View.GONE
        }

        mBinding.tools.highlighter.setCardBackgroundColor(
            resources.getColor(android.R.color.transparent, null)
        )
        mBinding.tools.bold.setCardBackgroundColor(
            resources.getColor(android.R.color.transparent, null)
        )
        mBinding.tools.underline.setCardBackgroundColor(
            resources.getColor(android.R.color.transparent, null)
        )


        for (i in result) {
            when (i) {
                ModifierName.HIGHLIGHTER -> mBinding.tools.highlighter.setCardBackgroundColor(
                    resources.getColor(R.color.palette_B100, null)
                )

                ModifierName.BOLD -> mBinding.tools.bold.setCardBackgroundColor(
                    resources.getColor(R.color.palette_B100, null)
                )

                ModifierName.UNDERLINE -> mBinding.tools.underline.setCardBackgroundColor(
                    resources.getColor(R.color.palette_B100, null)
                )

                else -> {}
            }
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

    // for fullScreen mode
    private fun hideAllTools() {
        mBinding.tools.root.visibility = View.GONE
    }

    private fun showAllTools() {
        mBinding.tools.root.visibility = View.VISIBLE
    }

    // SelectionListener
    override fun onSelectionListener(page: Page) {
        val result = page.mss.haveSelectionSentenceSpans()
        mSelectionPage = page
        showTools(result)
    }

    override fun onUnSelectionListener() {
        hideTools()
    }

    private fun onChangePageListener() {
        mBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mTTs?.apply {
                    if (isSpeaking) {
                        stopSpeech()
                        idleState()
                        mSpeechButton.setIcon(R.drawable.speech)
                    }
                }
            }
        })
    }

    override fun clickOnBookmark() {
        // mSelectionPage should have value
        var errorMessage = "error"
        mSelectionPage?.let {
            val pages = mStory.pages(null, null)
            errorMessage = if (it.number == 1) {
                "You can't add bookmark in title"
            } else if (pages.size == 2) {
                showBookmarkDialog(it, true)
                return
            } else {
                showBookmarkDialog(it)
                return
            }
        }
        Snackbar.make(
            mBinding.root, errorMessage, Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showBookmarkDialog(page: Page, onlyExternal: Boolean = false) {
        val pages = mStory.pages(null, null)
        BookMarkDialog(2, pages.size, page.number, onlyExternal)
            .setOnClickOnPositive { value ->
                if (value is String) {
                    // this value if external url
                    mAdapter.bookMark(page, value)
                } else if (value is Int) {
                    // this value if numberPage
                    mAdapter.bookMark(page, value - 1)       // base 0
                }
            }.show(parentFragmentManager, null)
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

    override fun idleState() {
        hideProgress()
    }

    override fun failureState(message: String) {
        hideProgress()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun successState(data: Any) {
        if (data is String) {
            // Save Modifiers successfully
            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
        } else {
            // retrieve modifiers interaction
            val list = (data as ModifierList).list
            applyRetrievedModifiers(list)
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun loadingState() {
        showProgress()
    }

    private fun applyRetrievedModifiers(list: MutableList<ModifierPage>) {
        for (i in list) {
            val pages = mStory.pages(null, null)
            pages[i.pageNumber - 1].mss.applyModifiers(
                mAdapter.getRecyclerView(),
                mNavigation,
                i.modifiers
            ) // base 0
        }
    }

    override fun onStop() {
        mTTs?.stopSpeech()
        super.onStop()
    }

    override fun onDestroy() {
        mTTs?.shutdownSpeech()
        super.onDestroy()

    }


}