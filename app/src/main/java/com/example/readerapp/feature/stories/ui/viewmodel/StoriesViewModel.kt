package com.example.readerapp.feature.stories.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readerapp.core.interation.UseCase
import com.example.readerapp.core.viewstate.ViewState
import com.example.readerapp.feature.stories.interation.GetStories
import com.example.readerapp.feature.stories.viewstate.Failure
import com.example.readerapp.feature.stories.viewstate.Idle
import com.example.readerapp.feature.stories.viewstate.Loading
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

/**
this is the mediator of ui and domain layer
receive the interaction from the activity (presentation layer) in interactionChannel
then doing the process of this interaction by connection with domain layer
when receive the result (viewState) send it to activity (presentation layer) by viewStateChannel
 */
class StoriesViewModel : ViewModel() {
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
                if (it is GetStories) {
                    // invoke
                    it(viewModelScope, viewStateChannel)
                } else {
                    viewStateChannel.send(Failure("Unknown interaction"))
                }
            }
        }
    }
}