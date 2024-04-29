package com.example.readerapp.feature.stories.interation

import android.content.Context
import com.example.readerapp.core.extension.toJson
import com.example.readerapp.core.interation.UseCase
import com.example.readerapp.core.stream.LocalStream
import com.example.readerapp.core.viewstate.ViewState
import com.example.readerapp.feature.stories.data.model.ModifierList
import com.example.readerapp.feature.stories.viewstate.Failure
import com.example.readerapp.feature.stories.viewstate.Success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class SaveModifiers(
    private val context: Context,
    private val modifierList: ModifierList,
    private val storyId: String
) : UseCase() {
    override suspend fun run(scope: CoroutineScope, channel: Channel<ViewState>) {
        scope.launch {
            val localStream = LocalStream.instance()
            val jsonString = String.toJson(modifierList)
            val folderPath = context.getExternalFilesDir(null)
            val file = localStream.getFile("$storyId.json", folderPath)
            val isSaved = scope.async { localStream.writeFile(file, jsonString, false) }
            channel.send(getViewState(isSaved.await() != null))
        }
    }

    private fun getViewState(isSaved: Boolean): ViewState {
        return if (!isSaved) Failure("No Data in this file")
        else Success("Success")
    }
}