package com.katic.rssfeedapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.katic.rssfeedapp.data.db.dao.RssChannelDao
import com.katic.rssfeedapp.data.db.dao.RssItemDao
import com.katic.rssfeedapp.data.model.RssChannel
import com.katic.rssfeedapp.data.model.RssItem

@Database(entities = [RssChannel::class, RssItem::class], version = 1)
abstract class RssDatabase : RoomDatabase() {
    abstract fun rssChannelDao(): RssChannelDao

    abstract fun rssItemDao(): RssItemDao
}
