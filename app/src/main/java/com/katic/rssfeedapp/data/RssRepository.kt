package com.katic.rssfeedapp.data

import com.katic.rssfeedapp.data.model.RssChannel


class RssRepository(private val service: RssService) {

    suspend fun getChannelFeed(url: String): RssChannel = service.getFeed(url).channel

}