package com.example.readerapp.core.interation

import com.example.readerapp.core.viewstate.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * this class is the super class of all interaction
 */
abstract class UseCase {

    // what the interaction do (process)
    abstract suspend fun run(scope: CoroutineScope, channel: Channel<ViewState>)

    // invoke the interaction to execute the process
    operator fun invoke(scope: CoroutineScope, channel: Channel<ViewState>) {
        scope.launch {
            run(scope, channel)
        }
    }
}