package zikrulla.production.uzbekchat.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.adapter.MessageAdapter
import zikrulla.production.uzbekchat.databinding.FragmentMessageBinding
import zikrulla.production.uzbekchat.model.Message
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.util.Util
import zikrulla.production.uzbekchat.viewmodel.MessagesViewModel

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
    private lateinit var adapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var user: User
    private lateinit var userI: User
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)

        load()
        click()
        change()

        return binding.root
    }

    private fun load() {
        viewModel = ViewModelProvider(this)[MessagesViewModel::class.java]
        messageList = ArrayList()
        adapter = MessageAdapter(user, messageList) {

        }
        binding.apply {
            name.text = user.displayName
            Glide.with(requireContext())
                .load(user.photoUrl)
                .centerCrop()
                .into(image)
        }
        readMessages()
    }

    private fun click() {
        binding.apply {
            image.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable(Util.ARG_USER, user)
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_messageFragment_to_settingsFragment, bundle)
            }
            send.setOnClickListener {

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun change() {
        viewModel.getMessages().observe(viewLifecycleOwner) {
            messageList = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun readMessages() {
        reference.child("users").child(userI.uid ?: "").child(user.uid ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}
