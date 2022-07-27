package com.katic.rssfeedapp.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class RssChannelAndItems(
    @Embedded val channel: RssChannel,
    @Relation(
        parentColumn = "id",
        entityColumn = "channel_id"
    )
    val items: List<RssItem>
)