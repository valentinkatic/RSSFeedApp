package com.katic.rssfeedapp.data.db.dao

import androidx.room.*
import com.katic.rssfeedapp.data.model.RssChannel
import com.katic.rssfeedapp.data.model.RssChannelAndItems

@Dao
interface RssChannelDao {
    @Query("SELECT * FROM channels")
    fun getAll(): List<RssChannel>

    @Query("SELECT * FROM channels WHERE favorite = 1")
    fun loadAllFavorite(): List<RssChannel>

    @Query("SELECT * FROM channels WHERE link LIKE :link LIMIT 1")
    fun findByLink(link: String): RssChannel?

    @Query("UPDATE channels SET favorite = :favorite WHERE id = :channelId")
    fun setFavorite(channelId: Long, favorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(channel: RssChannel): Long

    @Update
    fun update(channel: RssChannel): Int

    @Delete
    fun delete(channel: RssChannel)

    @Transaction
    suspend fun insertOrUpdate(channel: RssChannel): RssChannel {
        val id = insert(channel)
        channel.id = id
        if (id == -1L) {
            val existing = findByLink(channel.link)!!
            channel.id = existing.id
            channel.favorite = existing.favorite
            update(channel)
        }
        return channel
    }

    @Transaction
    @Query("SELECT * FROM channels WHERE id = :channelId LIMIT 1")
    fun getChannelWithItems(channelId: Long): RssChannelAndItems

}