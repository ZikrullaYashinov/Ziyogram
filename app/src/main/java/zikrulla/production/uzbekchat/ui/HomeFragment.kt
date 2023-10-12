package zikrulla.production.uzbekchat.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.adapter.UserChatListAdapter
import zikrulla.production.uzbekchat.databinding.FragmentHomeBinding
import zikrulla.production.uzbekchat.model.MenuItem
import zikrulla.production.uzbekchat.model.Message
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.model.UserChat
import zikrulla.production.uzbekchat.util.CustomDialog
import zikrulla.production.uzbekchat.util.MyService
import zikrulla.production.uzbekchat.util.Util
import zikrulla.production.uzbekchat.util.Util.F_LAST_ONLINE
import zikrulla.production.uzbekchat.util.Util.F_USERS
import zikrulla.production.uzbekchat.util.Util.STATUS_ONLINE
import zikrulla.production.uzbekchat.viewmodel.HomeViewModel
import zikrulla.production.uzbekchat.viewmodel.HomeViewModelFactory
import zikrulla.production.uzbekchat.viewmodel.Resource
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment(), CoroutineScope {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var manager: NotificationManager
    private lateinit var viewModel: HomeViewModel
    private val adapter by lazy { UserChatListAdapter() }
    private var user: User? = null
    private val TAG = "@@@@"

    private val onlineValueEventListener = object : ValueEventListener {
        @SuppressLint("SimpleDateFormat")
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                setOnlineStatus(snapshot.value.toString())
                reference.child(F_USERS)
                    .child(user?.uid!!)
                    .child(F_LAST_ONLINE)
                    .onDisconnect()
                    .setValue(SimpleDateFormat("dd MMMM HH:mm").format(System.currentTimeMillis()))
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }
    }

    private fun setOnlineStatus(status: String) {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        loadCreate()
        click()
        changeUsers()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        readShP()
    }

    override fun onResume() {
        super.onResume()
        reference.child(F_USERS)
            .child(user?.uid!!)
            .child(F_LAST_ONLINE)
            .addValueEventListener(onlineValueEventListener)
        reference.child(F_USERS)
            .child(user?.uid!!)
            .child(F_LAST_ONLINE)
            .setValue(STATUS_ONLINE)
    }

    override fun onDestroy() {
        reference.child(F_USERS)
            .child(user?.uid!!)
            .child(F_LAST_ONLINE)
            .removeEventListener(onlineValueEventListener)
        super.onDestroy()
    }

    private fun loadCreate() {
        sharedPreference = activity?.getSharedPreferences(Util.SHP_LOGIN, Context.MODE_PRIVATE)!!
        loadStart()
        viewModel = ViewModelProvider(this, HomeViewModelFactory(user!!))[HomeViewModel::class.java]
        manager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        database = FirebaseDatabase.getInstance()
        reference = database.reference

        adapter.itemClick = {
            val bundle = Bundle()
            bundle.putSerializable(Util.ARG_USER, it.user)
            bundle.putSerializable(Util.ARG_USER_I, user)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_homeFragment_to_messageFragment, bundle)
        }
        adapter.itemLongClick = {
            showMessagePopupMenu(it)
        }

        binding.recyclerView.adapter = adapter
    }

    private fun showMessagePopupMenu(userChat: UserChat) {
        CustomDialog().showCustomDialog(requireContext(), R.layout.dialog_message, listOf(
            MenuItem(R.id.delete, true) {
                viewModel.delete(userChat)
            }, MenuItem(R.id.edit, false) {}, MenuItem(R.id.copy, false) {}
        ))
    }

    private fun loadStart() {
        val name = sharedPreference.getString(Util.SHP_NAME, "")
        val email = sharedPreference.getString(Util.SHP_EMAIL, "")
        val url = sharedPreference.getString(Util.SHP_IMAGE_URL, "")
        val uid = sharedPreference.getString(Util.SHP_UID, "")
        val time = Date().time
        user = User(name, uid, email, url, time, null)
    }

    private fun click() {
        binding.apply {
            setting.setOnClickListener {
                val bundle = Bundle()
                bundle.putBoolean(Util.ARG_USER_EDIT, true)
                bundle.putSerializable(Util.ARG_USER, user)
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_homeFragment_to_settingsFragment, bundle)
            }
            search.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable(Util.ARG_USER, user)
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_homeFragment_to_searchFragment, bundle)
            }
        }
    }

    private fun changeUsers() {
        launch {
            viewModel.getUsers().collect {
                when (it) {
                    is Resource.Error -> {
                        Log.d(TAG, "changeUsers: Error")
                    }

                    is Resource.Loading -> {
                        Log.d(TAG, "changeUsers: Loading")
                    }

                    is Resource.Success -> {
                        Log.d(TAG, "changeUsers: Success")
                        adapter.submitList(it.data)
                    }
                }
            }
        }
    }

    private fun readShP() {
        val isLogin = sharedPreference.getBoolean(Util.SHP_IS_SIGIN, false)
        if (!isLogin)
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        else {
            loadStart()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

}