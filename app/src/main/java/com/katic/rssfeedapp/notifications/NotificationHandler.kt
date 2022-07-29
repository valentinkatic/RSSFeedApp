package com.katic.rssfeedapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.katic.rssfeedapp.R
import com.katic.rssfeedapp.ui.home.HomeActivity
import com.katic.rssfeedapp.utils.UiUtils
import timber.log.Timber

class NotificationHandler(private val applicationContext: Context) {

    companion object {
        private const val NOTIFICATIONS_CHANNEL_ID = "NOTIFICATIONS_CHANNEL_ID"
        private const val GROUP_STORY_NOTIFICATIONS = "GROUP_STORY_NOTIFICATIONS"
        private const val STORY_NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    private val UPDATE_PENDING_INTENT_FLAGS
        get(): Int {
            var flags = PendingIntent.FLAG_UPDATE_CURRENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags = PendingIntent.FLAG_IMMUTABLE or flags
            }
            return flags
        }

    /**
     * Generate Android notification with info about unread and new stories
     */
    fun generateNotificationAndShowIt(unreadStories: Int, newStories: Int) {
        Timber.d("generateNotificationAndShowIt: unreadStories: $unreadStories, newStories: $newStories")

        val intent = Intent()
            .setComponent(
                ComponentName(
                    applicationContext.packageName,
                    "${applicationContext.packageName}.Launcher"
                )
            )
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .putExtra(HomeActivity.EXTRA_FROM_NOTIFICATION, true)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            1,
            intent,
            UPDATE_PENDING_INTENT_FLAGS
        )

        val message =
            UiUtils.formatNotificationMessage(applicationContext, unreadStories, newStories)

        val builder =
            NotificationCompat.Builder(applicationContext, NOTIFICATIONS_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(applicationContext, R.color.rss_feed_color))
                .setContentTitle(applicationContext.getString(R.string.app_name))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(android.app.Notification.DEFAULT_ALL)
                .setGroup(GROUP_STORY_NOTIFICATIONS)
                .setContentIntent(pendingIntent)

        val notification = builder.build()
        NotificationManagerCompat.from(applicationContext)
            .notify(STORY_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.notifications_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATIONS_CHANNEL_ID, name, importance)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
