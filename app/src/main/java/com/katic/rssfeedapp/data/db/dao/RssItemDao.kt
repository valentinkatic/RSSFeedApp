package com.katic.rssfeedapp.data.db.dao

import androidx.room.*
import com.katic.rssfeedapp.data.model.RssItem

@Dao
interface RssItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(item: List<RssItem>)

    @Query("DELETE FROM items WHERE channel_id = :channelId")
    fun deleteChannelItems(channelId: Long)

    @Query("SELECT * FROM items WHERE channel_id = :channelId")
    fun findByChannelId(channelId: Long): List<RssItem>

    @Query("SELECT * FROM items WHERE channel_id = :channelId AND title LIKE :title LIMIT 1")
    fun findByChannelIdAndTitle(channelId: Long, title: String): RssItem

    @Transaction
    suspend fun insert(channelId: Long, items: List<RssItem>) {
        if (items.isEmpty()) return
        deleteChannelItems(channelId)
        items.forEach { it.channelId = channelId }
        insertAndReplace(items)
    }
}