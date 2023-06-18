package zikrulla.production.uzbekchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import zikrulla.production.uzbekchat.model.User

class UsersViewModel : ViewModel() {

    var users = MutableLiveData<ArrayList<User>>()

    fun getUsers(): LiveData<ArrayList<User>> {
        return users
    }

    fun fetchUsers(userList: ArrayList<User>) {
        users.postValue(userList)
    }
}