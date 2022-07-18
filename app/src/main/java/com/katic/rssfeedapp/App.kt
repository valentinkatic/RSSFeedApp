package com.katic.rssfeedapp

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.katic.rssfeedapp.di.AppComponent
import com.katic.rssfeedapp.di.DaggerAppComponent
import timber.log.Timber

val Context.appComponent get() = (applicationContext as App).appComponent
val Fragment.appComponent get() = context!!.appComponent

class App : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}