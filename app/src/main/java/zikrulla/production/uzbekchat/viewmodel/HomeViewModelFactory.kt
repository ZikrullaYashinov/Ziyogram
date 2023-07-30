package zikrulla.production.uzbekchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import zikrulla.production.uzbekchat.model.User

class HomeViewModelFactory(
    private val user: User
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(user) as T
        return throw Exception("Error")
    }
}