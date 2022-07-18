package com.katic.rssfeedapp.data.model

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "image")
data class RssImage(
    @PropertyElement
    val url: String,
    @PropertyElement
    val title: String,
    @PropertyElement
    val link: String
)
