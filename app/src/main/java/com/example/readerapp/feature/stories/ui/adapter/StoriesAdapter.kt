package com.example.readerapp.feature.stories.ui.adapter

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.readerapp.R
import com.example.readerapp.core.network.NetworkHandler
import com.example.readerapp.databinding.StoryBinding
import com.example.readerapp.feature.stories.data.model.Story
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@OptIn(DelicateCoroutinesApi::class)
class StoriesAdapter(
    stories: MutableList<Story>, private val storyListener: StoryListener
) : Adapter<StoriesAdapter.ViewHolder>() {

    val stories: MutableList<Story> by Delegates.observable(stories) { _, _, _ ->
        notifyItemInserted(stories.size - 1)
    }

    class ViewHolder(private val mStoryBinding: StoryBinding) :
        RecyclerView.ViewHolder(mStoryBinding.root) {
        fun bind(story: Story) {
            mStoryBinding.title.text = story.title
            GlobalScope.launch(Dispatchers.IO) {
                putCover(story.cover!!, mStoryBinding.storyCover)
            }
        }

        private suspend fun putCover(cover: String, coverView: ImageView) {
            GlobalScope.launch(Dispatchers.Main) {
                if (NetworkHandler.isNetworkAvailable(coverView.context)) {
                    Picasso.get().load(cover).error(R.drawable.cover).into(mStoryBinding.storyCover)
                } else {
                    Picasso.get().load(R.drawable.cover).into(mStoryBinding.storyCover)
                }
            }
            delay(1700)
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val bitmap = ((coverView.drawable) as BitmapDrawable).bitmap
                    Palette.from(bitmap).maximumColorCount(32).generate {
                        it?.let {
                            val vibrant = it.vibrantSwatch
                            val color = vibrant!!.rgb
                            mStoryBinding.title.setTextColor(color)
                        }
                    }
                } catch (e: Exception) {
                    val color = coverView.resources.getColor(R.color.white, null)
                    mStoryBinding.title.setTextColor(color)
                }
            }
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

