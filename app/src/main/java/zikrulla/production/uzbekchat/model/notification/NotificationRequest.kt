package zikrulla.production.uzbekchat.model.notification

data class NotificationRequest(
    val to: String,
    val data: NotificationData
)
