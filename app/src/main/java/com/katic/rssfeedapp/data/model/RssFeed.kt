package com.katic.rssfeedapp.data.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "rss")
data class RssFeed(
    @Element
    val channel: RssChannel
)
