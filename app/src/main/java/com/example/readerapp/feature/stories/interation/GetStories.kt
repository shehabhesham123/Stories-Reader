package com.example.readerapp.feature.stories.interation

import android.content.Context
import android.net.Uri
import com.example.readerapp.core.extension.toPojo
import com.example.readerapp.core.interation.UseCase
import com.example.readerapp.core.stream.LocalStream
import com.example.readerapp.core.viewstate.ViewState
import com.example.readerapp.feature.stories.data.model.Story
import com.example.readerapp.feature.stories.viewstate.Failure
import com.example.readerapp.feature.stories.viewstate.Success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * getStories interaction
 */
class GetStories(
    private val context: Context,       //  use context to pass it to readFile
    private val stories: MutableList<Uri>,      //  use stories to pass it to readFile
) : UseCase() {

    // the process of this action
    override suspend fun run(scope: CoroutineScope, channel: Channel<ViewState>) {
        getStories(scope, channel)
    }

    /*
        i use kotlin coroutines to get stories in background and concurrency
        and i get each story individually
        so if there is a lot of stories, the user don't need to wast time for waiting to get all stories
        because the downloaded story is displayed without waiting for the rest of the stories
    */
    private fun getStories(scope: CoroutineScope, channel: Channel<ViewState>) {
        scope.launch {
            val localStream = LocalStream.instance()
            // to get each story individually
            for (i in stories) {
                val content = scope.async { localStream.readFileContent(context, i) }
                // this line don't execute until the content variable have value  (async ,await)
                val viewState = getViewState(content.await())
                channel.send(viewState)
            }
        }
    }

    private fun getViewState(result: Any?): ViewState {
        // if result is null, so may be the file not json or some thing occur when reading
        return if (result == null) Failure("No Data in this file")
        else getPojo(result)
    }

    private fun getPojo(result: Any?): ViewState {
        val jsonString = result as String
        val story = String.toPojo(jsonString, Story::class.java)
        return Success(story)
    }


}