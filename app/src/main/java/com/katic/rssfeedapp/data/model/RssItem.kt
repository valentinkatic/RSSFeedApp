package com.katic.rssfeedapp.data.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import java.util.*

@Xml(name = "item")
data class RssItem(
    @PropertyElement
    val title: String,
    @PropertyElement
    val description: String,
    @PropertyElement
    val link: String?,
    @PropertyElement(name = "pubDate")
    val published: Date?,
    @Element
    val category: List<ItemCategory>?
)
