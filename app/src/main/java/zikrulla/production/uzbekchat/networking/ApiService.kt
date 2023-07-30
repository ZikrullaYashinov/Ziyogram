package zikrulla.production.uzbekchat.networking

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import zikrulla.production.uzbekchat.model.notification.NotificationRequest
import zikrulla.production.uzbekchat.util.Util
import zikrulla.production.uzbekchat.util.Util.CONTENT_TYPE

interface ApiService {
    @Headers("Authorization: key=${Util.SERVER_KEY}", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    fun postNotification(
        @Body notificationRequest: NotificationRequest
    ): Call<NotificationRequest>
}