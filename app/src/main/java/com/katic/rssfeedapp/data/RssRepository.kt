package com.katic.rssfeedapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.katic.rssfeedapp.data.db.RssDatabase
import com.katic.rssfeedapp.data.model.RssChannel
import com.katic.rssfeedapp.data.model.RssChannelAndItems
import com.katic.rssfeedapp.utils.LoadingResult
import com.katic.rssfeedapp.utils.runCatchCancel
import timber.log.Timber

class RssRepository(private val service: RssService, private val rssDatabase: RssDatabase) {

    val rssChannelResult: LiveData<LoadingResult<List<RssChannel>>> get() = _rssChannelResult
    private val _rssChannelResult = MutableLiveData<LoadingResult<List<RssChannel>>>()

    suspend fun getChannelFeed(vararg url: String) {
        Timber.d("getChannelFeed: ${url.joinToString()}")
        // notify listener loading is in progress
        _rssChannelResult.postValue(LoadingResult.loading(_rssChannelResult.value))

        runCatchCancel(
            run = {
                url.forEach { url ->
                    var channel = service.getFeed(url).channel
                    channel = rssDatabase.rssChannelDao().insertOrUpdate(channel)
                    channel.item?.also { items ->
                        rssDatabase.rssItemDao().insert(channel.id!!, items)
                    }
                }

                // notify listeners loading is done
                _rssChannelResult.postValue(
                    LoadingResult.loaded(rssDatabase.rssChannelDao().getAll())
                )
            },
            catch = { t ->
                Timber.e(t, "getChannelFeed error")
                _rssChannelResult.postValue(LoadingResult.exception(_rssChannelResult.value, t))
            },
            cancel = {
                Timber.i("getChannelFeed canceled")
            }
        )
    }

    suspend fun insertChannel(rssChannelAndItems: RssChannelAndItems) {
        Timber.d("insertChannel: $rssChannelAndItems")

        rssDatabase.rssChannelDao().insert(rssChannelAndItems.channel)
        rssDatabase.rssItemDao().insert(rssChannelAndItems.channel.id!!, rssChannelAndItems.items)

        // notify listeners
        _rssChannelResult.postValue(
            LoadingResult.loaded(rssDatabase.rssChannelDao().getAll())
        )
    }

    suspend fun removeChannel(rssChannel: RssChannel) {
        Timber.d("removeChannel: $rssChannel")
        rssDatabase.rssChannelDao().delete(rssChannel)
        _rssChannelResult.postValue(
            LoadingResult.loaded(rssDatabase.rssChannelDao().getAll())
        )
    }

    suspend fun setChannelFavorite(channelId: Long, favorite: Boolean) =
        rssDatabase.rssChannelDao().setFavorite(channelId, favorite)

    suspend fun getChannelAndItems(channelId: Long): RssChannelAndItems =
        rssDatabase.rssChannelDao().getChannelWithItems(channelId)

}