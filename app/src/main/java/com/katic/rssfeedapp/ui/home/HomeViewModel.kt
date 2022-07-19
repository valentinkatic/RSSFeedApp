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

    private var job: Job? = null

    fun getMockFeed() {
        Timber.d("getMockFeed")

        _rssChannelResult.value = LoadingResult.loading(_rssChannelResult.value)

        val urls = listOf(
            "https://medium.com/feed/mobile-app-development-publication",
            "https://www.nasa.gov/rss/dyn/breaking_news.rss"
        )

        job?.cancel()

        job = viewModelScope.launch {
            runCatchCancel(
                run = {
                    val channels = mutableListOf<RssChannel>()
                    urls.forEach {
                        val channel = repository.getChannelFeed(it)
                        channels.add(channel)
                    }
                    _rssChannelResult.value = LoadingResult.loaded(channels)
                },
                catch = { t ->
                    Timber.e(t, "getMockFeed error")
                    _rssChannelResult.value = LoadingResult.exception(_rssChannelResult.value, t)
                },
                cancel = {
                    Timber.i("getMockFeed canceled")
                }
            )
        }
    }

    fun getFeed(url: String) {
        Timber.d("getFeed")

        job?.cancel()

        _rssChannelResult.value = LoadingResult.loading(_rssChannelResult.value)

        job = viewModelScope.launch {
            runCatchCancel(
                run = {
                    val channel = repository.getChannelFeed(url)

                    val channels: MutableList<RssChannel> =
                        (rssChannelResult.value?.data ?: emptyList()).toMutableList()
                    channels.add(channel)
                    _rssChannelResult.value = LoadingResult.loaded(channels)
                },
                catch = { t ->
                    Timber.e(t, "getFeed error")
                    _rssChannelResult.value = LoadingResult.exception(_rssChannelResult.value, t)
                },
                cancel = {
                    Timber.i("getFeed canceled")
                }
            )
        }
    }

    fun setSelectedChannel(rssChannel: RssChannel) {
        Timber.d("setSelectedChannel: $rssChannel")
        _selectedRssChannel.value = rssChannel
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}