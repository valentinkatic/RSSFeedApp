package com.katic.rssfeedapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.katic.rssfeedapp.data.db.RssDatabase
import com.katic.rssfeedapp.data.model.RssChannel
import com.katic.rssfeedapp.data.model.RssChannelAndStories
import com.katic.rssfeedapp.data.model.RssItem
import com.katic.rssfeedapp.notifications.NotificationHandler
import com.katic.rssfeedapp.utils.LoadingResult
import com.katic.rssfeedapp.utils.runCatchCancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber
import java.util.*

class RssRepository(
    private val service: RssService,
    private val database: RssDatabase,
    private val preferences: AppPreferences,
    private val notificationHandler: NotificationHandler
) {

    val rssChannelResult: LiveData<LoadingResult<List<RssChannel>>> get() = _rssChannelResult
    private val _rssChannelResult = MutableLiveData<LoadingResult<List<RssChannel>>>()

    /**
     * Observable (event bus) for monitoring stories changes.
     */
    val storiesChanges: SharedFlow<Long> get() = _storiesChanges
    private val _storiesChanges = MutableSharedFlow<Long>(0)

    suspend fun getChannelFeed(vararg url: String) {
        Timber.d("getChannelFeed: ${url.joinToString()}")
        // notify listener loading is in progress
        _rssChannelResult.postValue(LoadingResult.loading(_rssChannelResult.value))

        runCatchCancel(
            run = {
                url.forEach { url ->
                    var channel = service.getFeed(url).channel
                    channel.sourceUrl = url
                    channel = database.rssChannelDao().insertOrUpdate(channel)
                    channel.item?.also { items ->
                        database.rssItemDao().insert(channel.id!!, items)
                    }
                }

                // notify listeners loading is done
                _rssChannelResult.postValue(
                    LoadingResult.loaded(database.rssChannelDao().getAll())
                )
                countNewAndUnreadStories()
                preferences.refreshTimestamp = Date().time
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

    suspend fun refreshFeed(showNotificationAfter: Boolean = true) {
        Timber.d("refreshFeed")
        // notify listener loading is in progress
        _rssChannelResult.postValue(LoadingResult.loading(_rssChannelResult.value))

        runCatchCancel(
            run = {
                val urls = database.rssChannelDao().getAll().map { it.sourceUrl!! }.toList()
                urls.forEach { url ->
                    var channel = service.getFeed(url).channel
                    channel.sourceUrl = url
                    channel = database.rssChannelDao().insertOrUpdate(channel)
                    channel.item?.also { items ->
                        database.rssItemDao().insert(channel.id!!, items)
                    }
                }

                // notify listeners loading is done
                _rssChannelResult.postValue(
                    LoadingResult.loaded(database.rssChannelDao().getAll())
                )
                if (showNotificationAfter) {
                    countNewAndUnreadStories()
                }
                preferences.refreshTimestamp = Date().time
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

    private fun countNewAndUnreadStories() {
        Timber.d("countNewAndUnreadStories")
        database.rssItemDao().also {
            val unread = it.countUnreadStories()
            val new = it.countNewStories(preferences.refreshTimestamp)
            if (unread > 0) {
                notificationHandler.generateNotificationAndShowIt(unread, new)
            }
        }
    }

    suspend fun insertChannel(rssChannelAndStories: RssChannelAndStories) {
        Timber.d("insertChannel: $rssChannelAndStories")

        database.rssChannelDao().insert(rssChannelAndStories.channel)
        database.rssItemDao()
            .insert(rssChannelAndStories.channel.id!!, rssChannelAndStories.stories)

        // notify listeners
        _rssChannelResult.postValue(
            LoadingResult.loaded(database.rssChannelDao().getAll())
        )
    }

    suspend fun removeChannel(rssChannel: RssChannel) {
        Timber.d("removeChannel: $rssChannel")
        database.rssChannelDao().delete(rssChannel)
        _rssChannelResult.postValue(
            LoadingResult.loaded(database.rssChannelDao().getAll())
        )
    }

    suspend fun setChannelFavorite(channelId: Long, favorite: Boolean) =
        database.rssChannelDao().setFavorite(channelId, favorite)

    suspend fun getChannelAndStories(channelId: Long): RssChannelAndStories =
        database.rssChannelDao().getChannelWithStories(channelId)

    suspend fun getStory(channelId: Long, storyTitle: String): RssItem =
        database.rssItemDao().findByChannelIdAndTitle(channelId, storyTitle)

    suspend fun removeStory(story: RssItem) {
        Timber.d("removeStory: $story")
        database.rssItemDao().delete(story)
        _storiesChanges.emit(story.channelId!!)
    }

    suspend fun insertStory(story: RssItem) {
        Timber.d("insertStory: $story")
        database.rssItemDao().insert(story.channelId!!, listOf(story))
        _storiesChanges.emit(story.channelId!!)
    }

    suspend fun updateStory(story: RssItem) {
        Timber.d("updateStory: $story")
        database.rssItemDao().update(story)
        _storiesChanges.emit(story.channelId!!)
    }

    suspend fun setAllChannelStoriesAsRead(channelId: Long) {
        database.rssItemDao().setAllChannelStoriesAsRead(channelId)
        _storiesChanges.emit(channelId)
    }

}