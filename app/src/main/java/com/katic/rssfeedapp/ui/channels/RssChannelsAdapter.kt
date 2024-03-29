package com.katic.rssfeedapp.ui.channels

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.katic.rssfeedapp.R
import com.katic.rssfeedapp.data.model.RssChannel
import com.katic.rssfeedapp.databinding.ItemRssChannelBinding
import com.katic.rssfeedapp.di.GlideApp

class RssChannelsAdapter(val listener: Listener) :
    RecyclerView.Adapter<RssChannelsAdapter.ViewHolder>() {

    private var channels: List<RssChannel> = emptyList()

    init {
        setHasStableIds(true)
    }

    inner class ViewHolder(private val binding: ItemRssChannelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val channel = channels[position]

            binding.apply {
                title.text = channel.title

                val styledText = HtmlCompat.fromHtml(
                    channel.description,
                    HtmlCompat.FROM_HTML_MODE_COMPACT,
                    null,
                    null
                )

                description.text = styledText

                GlideApp.with(itemView.context)
                    .load(channel.image?.url)
                    .placeholder(R.drawable.ic_rss_feed)
                    .error(R.drawable.ic_rss_feed)
                    .into(thumbnail)

                val drawable = ContextCompat.getDrawable(
                    itemView.context,
                    if (channel.favorite) R.drawable.ic_favorite else R.drawable.ic_not_favorite
                )
                favorite.setImageDrawable(drawable)
                favorite.setOnClickListener {
                    channel.favorite = !channel.favorite
                    notifyItemChanged(position)
                    listener.onAddedToFavorites(channel)
                }

                root.setOnClickListener { listener.onChannelSelected(channel) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemRssChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = channels.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemId(position: Int): Long {
        return channels[position].id ?: -1
    }

    fun swapData(rssChannels: List<RssChannel>) {
        this.channels = rssChannels
        notifyDataSetChanged()
    }

    fun getChannelOnPosition(position: Int): RssChannel? {
        if (position >= itemCount) return null
        return channels[position]
    }

    interface Listener {
        fun onChannelSelected(channel: RssChannel)

        fun onAddedToFavorites(channel: RssChannel)
    }
}