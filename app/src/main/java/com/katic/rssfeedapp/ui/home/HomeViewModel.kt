package com.katic.rssfeedapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katic.rssfeedapp.data.RssRepository
import com.katic.rssfeedapp.data.model.RssChannel
import com.katic.rssfeedapp.data.model.RssChannelAndStories
import com.katic.rssfeedapp.utils.LoadingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val repository: RssRepository) : ViewModel() {

    val rssChannelResult: LiveData<LoadingResult<List<RssChannel>>> get() = repository.rssChannelResult

    val favoriteRssChannelResult: LiveData<List<RssChannel>>
        get() = Transformations.map(rssChannelResult) {
            it.data?.filter { channel -> channel.favorite } ?: emptyList()
        }

    private var removedChannel: RssChannelAndStories? = null

    private var job: Job? = null

    fun fetchDummyRssFeed() {
        Timber.d("fetchDummyRssFeed")

        val urls = arrayOf(
            "https://medium.com/feed/mobile-app-development-publication",
            "https://www.nasa.gov/rss/dyn/breaking_news.rss",
            "https://rss.art19.com/apology-line"
        )

        job?.cancel()

        job = viewModelScope.launch(Dispatchers.IO) {
            repository.getChannelFeed(*urls)
        }
    }

    fun getRssFeed(url: String) {
        Timber.d("getRssFeed")

        job?.cancel()

        job = viewModelScope.launch {
            repository.getChannelFeed(url)
        }
    }

    fun removeRssChannel(channel: RssChannel) {
        Timber.d("removeRssChannel: $channel")
        viewModelScope.launch(Dispatchers.IO) {
            removedChannel = repository.getChannelAndStories(channel.id!!)
            repository.removeChannel(channel)
        }
    }

    fun undoRemove() {
        Timber.d("undoRemove: $removedChannel")
        if (removedChannel == null) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertChannel(removedChannel!!)
            removedChannel = null
        }
    }

    fun addToFavorites(channel: RssChannel) {
        Timber.d("addToFavorites: $channel")
        if (channel.id == null) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.setChannelFavorite(channel.id!!, channel.favorite)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}