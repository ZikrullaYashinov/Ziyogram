package zikrulla.production.uzbekchat.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import zikrulla.production.uzbekchat.util.Util.BASE_URL

object ApiClient {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api by lazy {
        retrofit.create(ApiService::class.java)
    }

}