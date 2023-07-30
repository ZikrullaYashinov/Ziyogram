package zikrulla.production.uzbekchat.ui

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
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
import zikrulla.production.uzbekchat.viewmodel.HomeViewModel
import zikrulla.production.uzbekchat.viewmodel.HomeViewModelFactory
import zikrulla.production.uzbekchat.viewmodel.Resource
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
        user = User(name, uid, email, url, time)
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

    private fun notification(fromUser: User, message: Message) {
        val EXTRA_TEXT = "ACTION_REPLY"
        val notificationId = viewModel.getNotificationId()

        val intent = Intent(activity, MyService::class.java)
        intent.action = EXTRA_TEXT
        intent.putExtra("notificationId", notificationId)
        intent.putExtra("user", fromUser)
        intent.putExtra("message", message)

        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val remoteInput = RemoteInput.Builder(EXTRA_TEXT)
            .setLabel("Xabar")
            .build()

        val action = NotificationCompat.Action.Builder(
            R.drawable.ic_send_black,
            "Javob Yozish",
            pendingIntent
        ).addRemoteInput(remoteInput).build()

        val notification = NotificationCompat.Builder(requireContext(), "channelId")
            .setSmallIcon(R.drawable.ic_ziyogram)
            .setContentTitle(fromUser.displayName)
            .setContentText(message.text)
            .setContentIntent(pendingIntent)
            .addAction(action)
            .build()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = fromUser.displayName
            val descriptionText = message.text
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(getString(R.string.app_name), name, importance).apply {
                    description = descriptionText
                }
            manager.createNotificationChannel(channel)
        }
        manager.notify(notificationId, notification)
    }

    fun notificationFirebase(fromUser: User, message: Message) {

    }

    fun notificationCancel(id: Int) {
        if (id == -1)
            manager.cancelAll()
        else
            manager.cancel(id)
    }

    private fun readShP() {
        val isLogin = sharedPreference.getBoolean(Util.SHP_IS_SIGIN, false)
        if (!isLogin)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_homeFragment_to_loginFragment)
        else {
            loadStart()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

}