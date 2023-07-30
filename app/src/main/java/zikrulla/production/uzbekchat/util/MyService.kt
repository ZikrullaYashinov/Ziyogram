package zikrulla.production.uzbekchat.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.RemoteInput
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.model.Message
import zikrulla.production.uzbekchat.model.User

class MyService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action?.equals("ACTION_REPLY") == true) {
            val resultsFromIntent = RemoteInput.getResultsFromIntent(intent)
            val answer = resultsFromIntent.getCharSequence("key")
            Log.d("@@@@", "onStartCommand: $answer")
            val notificationId = intent.getIntExtra("notificationId", 1)
            val fromUser = intent.getSerializableExtra("user") as User
            val message = intent.getSerializableExtra("message") as Message
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(applicationContext, "channelId")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("Replied")
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = fromUser.displayName
                val descriptionText = message.text
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel =
                    NotificationChannel(getString(R.string.app_name), name, importance).apply {
                        description = descriptionText
                    }
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(notificationId, notification)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}