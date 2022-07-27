package com.katic.rssfeedapp.ui.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katic.rssfeedapp.data.RssRepository
import com.katic.rssfeedapp.data.model.RssChannelAndItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RssItemsViewModel @Inject constructor(
    private val repository: RssRepository,
    val channelId: Long
) : ViewModel() {

    val rssChannelAndItemsResult: LiveData<RssChannelAndItems> get() = _rssChannelAndItemsResult
    private val _rssChannelAndItemsResult = MutableLiveData<RssChannelAndItems>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _rssChannelAndItemsResult.postValue(
                repository.getChannelAndItems(channelId)
            )
        }
    }
}