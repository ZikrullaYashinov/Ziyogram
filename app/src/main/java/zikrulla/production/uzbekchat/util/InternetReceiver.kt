package zikrulla.production.uzbekchat.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast

class InternetReceiver : BroadcastReceiver() {

    private var isConnect = true

    override fun onReceive(context: Context, intent: Intent) {
        if (isNetworkConnected(context)) {
            if (!isConnect) {
                isConnect = true
                Toast.makeText(context, "Internetga ulangan", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (isConnect) {
                isConnect = false
                Toast.makeText(context, "Internetga ulanmagan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}