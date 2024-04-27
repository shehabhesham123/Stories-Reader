package com.example.readerapp.core.storage

import com.example.readerapp.feature.stories.data.model.Story

/**
 * allow me to store any datatype when use startActivity() and retrieve it from the second activity
 */
class TempStorage private constructor() {

    // here we initialize the datatype that we want to store it

    var currentStory: Story? = null

    companion object {
        private var tempStorage = TempStorage()
        fun instance() = tempStorage
    }
}