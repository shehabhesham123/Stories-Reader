package com.example.readerapp.feature.stories.ui.adapter

import com.example.readerapp.feature.stories.data.model.Story

interface StoryListener {
    fun onClickListener(story: Story)
}