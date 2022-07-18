package com.katic.rssfeedapp.data.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "channel")
data class RssChannel(
    @PropertyElement
    val title: String,
    @PropertyElement
    val description: String,
    @PropertyElement
    val link: String,
    @Element
    val image: RssImage?,
    @Element
    val item: List<RssItem>?
)
