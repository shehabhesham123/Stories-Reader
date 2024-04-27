package com.example.readerapp.feature.stories.ui.activity

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.readerapp.core.interation.UseCase
import com.example.readerapp.core.navigation.Navigation
import com.example.readerapp.core.platform.BaseFragment
import com.example.readerapp.core.storage.TempStorage
import com.example.readerapp.databinding.FragmentStoriesBinding
import com.example.readerapp.feature.auth.credentials.Authenticator
import com.example.readerapp.feature.stories.data.model.Story
import com.example.readerapp.feature.stories.interation.GetStories
import com.example.readerapp.feature.stories.ui.adapter.StoriesAdapter
import com.example.readerapp.feature.stories.ui.adapter.StoryListener
import com.example.readerapp.feature.stories.ui.viewmodel.StoriesViewModel
import com.example.readerapp.feature.stories.viewstate.Failure
import com.example.readerapp.feature.stories.viewstate.Idle
import com.example.readerapp.feature.stories.viewstate.Loading
import com.example.readerapp.feature.stories.viewstate.Success
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class StoriesFragment : BaseFragment(), StoryListener {

    private lateinit var mBinding: FragmentStoriesBinding
    private lateinit var mActivityResultForGetContent: ActivityResultLauncher<Intent>
    private lateinit var mStoriesViewModel: StoriesViewModel
    private val mNavigation by lazy { Navigation(Authenticator.instance()) }
    private val stories: MutableList<Story> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeActivityResultLauncher()
        mStoriesViewModel = ViewModelProvider(this)[StoriesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStoriesBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeRecyclerView()
        render()

        // listeners
        mBinding.selectStories.setOnClickListener {
            startContentIntent()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun sendAction(interaction: UseCase) {
        GlobalScope.launch(Dispatchers.IO) {
            mStoriesViewModel.interactionChannel.send(interaction)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun render() {
        GlobalScope.launch(Dispatchers.Main) {
            mStoriesViewModel.viewStateChannel.consumeAsFlow().buffer().collect {
                hideProgress()
                when (it) {
                    is Idle -> idleState()
                    is Failure -> failureState(it.message)
                    is Success -> successState(it.data)
                    is Loading -> loadingState()
                }
            }
        }
    }

    override fun idleState() {
        updateUI(true)
    }

    override fun failureState(message: String) {
        Snackbar.make(mBinding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun successState(data: Any) {
        updateUI(false)
        // i don't make notify because i use observable in stories variable in adapter , so it notify auto
        stories.add(data as Story)
    }

    override fun loadingState() {
        showProgress()
    }

    private fun updateUI(isIdle: Boolean) {
        mBinding.noStories.visibility = if (isIdle) View.VISIBLE else View.GONE
        mBinding.recyclerView.visibility = if (isIdle) View.GONE else View.VISIBLE
    }

    private fun initializeActivityResultLauncher() {
        mActivityResultForGetContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val filesUri = getFilesUri(it.data)
                    val interaction = GetStories(requireContext(), filesUri)
                    sendAction(interaction)
                }
            }
    }

    private fun getFilesUri(intent: Intent?): MutableList<Uri> {
        val filesUri = mutableListOf<Uri>()
        // if intent?.clipData not equal null so the user select more than one story
        // else the user select only one story
        intent?.clipData?.apply {
            if (itemCount != 0) {
                for (i in 0..<itemCount) {
                    filesUri.add(getItemAt(i).uri)
                }
            }
        }
        // if filesUri.isEmpty()  ---> that's mean that intent?.clipData is null, and the user select one story
        if (filesUri.isEmpty() && intent?.data != null) filesUri.add(intent.data!!)
        return filesUri
    }

    private fun initializeRecyclerView() {
        mBinding.recyclerView.hasFixedSize()
        mBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        mBinding.recyclerView.adapter = StoriesAdapter(stories, this)
    }

    private fun startContentIntent() {
        mActivityResultForGetContent.launch(mNavigation.getContentIntent())
    }

    private fun goToStoryDetailsActivity() {
        mNavigation.showStoryDetails(requireContext())
    }

    // StoryListener
    override fun onClickListener(story: Story) {
        // store the story in tempStorage to can retrieve it in another activity
        TempStorage.instance().currentStory = story
        goToStoryDetailsActivity()
    }

}