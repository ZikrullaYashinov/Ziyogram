package zikrulla.production.uzbekchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.networking.notification.ApiService

class MessageViewModelFactory(
    private val user: User,
    private val userI: User,
    private val apiService: ApiService
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessagesViewModel::class.java))
            return MessagesViewModel(user, userI, apiService) as T
        return throw Exception("Error")
    }
}