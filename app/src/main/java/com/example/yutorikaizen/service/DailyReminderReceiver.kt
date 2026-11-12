package com.example.yutorikaizen.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.yutorikaizen.MainActivity
import com.example.yutorikaizen.R

class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            DailyReminderScheduler.ACTION_MORNING_REMINDER -> {
                showNotification(
                    context = context,
                    notificationId = 1001,
                    title = "Morning Anchor",
                    message = "It is time to fill in your morning anchor."
                )
                // Re-arm for tomorrow at 7:00 AM
                DailyReminderScheduler.scheduleMorningReminder(context)
            }
            DailyReminderScheduler.ACTION_EVENING_REMINDER -> {
                showNotification(
                    context = context,
                    notificationId = 1002,
                    title = "Evening Reflection",
                    message = "It is time to reflect and prepare for tomorrow."
                )
                // Re-arm for tomorrow at 9:00 PM
                DailyReminderScheduler.scheduleEveningReminder(context)
            }
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                DailyReminderScheduler.scheduleDailyReminders(context)
            }
        }
    }

    private fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String
    ) {
        DailyReminderScheduler.createNotificationChannel(context)

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_NAVIGATE_TO, "daily_goal")
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(context, notificationId, contentIntent, flags)

        val notification = NotificationCompat.Builder(context, DailyReminderScheduler.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Notifications blocked or permission denied
        }
    }
}
