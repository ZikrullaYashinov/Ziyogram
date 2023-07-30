package zikrulla.production.uzbekchat.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.util.Util

class LoginViewModel : ViewModel() {

    private var isRun = MutableLiveData<Boolean>()
    private var database = FirebaseDatabase.getInstance()
    private var reference = database.reference

    init {
        isRun.postValue(false)
    }

    fun getIsRun(): MutableLiveData<Boolean> {
        return isRun
    }

    fun fetchIsOpposite() {
        handler()
    }

    fun addToken(user: User, token: ArrayList<String>? = null) {
        if (token == null) {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                val list = ArrayList<String>()
                list.add(it)
                reference.child(Util.F_USERS)
                    .child(user.uid!!)
                    .child(Util.F_DEVICE_TOKENS)
                    .setValue(list)
            }
        } else {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                if (it !in token) {
                    token.add(it)
                    reference.child(Util.F_USERS)
                        .child(user.uid!!)
                        .child(Util.F_DEVICE_TOKENS)
                        .setValue(token)
                }
            }
        }
    }

    private fun handler() {
        Handler(Looper.getMainLooper()).postDelayed({
            isRun.postValue(true)
        }, 1000)
    }
}