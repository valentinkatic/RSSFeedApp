package com.katic.rssfeedapp.data.model

import androidx.room.*
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "channel")
@Entity(tableName = "channels", indices = [Index("link", unique = true), Index("favorite")])
class RssChannel {
    @PrimaryKey(autoGenerate = true)
    @Attribute
    var id: Long? = null

    @PropertyElement
    lateinit var title: String

    @PropertyElement
    lateinit var description: String

    @PropertyElement
    lateinit var link: String

    @Element
    @Embedded
    var image: RssImage? = null

    @Element
    @Ignore
    var item: List<RssItem>? = null

    @Attribute
    var favorite: Boolean = false

    @Attribute
    @ColumnInfo(name = "source_url")
    var sourceUrl: String? = null

    override fun toString(): String {
        return "RssChannel(id=$id, title='$title', description='$description', link='$link', image=$image, item=${item?.size ?: 0}, favorite=$favorite)"
    }

}
