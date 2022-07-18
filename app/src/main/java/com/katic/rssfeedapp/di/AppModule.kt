package com.katic.rssfeedapp.di

import com.katic.rssfeedapp.data.RssRepository
import com.katic.rssfeedapp.data.RssService
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()

        // log all network calls
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Timber.tag("http")
            Timber.v(message)
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addNetworkInterceptor(loggingInterceptor)

        // return
        return clientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideTikXml(): TikXml {
        return TikXml.Builder()
            .exceptionOnUnreadXml(false)
            .build()
    }

    @Provides
    fun provideRssService(
        client: OkHttpClient,
        tikXml: TikXml
    ): RssService {
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("http://localhost/")
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
            .build()
        return retrofit.create(RssService::class.java)
    }

    @Provides
    fun provideRssRepository(rssService: RssService) =
        RssRepository(rssService)

}