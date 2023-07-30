package zikrulla.production.uzbekchat.util

import android.content.Context
import android.util.Log
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal

class NotificationService: OneSignal.OSRemoteNotificationReceivedHandler {
    override fun remoteNotificationReceived(p0: Context?, p1: OSNotificationReceivedEvent?) {
        val notification = p1?.notification

        Log.d("@@@@", "remoteNotificationReceived: ${notification?.title} ${notification?.body}")

    }
}