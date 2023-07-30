package zikrulla.production.uzbekchat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import zikrulla.production.uzbekchat.model.appinfo.AppInfo
import zikrulla.production.uzbekchat.util.Util

class ProductionViewModel : ViewModel() {

    private val stateFlow = MutableStateFlow<Resource<AppInfo>>(Resource.Loading())
    private val reference = FirebaseDatabase.getInstance().reference

    init {
        fetchData()
    }

    private fun fetchData() {
        reference.child(Util.F_APP)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModelScope.launch(Dispatchers.IO) {
                        try{
                            val appInfo = snapshot.getValue(AppInfo::class.java)
                            stateFlow.emit(Resource.Success(appInfo!!))
                        }catch (e:Exception){
                            Log.e("@@@@", "onDataChange: ${e.message}")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    viewModelScope.launch(Dispatchers.IO) {
                        stateFlow.emit(Resource.Error(error.message))
                    }
                }
            })
    }

    fun getData(): MutableStateFlow<Resource<AppInfo>> {
        return stateFlow
    }
}