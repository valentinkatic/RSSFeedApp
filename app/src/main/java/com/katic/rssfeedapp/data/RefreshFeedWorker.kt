package com.katic.rssfeedapp.data

import android.content.Context
import androidx.work.*
import com.katic.rssfeedapp.appComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RefreshFeedWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val repository = applicationContext.appComponent.rssRepository
            repository.refreshFeed()
            Result.success()
        }
    }

    companion object {
        private const val TAG_REFRESH_FEED = "REFRESH_FEED"

        fun initialize(context: Context, repeatInterval: Long) {
            Timber.d("initialize: repeatInterval: $repeatInterval")
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val refreshCpnWork =
                PeriodicWorkRequest.Builder(
                    RefreshFeedWorker::class.java,
                    repeatInterval,
                    TimeUnit.MINUTES
                )
                    .setConstraints(constraints)
                    .addTag(TAG_REFRESH_FEED)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                TAG_REFRESH_FEED,
                ExistingPeriodicWorkPolicy.REPLACE,
                refreshCpnWork
            )
        }

        fun cancel(context: Context) {
            Timber.d("cancel")
            WorkManager.getInstance(context).cancelAllWorkByTag(TAG_REFRESH_FEED)
        }
    }
}