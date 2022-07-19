package com.katic.rssfeedapp.ui.items

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.katic.rssfeedapp.data.model.RssItem
import com.katic.rssfeedapp.databinding.ItemRssItemBinding
import com.katic.rssfeedapp.utils.ImageGetter
import com.katic.rssfeedapp.utils.UiUtils
import com.katic.rssfeedapp.utils.formatRssDate

class RssItemsAdapter(val listener: Listener) :
    RecyclerView.Adapter<RssItemsAdapter.ViewHolder>() {

    private var rssItems: List<RssItem> = emptyList()

    init {
        setHasStableIds(true)
    }

    inner class ViewHolder(private val binding: ItemRssItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val rssItem = rssItems[position]

            binding.apply {
                title.text = rssItem.title

                val imageGetter = ImageGetter(root.context, description)
                // Using Html framework to parse html
                val styledText = HtmlCompat.fromHtml(
                    rssItem.description,
                    HtmlCompat.FROM_HTML_MODE_COMPACT,
                    imageGetter,
                    null
                )
                // setting the text after formatting html and downloading and setting images
                description.text = styledText

                published.text = UiUtils.formatPublishedDate(published.context, rssItem.published)

                root.setOnClickListener { listener.onItemSelected(rssItem) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemRssItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = rssItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return rssItems[position].link.hashCode()
    }

    fun swapData(rssChannels: List<RssItem>) {
        this.rssItems = rssChannels
        notifyDataSetChanged()
    }

    interface Listener {
        fun onItemSelected(rssItem: RssItem)
    }
}