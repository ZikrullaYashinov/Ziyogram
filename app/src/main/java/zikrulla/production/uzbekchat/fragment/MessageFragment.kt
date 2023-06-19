package zikrulla.production.uzbekchat.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.adapter.MessageListAdapter
import zikrulla.production.uzbekchat.databinding.FragmentMessageBinding
import zikrulla.production.uzbekchat.model.Message
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.util.Util
import zikrulla.production.uzbekchat.viewmodel.MessagesViewModel
import java.util.Date

class MessageFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            user = requireArguments().getSerializable(Util.ARG_USER) as User
            userI = requireArguments().getSerializable(Util.ARG_USER_I) as User
        }
    }

    private lateinit var binding: FragmentMessageBinding
    private lateinit var viewModel: MessagesViewModel
    private val adapter by lazy { MessageListAdapter(userI) }
    private lateinit var messageList: ArrayList<Message>
    private lateinit var user: User
    private lateinit var userI: User
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var manager: LinearLayoutManager
    private var p = 0
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

    override fun onStart() {
        super.onStart()
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = activity?.currentFocus

    }

    private fun load() {
        viewModel = ViewModelProvider(this)[MessagesViewModel::class.java]
        database = FirebaseDatabase.getInstance()
        reference = database.reference
        messageList = ArrayList()

        adapter.apply {
            itemClick = {
                Log.d(TAG, "load: ")
            }
        }

        manager = LinearLayoutManager(requireContext())
        binding.apply {
            recyclerView.adapter = adapter
            name.text = user.displayName
            Glide.with(requireContext()).load(user.photoUrl).centerCrop().into(image)
        }

        readMessages()
    }

    private fun click() {
        binding.apply {
            image.setOnClickListener { messageToSettingsFragment() }
            back.setOnClickListener { Navigation.findNavController(binding.root).popBackStack() }
            name.setOnClickListener { messageToSettingsFragment() }
            send.setOnClickListener {
                val text = message.text.toString().trim()
                val massage =
                    Message(reference.push().key, userI.uid, user.uid, text, Date().time, false)
                sendMessage(massage)
                message.setText("")
            }
            message.addTextChangedListener {
                val text = message.text.toString().trim()
                send.isEnabled = text.isNotEmpty()
            }
        }
    }

    private fun messageToSettingsFragment() {
        val bundle = Bundle()
        bundle.putSerializable(Util.ARG_USER, user)
        bundle.putBoolean(Util.ARG_USER_EDIT, false)
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_messageFragment_to_settingsFragment, bundle)
    }

    private fun change() {
        viewModel.getMessages().observe(viewLifecycleOwner) {
            Log.d(TAG, "change: $it")
            adapter.submitList(it)
            binding.recyclerView.scrollToPosition(it.size - 1)
        }
    }

    private fun readMessages() {
        reference.child("users").child(userI.uid ?: "").child("messages")
            .child(user.uid ?: "").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    snapshot.children.forEach {
                        val message = it.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    viewModel.fetchMessages(messageList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun sendMessage(message: Message) {
        reference.child("users").child(userI.uid ?: "").child("messages").child(user.uid ?: "")
            .child(message.messageId ?: "").setValue(message).addOnSuccessListener {

            }
        reference.child("users").child(user.uid ?: "").child("messages").child(userI.uid ?: "")
            .child(message.messageId ?: "").setValue(message)
    }
}