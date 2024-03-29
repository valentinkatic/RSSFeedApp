package com.katic.rssfeedapp.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class RssChannelAndStories(
    @Embedded val channel: RssChannel,
    @Relation(
        parentColumn = "id",
        entityColumn = "channel_id"
    )
    val stories: List<RssItem>
)