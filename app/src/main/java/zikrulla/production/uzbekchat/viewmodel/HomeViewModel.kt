package zikrulla.production.uzbekchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.model.UserChat
import zikrulla.production.uzbekchat.util.Util

class HomeViewModel(private val user: User) : ViewModel() {

    var id = 0

    private val reference = FirebaseDatabase.getInstance().reference
    private val stateFlow = MutableStateFlow<Resource<List<UserChat>>>(Resource.Loading())
    private val stateUidFlow = MutableStateFlow<Resource<List<UserChat>>>(Resource.Loading())

    init {
        fetchUsersUid()
        fetchUsers()
    }

    fun getUsers(): MutableStateFlow<Resource<List<UserChat>>> {
        return stateFlow
    }

    private fun getUsersUid(): MutableStateFlow<Resource<List<UserChat>>> {
        return stateUidFlow
    }

    fun getNotificationId(): Int {
        return ++id
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            getUsersUid().collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        stateFlow.emit(Resource.Error(resource.e))
                    }

                    is Resource.Loading -> {
                        stateFlow.emit(Resource.Loading())
                    }

                    is Resource.Success -> {
                        reference.child(Util.F_USERS)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val list = ArrayList<UserChat>()
                                    resource.data.forEach {
                                        val u = snapshot.child(it.user?.uid!!)
                                            .getValue(User::class.java) as User
                                        list.add(UserChat(u, it.newMessage))
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
        }
    }

    private fun fetchUsersUid() {
        reference.child(Util.F_USERS)
            .child(user.uid!!)
            .child(Util.F_MESSAGES)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val listUserChat = ArrayList<UserChat>()
                        snapshot.children.forEach {
                            listUserChat.add(
                                UserChat(
                                    user = User(null, it.key, null, null, null, null),
                                    isNewMessage = it.child(Util.F_NEW_MESSAGE).value as Boolean
                                )
                            )
                        }
                        stateUidFlow.emit(Resource.Success(listUserChat))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    viewModelScope.launch {
                        stateUidFlow.emit(Resource.Error(error.message))
                    }
                }
            })
    }

    fun delete(userChat: UserChat) {
        reference.child(Util.F_USERS)
            .child(user.uid!!)
            .child(Util.F_MESSAGES)
            .child(userChat.user?.uid!!)
            .removeValue()
    }


}