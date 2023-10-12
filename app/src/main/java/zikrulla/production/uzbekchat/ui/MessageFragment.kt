package zikrulla.production.uzbekchat.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.adapter.MessageListAdapter
import zikrulla.production.uzbekchat.databinding.FragmentMessageBinding
import zikrulla.production.uzbekchat.model.MenuItem
import zikrulla.production.uzbekchat.model.Message
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.networking.notification.ApiClient
import zikrulla.production.uzbekchat.util.CustomDialog
import zikrulla.production.uzbekchat.util.Util
import zikrulla.production.uzbekchat.viewmodel.MessageViewModelFactory
import zikrulla.production.uzbekchat.viewmodel.MessagesViewModel
import zikrulla.production.uzbekchat.viewmodel.Resource
import java.util.Date
import kotlin.coroutines.CoroutineContext

class MessageFragment : Fragment(), CoroutineScope {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            user = requireArguments().getSerializable(Util.ARG_USER) as User
            userI = requireArguments().getSerializable(Util.ARG_USER_I) as User
        }
    }

    private lateinit var binding: FragmentMessageBinding
    private lateinit var viewModel: MessagesViewModel
    private lateinit var reference: DatabaseReference
    private lateinit var user: User
    private lateinit var userI: User
    private val adapter by lazy { MessageListAdapter(userI) }
    private val TAG = "@@@@"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)

        load()
        click()
        change()

        return binding.root
    }

    private fun load() {
        viewModel = ViewModelProvider(
            this,
            MessageViewModelFactory(user, userI, ApiClient.api)
        )[MessagesViewModel::class.java]
        reference = FirebaseDatabase.getInstance().reference

        binding.apply {
            recyclerView.adapter = adapter
            name.text = user.displayName
            Glide.with(requireContext()).load(user.photoUrl).centerCrop().into(image)
            reference.child(Util.F_USERS)
                .child(user.uid!!)
                .child(Util.F_LAST_ONLINE).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val s = snapshot.value.toString()
                        if (s != "null") {
                            status.isVisible = true
                            status.text = s
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(Util.TAG, "onCancelled: ${error.message}")
                    }
                })
        }

    }

    private fun click() {
        binding.apply {
            image.setOnClickListener { messageToSettingsFragment() }
            back.setOnClickListener { Navigation.findNavController(binding.root).popBackStack() }
            name.setOnClickListener { messageToSettingsFragment() }
            send.setOnClickListener {
                val text = message.text.toString().trim()
                if (text.isNotEmpty()) {
                    if (linearLayout1.isVisible) {
                        val m =
                            Message(
                                viewModel.message?.messageId,
                                viewModel.message?.fromUser,
                                viewModel.message?.toUser,
                                text,
                                viewModel.message?.time,
                                viewModel.message?.isRead,
                                true
                            )
                        if (text != viewModel.message?.text) viewModel.editMessage(m)
                        linearLayout1.isVisible = false
                        message.setText("")
                        send.setImageResource(R.drawable.ic_send_black)
                    } else {
                        val m =
                            Message(reference.push().key, userI.uid, user.uid, text, Date().time, 0)
                        viewModel.sendMessage(m)
                    }
                    message.setText("")
                }
            }
            cancel.setOnClickListener {
                linearLayout1.isVisible = false
                message.setText("")
                send.setImageResource(R.drawable.ic_send_black)
            }
            linearLayout1.setOnClickListener { binding.recyclerView.scrollToPosition(viewModel.isAdapterDeleteEdit) }
        }
        adapter.itemClick = { message, position, size ->
            showMessagePopupMenu(message, message.fromUser == userI.uid)
            viewModel.isAdapterDeleteEdit = if (size > 1) position - 1 else size - 1
        }
    }

    private fun showMessagePopupMenu(m: Message, isI: Boolean) {
        CustomDialog().showCustomDialog(requireContext(), R.layout.dialog_message, listOf(
            MenuItem(R.id.edit, isI) {
                binding.apply {
                    message.setText(m.text)
                    linearLayout1.isVisible = true
                    textEdit.text = m.text
                    send.setImageResource(R.drawable.ic_done)
                    viewModel.message = m
                }
            }, MenuItem(R.id.delete, true) {
                viewModel.deleteMessage(m, true)
            }, MenuItem(R.id.copy, true) {
                copyText(m.text!!)
            }
        ))
    }

    private fun messageToSettingsFragment() {
        val bundle = Bundle()
        bundle.putSerializable(Util.ARG_USER, user)
        bundle.putBoolean(Util.ARG_USER_EDIT, false)
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_messageFragment_to_settingsFragment, bundle)
    }

    private fun copyText(text: String) {
        val manager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(ClipData.newPlainText("", text))
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            Toast.makeText(requireContext(), getString(R.string.copied), Toast.LENGTH_SHORT).show()
    }

    private fun change() {
        launch {
            viewModel.getMessagesFlow().collect {
                when (it) {
                    is Resource.Error -> {
                        Log.d(TAG, "Error ${it.e}")
                    }

                    is Resource.Loading -> {
                        Log.d(TAG, "Loading")
                    }

                    is Resource.Success -> {
                        adapter.submitList(it.data)
                        viewModel.seeMessage(it.data)
                        if (viewModel.isAdapterDeleteEdit == -1)
                            binding.recyclerView.scrollToPosition(it.data.size - 1)
                        else {
                            binding.recyclerView.scrollToPosition(viewModel.isAdapterDeleteEdit)
                            viewModel.isAdapterDeleteEdit = -1
                        }
                    }
                }

            }

        }
    }

//    private fun sendNotification(notificationRequest: NotificationRequest) =
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = ApiClient.api.postNotification(notificationRequest)
//                if (response.isSuccessful) {
////                    Log.d(TAG, "sendNotification: ${response.body()?.string()}")
//                } else {
//                    Log.w(TAG, "sendNotification: ${response.message()}")
//                }
//            } catch (e: Exception) {
//                Log.d(TAG, "sendNotification: $e")
//            }
//        }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}