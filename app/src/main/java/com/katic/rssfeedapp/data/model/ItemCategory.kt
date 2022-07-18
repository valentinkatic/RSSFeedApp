package com.katic.rssfeedapp.data.model

import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml


@Xml(name = "category")
data class ItemCategory(
    @TextContent
    var content: String
)