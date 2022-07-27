package com.katic.rssfeedapp.data.model

import androidx.room.ColumnInfo
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "image")
data class RssImage(
    @PropertyElement
    @ColumnInfo(name = "image_url")
    val url: String,
    @PropertyElement
    @ColumnInfo(name = "image_title")
    val title: String,
    @PropertyElement
    @ColumnInfo(name = "image_link")
    val link: String
)
