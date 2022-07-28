package com.katic.rssfeedapp.ui.stories

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.katic.rssfeedapp.data.model.RssItem
import com.katic.rssfeedapp.databinding.ItemStoryBinding
import com.katic.rssfeedapp.utils.UiUtils

class StoriesAdapter(val listener: Listener) : RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    private var stories: List<RssItem> = emptyList()

    init {
        setHasStableIds(true)
    }

    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val story = stories[position]

            val typeface = if (story.read) Typeface.NORMAL else Typeface.BOLD

            binding.apply {
                title.apply {
                    text = story.title
                    setTypeface(null, typeface)
                }

                published.apply {
                    text = UiUtils.formatPublishedDate(published.context, story.published)
                    setTypeface(null, typeface)
                }

                root.setOnClickListener { listener.onStorySelected(story) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemId(position: Int): Long {
        return stories[position].title.hashCode().toLong()
    }

    fun swapData(rssChannels: List<RssItem>) {
        this.stories = rssChannels
        notifyDataSetChanged()
    }

    fun getItemOnPosition(position: Int): RssItem? {
        if (position >= itemCount) return null
        return stories[position]
    }

    interface Listener {
        fun onStorySelected(rssItem: RssItem)
    }
}