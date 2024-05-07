package com.example.readerapp.feature.stories.interation

import android.content.Context
import com.example.readerapp.core.extension.toPojo
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

class RetrieveModifiers(private val context: Context, private val storyId: String) : UseCase() {
    override suspend fun run(scope: CoroutineScope, channel: Channel<ViewState>) {
        scope.launch {
            val localStream = LocalStream.instance()
            val folderPath = context.getExternalFilesDir(null)     // private file
            val file = localStream.getFile("$storyId.json", folderPath)
            if (file != null && file.exists()) {
                val content = scope.async { localStream.readFileContent(file) }
                channel.send(getViewState(content.await()))
            }
        }
    }

    private fun getViewState(result: Any?): ViewState {
        return if (result == null) Failure("No Data in this file")
        else getPojo(result)
    }

    private fun getPojo(result: Any?): ViewState {
        val jsonString = result as String
        val modifierList = String.toPojo(jsonString, ModifierList::class.java)
        return Success(modifierList)
    }
}