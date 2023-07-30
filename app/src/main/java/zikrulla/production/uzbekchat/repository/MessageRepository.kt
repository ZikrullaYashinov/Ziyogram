package zikrulla.production.uzbekchat.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zikrulla.production.uzbekchat.model.Message
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.util.Util

class MessageRepository(
    private val reference: DatabaseReference,
    private val user: User,
    private val userI: User
) {




//        NotificationRequest(
//            Util.TOPIC,
//            NotificationData(message.text ?: "", "Yangi Xabar")
//        ).also {
////            Log.d(TAG, "sendMessage: 1")
//            sendNotification(it)
////            Log.d(TAG, "sendMessage: 2")
//        }


}