package zikrulla.production.uzbekchat

import android.app.Application
import com.onesignal.OneSignal

class App: Application() {

    private val ONESIGNAL_APP_ID = "49c68d51-e12e-44ef-be9b-d303c110f217"

    override fun onCreate() {
        super.onCreate()
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}