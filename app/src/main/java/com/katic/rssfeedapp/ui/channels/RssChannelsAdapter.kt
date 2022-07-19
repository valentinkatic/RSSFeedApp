package com.katic.rssfeedapp.ui.channels

import android.view.LayoutInflater
import android.view.ViewGroup
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
                description.text = channel.description

                GlideApp.with(itemView.context)
                    .load(channel.image?.url)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(thumbnail)

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

    override fun getItemViewType(position: Int): Int {
        return channels[position].link.hashCode()
    }

    fun swapData(rssChannels: List<RssChannel>) {
        this.channels = rssChannels
        notifyDataSetChanged()
    }

    interface Listener {
        fun onChannelSelected(channel: RssChannel)
    }
}