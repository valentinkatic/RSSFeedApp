package com.katic.rssfeedapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katic.rssfeedapp.data.RssRepository
import com.katic.rssfeedapp.data.model.RssChannel
import com.katic.rssfeedapp.utils.LoadingResult
import com.katic.rssfeedapp.utils.runCatchCancel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val repository: RssRepository) : ViewModel() {

    val rssChannelResult: LiveData<LoadingResult<List<RssChannel>>> get() = _rssChannelResult
    private val _rssChannelResult = MutableLiveData<LoadingResult<List<RssChannel>>>()

    val selectedRssChannel: LiveData<RssChannel> get() = _selectedRssChannel
    private val _selectedRssChannel = MutableLiveData<RssChannel>()

    private var removedChannel: RssChannel? = null

    private var job: Job? = null

    fun fetchDummyRssFeed() {
        Timber.d("fetchDummyRssFeed")

        _rssChannelResult.value = LoadingResult.loading(_rssChannelResult.value)

        val urls = listOf(
            "https://medium.com/feed/mobile-app-development-publication",
            "https://www.nasa.gov/rss/dyn/breaking_news.rss",
            "https://rss.art19.com/apology-line"
        )

        job?.cancel()

        job = viewModelScope.launch {
            runCatchCancel(
                run = {
                    val channels = mutableListOf<RssChannel>()
                    for (url in urls) {
                        val channel = repository.getChannelFeed(url)
                        channels.add(channel)
                    }
                    _rssChannelResult.value = LoadingResult.loaded(channels)
                },
                catch = { t ->
                    Timber.e(t, "fetchDummyRssFeed error")
                    _rssChannelResult.value = LoadingResult.exception(_rssChannelResult.value, t)
                },
                cancel = {
                    Timber.i("fetchDummyRssFeed canceled")
                }
            )
        }
    }

    fun getRssFeed(url: String) {
        Timber.d("getRssFeed")

        job?.cancel()

        _rssChannelResult.value = LoadingResult.loading(_rssChannelResult.value)

        job = viewModelScope.launch {
            runCatchCancel(
                run = {
                    val channel = repository.getChannelFeed(url)
                    val channels = addRssChannel(channel)
                    _rssChannelResult.value = LoadingResult.loaded(channels)
                },
                catch = { t ->
                    Timber.e(t, "getRssFeed error")
                    _rssChannelResult.value = LoadingResult.exception(_rssChannelResult.value, t)
                },
                cancel = {
                    Timber.i("getRssFeed canceled")
                }
            )
        }
    }

    fun setSelectedRssChannel(rssChannel: RssChannel) {
        Timber.d("setSelectedRssChannel: ${rssChannel.link}")
        _selectedRssChannel.value = rssChannel
    }

    private fun addRssChannel(rssChannel: RssChannel): List<RssChannel> {
        Timber.d("addRssChannel: ${rssChannel.link}")
        val channels = (rssChannelResult.value?.data ?: emptyList()).toMutableList()
        channels.add(rssChannel)
        return channels
    }

    fun removeRssChannel(index: Int) {
        Timber.d("removeRssChannel: $index")
        val channels = (rssChannelResult.value?.data ?: emptyList()).toMutableList()
        if (channels.size <= index) return
        removedChannel = channels[index]
        channels.removeAt(index)
        _rssChannelResult.value = LoadingResult.loaded(channels)
    }

    fun undoRemove(index: Int) {
        Timber.d("undoRemove: $index")
        val channels = (rssChannelResult.value?.data ?: emptyList()).toMutableList()
        if (channels.size < index || removedChannel == null) return
        channels.add(index, removedChannel!!)
        _rssChannelResult.value = LoadingResult.loaded(channels)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}