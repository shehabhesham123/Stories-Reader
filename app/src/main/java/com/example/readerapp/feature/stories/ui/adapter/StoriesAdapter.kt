package com.example.readerapp.feature.stories.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.readerapp.databinding.StoryBinding
import com.example.readerapp.feature.stories.data.model.Story
import kotlin.properties.Delegates

class StoriesAdapter(
    stories: MutableList<Story>,
    private val storyListener: StoryListener
) :
    Adapter<StoriesAdapter.ViewHolder>() {

    val stories: MutableList<Story> by Delegates.observable(stories) { _, _, _ ->
        notifyItemInserted(stories.size - 1)
    }

    class ViewHolder(private val mStoryBinding: StoryBinding) :
        RecyclerView.ViewHolder(mStoryBinding.root) {
        fun bind(story: Story) {
            mStoryBinding.title.text = story.title
            //mStoryBinding.storyCover
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val storyBinding = StoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(storyBinding)
    }

    override fun getItemCount(): Int {
        return stories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
        holder.itemView.setOnClickListener {
            storyListener.onClickListener(story)
        }
    }
}

