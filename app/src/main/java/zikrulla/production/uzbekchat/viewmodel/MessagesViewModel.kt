package zikrulla.production.uzbekchat.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.R
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zikrulla.production.uzbekchat.model.Message
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.model.notification.NotificationData
import zikrulla.production.uzbekchat.model.notification.NotificationRequest
import zikrulla.production.uzbekchat.model.notification.NotificationResponse
import zikrulla.production.uzbekchat.networking.notification.ApiService
import zikrulla.production.uzbekchat.repository.MessageRepository
import zikrulla.production.uzbekchat.util.Util
import zikrulla.production.uzbekchat.util.Util.TAG

class MessagesViewModel(
    private val user: User,
    private val userI: User,
    private val apiService: ApiService
) : ViewModel() {

    private val reference = FirebaseDatabase.getInstance().reference
    private val repository = MessageRepository(reference, user, userI)
    private val stateFlow = MutableStateFlow<Resource<List<Message>>>(Resource.Loading())
    var isAdapterDeleteEdit = -1
    var message: Message? = null

    init {
        readMessages()
    }

    fun getMessagesFlow(): MutableStateFlow<Resource<List<Message>>> {
        return stateFlow
    }

    private suspend fun fetchMessageFlow(messages: ArrayList<Message>?, e: String? = null) {
        if (e == null)
            stateFlow.emit(Resource.Success(messages!!))
        else
            stateFlow.emit(Resource.Error(e))
    }

    private fun readMessages() {
        reference.child(Util.F_USERS)
            .child(userI.uid!!)
            .child(Util.F_MESSAGES)
            .child(user.uid!!)
            .child(Util.F_MESSAGES)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModelScope.launch {
                        val messages = ArrayList<Message>()
                        snapshot.children.forEach {
                            messages.add(it.getValue(Message::class.java)!!)
                        }
                        fetchMessageFlow(messages)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    viewModelScope.launch {
                        fetchMessageFlow(null, error.message)
                    }
                }
            })
    }

    fun seeMessage(it: List<Message>) {
        for (m in it) {
            if (m.isRead == 1 && m.fromUser == user.uid) {
                stateMessage(m, false, 2)
            }
        }
        isNewMessage(isNewMessage = false, i = true)
    }

    private fun isNewMessage(isNewMessage: Boolean, i: Boolean) {
        if (i)
            reference.child(Util.F_USERS)
                .child(userI.uid!!)
                .child(Util.F_MESSAGES)
                .child(user.uid!!)
                .child(Util.F_NEW_MESSAGE)
                .setValue(isNewMessage)
        else
            reference.child(Util.F_USERS)
                .child(user.uid!!)
                .child(Util.F_MESSAGES)
                .child(userI.uid!!)
                .child(Util.F_NEW_MESSAGE)
                .setValue(isNewMessage)
    }

    private fun writeMessage(message: Message, i: Boolean) {
        if (i)
            reference.child(Util.F_USERS)
                .child(userI.uid!!)
                .child(Util.F_MESSAGES)
                .child(user.uid!!)
                .child(Util.F_MESSAGES)
                .child(message.messageId ?: "")
                .setValue(message).addOnSuccessListener {
                    viewModelScope.launch(Dispatchers.IO) {
                        stateMessage(message, true)
                    }
                }
        else {
            message.isRead = 1
            reference.child(Util.F_USERS)
                .child(user.uid!!)
                .child(Util.F_MESSAGES)
                .child(userI.uid!!)
                .child(Util.F_MESSAGES)
                .child(message.messageId ?: "")
                .setValue(message)
        }
    }

    private fun stateMessage(message: Message, i: Boolean, isRead: Int = 1) {
        message.isRead = isRead
        if (i)
            reference.child(Util.F_USERS)
                .child(userI.uid!!)
                .child(Util.F_MESSAGES)
                .child(user.uid!!)
                .child(Util.F_MESSAGES)
                .child(message.messageId!!)
                .setValue(message)
        else
            reference.child(Util.F_USERS)
                .child(user.uid!!)
                .child(Util.F_MESSAGES)
                .child(userI.uid!!)
                .child(Util.F_MESSAGES)
                .child(message.messageId!!)
                .setValue(message)
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            async(Dispatchers.IO) { isNewMessage(isNewMessage = false, i = true) }
            async(Dispatchers.IO) { writeMessage(message, true) }
            async(Dispatchers.IO) { writeMessage(message, false) }
            async(Dispatchers.IO) { isNewMessage(isNewMessage = true, i = false) }
            async(Dispatchers.IO) { sendNotification(message) }
        }
    }

    private fun sendNotification(message: Message) {
        user.deviceTokens?.forEach {
            Log.d(TAG, "sendNotification: $message")

            apiService.postNotification(
                NotificationRequest(
                    NotificationData(message, userI, user),
                    it
                )
            ).enqueue(object :Callback<NotificationResponse>{
                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Success: ${response.body()?.success}")
                    }
                }

                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "onFailure: ${t.message}")
                }
            })
//            Log.d(TAG, "sendNotification: $it $postNotification")
        }
    }

    fun deleteMessage(message: Message, isAll: Boolean) {
        reference.child(Util.F_USERS)
            .child(userI.uid!!)
            .child(Util.F_MESSAGES)
            .child(user.uid!!)
            .child(Util.F_MESSAGES)
            .child(message.messageId ?: "")
            .removeValue()
        if (isAll)
            reference.child(Util.F_USERS)
                .child(user.uid!!)
                .child(Util.F_MESSAGES)
                .child(userI.uid!!)
                .child(Util.F_MESSAGES)
                .child(message.messageId ?: "")
                .removeValue()
    }

    fun editMessage(message: Message) {
        reference.child(Util.F_USERS)
            .child(userI.uid!!)
            .child(Util.F_MESSAGES)
            .child(user.uid!!)
            .child(Util.F_MESSAGES)
            .child(message.messageId ?: "")
            .setValue(message)
        reference.child(Util.F_USERS)
            .child(user.uid!!)
            .child(Util.F_MESSAGES)
            .child(userI.uid!!)
            .child(Util.F_MESSAGES)
            .child(message.messageId ?: "")
            .setValue(message)

    }
}