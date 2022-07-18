package com.katic.rssfeedapp.di

import android.content.Context
import com.katic.rssfeedapp.data.RssRepository
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance applicationContext: Context
        ): AppComponent
    }

    val okHttpClient: OkHttpClient

    val rssRepository: RssRepository

}