package com.katic.rssfeedapp.ui.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katic.rssfeedapp.data.RssRepository
import com.katic.rssfeedapp.data.model.RssChannelAndStories
import com.katic.rssfeedapp.data.model.RssItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class StoriesViewModel @Inject constructor(
    private val repository: RssRepository,
    val channelId: Long
) : ViewModel() {

    val rssChannelAndStoriesResult: LiveData<RssChannelAndStories> get() = _rssChannelAndStoriesResult
    private val _rssChannelAndStoriesResult = MutableLiveData<RssChannelAndStories>()

    private var selectedFilter: Filter = Filter.ALL

    private var removedStory: RssItem? = null

    // monitor stories changes for [channelId] and update list
    init {
        repository.storiesChanges.filter { it == channelId }.onEach {
            Timber.d("storiesChanges: $it")
            fetchStories()
        }.launchIn(viewModelScope)
    }

    init {
        fetchStories()
    }

    private fun fetchStories() {
        Timber.d("fetchStories")
        viewModelScope.launch(Dispatchers.IO) {
            val channelAndStories = repository.getChannelAndStories(channelId)
            val stories = when (selectedFilter) {
                Filter.ALL -> channelAndStories.stories
                Filter.READ -> channelAndStories.stories.filter { it.read }
                Filter.UNREAD -> channelAndStories.stories.filter { !it.read }
            }.toMutableList().also { it.sort() }
            _rssChannelAndStoriesResult.postValue(
                RssChannelAndStories(
                    channelAndStories.channel,
                    stories
                )
            )
        }
    }

    fun removeStory(story: RssItem) {
        Timber.d("removeStory: $story")
        viewModelScope.launch(Dispatchers.IO) {
            removedStory = story
            repository.removeStory(story)
        }
    }

    fun undoRemove() {
        Timber.d("undoRemove: $removedStory")
        if (removedStory == null) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertStory(removedStory!!)
            removedStory = null
        }
    }

    fun markAllStoriesRead() {
        Timber.d("markAllStoriesRead")
        viewModelScope.launch(Dispatchers.IO) {
            repository.setAllChannelStoriesAsRead(channelId)
        }
    }

    fun setFilter(filter: Filter) {
        selectedFilter = filter
        fetchStories()
    }

    enum class Filter { ALL, READ, UNREAD }

}