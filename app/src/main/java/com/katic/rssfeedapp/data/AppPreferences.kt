package com.katic.rssfeedapp.data

import android.content.Context
import android.content.SharedPreferences
import com.katic.rssfeedapp.R
import javax.inject.Inject

class AppPreferences @Inject constructor(
    context: Context,
    private val prefs: SharedPreferences
) {
    private val keyEnableFeedRefresh: String =
        context.getString(R.string.pref_key_enable_feed_refresh)
    private val keyFeedRefreshPeriod: String =
        context.getString(R.string.pref_key_feed_refresh_period)
    private val keyRefreshTimestamp: String = context.getString(R.string.pref_key_refresh_timestamp)

    val feedRefreshEnabled: Boolean
        get() = prefs.getBoolean(keyEnableFeedRefresh, true)

    val feedRefreshPeriod: Long
        get() {
            val period = prefs.getString(keyFeedRefreshPeriod, null)
            return period?.toLongOrNull() ?: 15
        }

    var refreshTimestamp: Long
        get() = prefs.getLong(keyRefreshTimestamp, -1)
        set(value) = prefs.edit().putLong(keyRefreshTimestamp, value).apply()

}