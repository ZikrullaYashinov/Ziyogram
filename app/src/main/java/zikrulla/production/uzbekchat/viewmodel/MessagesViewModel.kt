package zikrulla.production.uzbekchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import zikrulla.production.uzbekchat.model.Message

class MessagesViewModel : ViewModel() {
    var messages = MutableLiveData<ArrayList<Message>>()

    fun fetchMessages(messageList: ArrayList<Message>) {
        messages.postValue(messageList)
    }

    fun getMessages(): LiveData<ArrayList<Message>> {
        return messages
    }
}