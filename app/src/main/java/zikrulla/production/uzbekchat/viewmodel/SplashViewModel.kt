package zikrulla.production.uzbekchat.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel() {

    private var isRun = MutableLiveData<Boolean>()

    init {
        isRun.postValue(false)
        handler()
    }

    fun getIsRun(): MutableLiveData<Boolean> {
        return isRun
    }

    private fun handler() {
        Handler(Looper.getMainLooper()).postDelayed({
            isRun.postValue(true)
        }, 2000)
    }
}