package edu.buffalo.cse.cse442f19.spotme

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Notifications {

    companion object {
        @JvmStatic
        var notificationMap: HashMap<Int, Notification> = hashMapOf()

        var CREATED_CHANNEL: Boolean = false
        var CHANNEL_ID = "spotme_all_notifs"
        var NOTIFICATION_ID: Int = 0

        fun displayNotification(message: String, activity: AppCompatActivity) {

            if (!CREATED_CHANNEL) {
                createNotificationChannel(activity);
            }

            var builder = NotificationCompat.Builder(activity, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Spot Me")
                .setContentText(message)
                //            .setStyle(NotificationCompat.BigTextStyle()
                //                .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            var notificationManagerCompat = NotificationManagerCompat.from(activity);
            var notification: Notification = builder.build()
            notificationManagerCompat.notify(NOTIFICATION_ID, notification)
            notificationMap.put(NOTIFICATION_ID, notification)
            NOTIFICATION_ID++
        }

        fun createNotificationChannel(activity: AppCompatActivity) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val name: CharSequence = "SpotMe Notifications"
                var description: String = "Includes all SpotMe notifications"
                var importance: Int = NotificationManager.IMPORTANCE_DEFAULT;

                var notifChannel: NotificationChannel =
                    NotificationChannel(CHANNEL_ID, name, importance)

                notifChannel.description = description
                val notifManager: NotificationManager =
                    activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notifManager.createNotificationChannel(notifChannel)
            }
            CREATED_CHANNEL = true
        }
    }
}