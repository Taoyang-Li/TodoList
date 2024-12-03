package com.example.todolist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Check for a valid context and intent
        if (context == null || intent == null) return

        // Retrieve the task title from the intent
        val todoTitle = intent.getStringExtra("TODO_TITLE") ?: "Task Reminder"

        // Notification channel ID
        val channelId = "TODO_CHANNEL_ID"

        // Ensure the notification channel is created (for Android 8.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelName = "Todo Reminder Notifications"
            val channelDescription = "Channel for Todo reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            // Register the channel with the system
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Replace with your app's notification icon
            .setContentTitle("Task Reminder")
            .setContentText("It's time for: $todoTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Automatically dismiss notification when clicked

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(todoTitle.hashCode(), notificationBuilder.build())
        }
    }
}
