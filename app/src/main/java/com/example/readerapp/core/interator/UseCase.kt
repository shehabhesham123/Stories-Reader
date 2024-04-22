package com.example.readerapp.core.interator

import com.example.readerapp.core.viewstate.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * this class is the super class of all interaction
 */
abstract class UseCase {
    abstract suspend fun run(scope: CoroutineScope, stateFlow: MutableStateFlow<ViewState>)

    operator fun invoke(scope: CoroutineScope, stateFlow: MutableStateFlow<ViewState>) {
        scope.launch {
            run(scope, stateFlow)
        }
    }
}