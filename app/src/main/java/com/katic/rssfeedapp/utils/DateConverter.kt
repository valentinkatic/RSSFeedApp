package com.katic.rssfeedapp.utils

import com.tickaroo.tikxml.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateConverter : TypeConverter<Date> {

    companion object {
        private const val RSS_DATE_FORMAT = "E, dd MMM yyyy HH:mm:ss Z"
        private const val RSS_DATE_FORMAT_2 = "E, dd MMM yyyy HH:mm Z"
        private val SUPPORTED_RSS_DATE_FORMATS = listOf(RSS_DATE_FORMAT, RSS_DATE_FORMAT_2)
    }

    @Throws(Exception::class)
    override fun read(value: String?): Date? {
        if (value == null) {
            return null
        }
        for (pattern in SUPPORTED_RSS_DATE_FORMATS) {
            try {
                return SimpleDateFormat(pattern, Locale.US).parse(value)
            } catch (e: ParseException) {
                // try the next one
            }
        }
        return null
    }

    @Throws(Exception::class)
    override fun write(value: Date?): String? {
        val formatter = SimpleDateFormat(RSS_DATE_FORMAT, Locale.US)
        return formatter.format(value ?: return null)
    }

}

fun Date.formatRssDate(): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US)
    return dateFormat.format(this)
}