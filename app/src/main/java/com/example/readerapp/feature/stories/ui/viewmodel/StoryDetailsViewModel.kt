package com.example.readerapp.feature.stories.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readerapp.core.interation.UseCase
import com.example.readerapp.core.viewstate.ViewState
import com.example.readerapp.feature.stories.interation.RetrieveModifiers
import com.example.readerapp.feature.stories.interation.SaveModifiers
import com.example.readerapp.feature.stories.viewstate.Failure
import com.example.readerapp.feature.stories.viewstate.Idle
import com.example.readerapp.feature.stories.viewstate.Loading
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class StoryDetailsViewModel : ViewModel() {
    // channel to receive the use case
    var interactionChannel: Channel<UseCase> = Channel(Channel.UNLIMITED)
        private set

    // channel to send the ViewState
    // i use channel instead of stateFlow because i wants the state to be repeated
    // because i get each story individually
    // so i send success state every time
    // and if i use the stateFlow i can't send the same state every time
    var viewStateChannel: Channel<ViewState> = Channel(Channel.UNLIMITED)
        private set

    init {
        viewModelScope.launch {
            // initial state
            viewStateChannel.send(Idle)
        }
        // to receive the interactions from activity
        progress()
    }

    private fun progress() {
        viewModelScope.launch {
            interactionChannel.consumeAsFlow().buffer().collect {
                viewStateChannel.send(Loading)
                if (it is SaveModifiers || it is RetrieveModifiers) {
                    viewStateChannel.send(Loading)
                    // invoke
                    it(viewModelScope, viewStateChannel)
                } else {
                    viewStateChannel.send(Failure("Unknown interaction"))
                }
            }
        }
    }
}