package zikrulla.production.uzbekchat.networking.notification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import zikrulla.production.uzbekchat.model.notification.NotificationRequest
import zikrulla.production.uzbekchat.model.notification.NotificationResponse
import zikrulla.production.uzbekchat.util.Util
import zikrulla.production.uzbekchat.util.Util.CONTENT_TYPE

interface ApiService {
    @Headers(
        "Content-type:$CONTENT_TYPE",
        "Authorization:key=${Util.SERVER_KEY}"
    )
    @POST("fcm/send")
    fun postNotification(
        @Body notificationRequest: NotificationRequest
    ): Call<NotificationResponse>
}