package zikrulla.production.uzbekchat.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import zikrulla.production.uzbekchat.MainActivity
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.model.Message
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.util.Util.TAG

class FirebaseService : FirebaseMessagingService() {

    var notificationId = 0
    private val CHANNEL_ID = "zikrulla.production"

    private fun getId() = ++notificationId

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken: $token")

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "onMessageReceived: ${message.notification?.title}")
        Log.d(TAG, "onMessageReceived: ${message.notification?.body}")
        Log.d(TAG, "onMessageReceived: ${message.data}")


        val fromUser = Gson().fromJson(message.data["fromUser"], User::class.java)
        val toUser = Gson().fromJson(message.data["toUser"], User::class.java)
        val _message = Gson().fromJson(message.data["message"], Message::class.java)

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("id", R.id.messageFragment)
        intent.putExtra("fromUser", fromUser)
        intent.putExtra("toUser", toUser)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(fromUser.displayName)
            .setContentText(_message.text)
            .setSmallIcon(R.drawable.ic_ziyogram)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(getId(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "ziyogram"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "Description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

}