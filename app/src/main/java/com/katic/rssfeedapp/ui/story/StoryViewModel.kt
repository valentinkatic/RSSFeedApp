package com.katic.rssfeedapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katic.rssfeedapp.data.RssRepository
import com.katic.rssfeedapp.data.model.RssItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoryViewModel @Inject constructor(
    private val repository: RssRepository,
    private val channelId: Long,
    private val storyTitle: String
) : ViewModel() {

    val storyResult: LiveData<RssItem> get() = _storyResult
    private val _storyResult = MutableLiveData<RssItem>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val story = repository.getStory(channelId, storyTitle)
            story.read = true
            repository.updateStory(story)
            _storyResult.postValue(story)
        }
    }

}
