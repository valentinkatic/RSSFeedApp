package com.katic.rssfeedapp.data

import com.katic.rssfeedapp.data.model.RssFeed
import retrofit2.http.GET
import retrofit2.http.Url

interface RssService {

    @GET
    suspend fun getFeed(@Url url: String): RssFeed

}