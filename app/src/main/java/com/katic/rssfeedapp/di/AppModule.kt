package com.katic.rssfeedapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.katic.rssfeedapp.data.AppPreferences
import com.katic.rssfeedapp.data.RssRepository
import com.katic.rssfeedapp.data.RssService
import com.katic.rssfeedapp.data.db.RssDatabase
import com.katic.rssfeedapp.notifications.NotificationHandler
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
    @Singleton
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
    @Singleton
    fun provideRssDatabase(applicationContext: Context): RssDatabase =
        Room.databaseBuilder(
            applicationContext,
            RssDatabase::class.java,
            "rssfeed"
        ).build()

    @Provides
    @Singleton
    fun provideRssRepository(
        rssService: RssService,
        rssDatabase: RssDatabase,
        appPreferences: AppPreferences,
        notificationHandler: NotificationHandler
    ) = RssRepository(rssService, rssDatabase, appPreferences, notificationHandler)

    @Provides
    @Singleton
    fun provideAppPreferences(applicationContext: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(applicationContext)

    @Provides
    @Singleton
    fun provideNotificationHandler(applicationContext: Context): NotificationHandler =
        NotificationHandler(applicationContext)

}