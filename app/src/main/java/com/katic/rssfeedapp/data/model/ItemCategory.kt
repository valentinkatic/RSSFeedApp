package com.katic.rssfeedapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml


@Xml(name = "category")
@Entity(tableName = "categories")
data class ItemCategory(
    @Attribute
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    val itemId: Int?,
    @TextContent
    var content: String
)