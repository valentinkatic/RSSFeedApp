package com.katic.rssfeedapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.katic.rssfeedapp.utils.DateConverter
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "item")
@Entity(
    tableName = "items", foreignKeys = [ForeignKey(
        entity = RssChannel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("channel_id"),
        onDelete = ForeignKey.CASCADE
    )],
    primaryKeys = ["title", "description"],
    indices = [Index("channel_id")]
)
data class RssItem(
    @Attribute
    @ColumnInfo(name = "channel_id")
    var channelId: Long? = null,

    @PropertyElement
    val title: String,

    @PropertyElement
    val description: String,

    @PropertyElement
    val link: String?,

    @PropertyElement(name = "pubDate", converter = DateConverter::class)
    @ColumnInfo(name = "pub_date")
    val published: Long?,

    @Attribute
    @ColumnInfo(name = "save_date")
    var saved: Long?,

    @Attribute
    @ColumnInfo(name = "read_flag")
    var read: Boolean = false
) : Comparable<RssItem> {

    override fun compareTo(other: RssItem): Int {
        return ((other.published ?: 0) - (this.published ?: 0)).toInt()
    }

    override fun toString(): String {
        return "RssItem(channelId=$channelId, title='$title', description='${description.length}', link=$link, published=$published, read: $read)"
    }
}
