package zikrulla.production.uzbekchat.model.notification

import java.io.Serializable

data class NotificationRequest(
    val data: NotificationData,
    val to: String
) : Serializable
