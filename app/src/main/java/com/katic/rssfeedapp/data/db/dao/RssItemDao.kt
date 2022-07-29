package com.katic.rssfeedapp.data.db.dao

import androidx.room.*
import com.katic.rssfeedapp.data.model.RssItem
import java.util.*

@Dao
interface RssItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(stories: List<RssItem>)

    @Query("SELECT * FROM items WHERE channel_id = :channelId AND title LIKE :title LIMIT 1")
    fun findByChannelIdAndTitle(channelId: Long, title: String): RssItem

    @Query("UPDATE items SET read_flag = 1 WHERE channel_id = :channelId")
    fun setAllChannelStoriesAsRead(channelId: Long)

    @Query("SELECT COUNT(*) FROM items WHERE read_flag = 0")
    fun countUnreadStories(): Int

    @Query("SELECT COUNT(*) FROM items WHERE save_date > :timestamp")
    fun countNewStories(timestamp: Long): Int

    @Update
    fun update(story: RssItem): Int

    @Delete
    fun delete(story: RssItem)

    @Transaction
    suspend fun insert(channelId: Long, stories: List<RssItem>) {
        if (stories.isEmpty()) return
        val now = Date().time
        stories.forEach {
            it.channelId = channelId
            it.saved = now
        }
        insert(stories)
    }
}