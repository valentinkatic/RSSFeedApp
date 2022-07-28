package com.katic.rssfeedapp.data.db.dao

import androidx.room.*
import com.katic.rssfeedapp.data.model.RssItem

@Dao
interface RssItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(stories: List<RssItem>)

    @Query("SELECT * FROM items WHERE channel_id = :channelId AND title LIKE :title LIMIT 1")
    fun findByChannelIdAndTitle(channelId: Long, title: String): RssItem

    @Query("UPDATE items SET read = 1 WHERE channel_id = :channelId")
    fun setAllChannelStoriesAsRead(channelId: Long)

    @Update
    fun update(story: RssItem): Int

    @Delete
    fun delete(story: RssItem)

    @Transaction
    suspend fun insert(channelId: Long, stories: List<RssItem>) {
        if (stories.isEmpty()) return
        stories.forEach { it.channelId = channelId }
        insert(stories)
    }
}