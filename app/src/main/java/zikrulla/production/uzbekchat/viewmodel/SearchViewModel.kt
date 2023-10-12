package zikrulla.production.uzbekchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.util.Util

class SearchViewModel : ViewModel() {

    private val reference = FirebaseDatabase.getInstance().reference
    private val stateFlow = MutableStateFlow<Resource<List<User>>>(Resource.Loading())
    private val stateUidFlow = MutableStateFlow<Resource<List<User>>>(Resource.Loading())

    init {
        fetchUsers()
    }

    fun getUsers(): MutableStateFlow<Resource<List<User>>> {
        return stateFlow
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            reference.child(Util.F_USERS)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = ArrayList<User>()
                        snapshot.children.forEach {
                            list.add(
                                User(null, it.key, null, null, null, null)
                            )
                        }
                        for (it in list.indices) {
                            val u = snapshot.child(list[it].uid!!)
                                .getValue(User::class.java) as User
                            list[it] = u
                        }
                        viewModelScope.launch {
                            stateFlow.emit(Resource.Success(list))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }
}