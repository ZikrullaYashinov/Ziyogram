package zikrulla.production.uzbekchat.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.adapter.UserListAdapter
import zikrulla.production.uzbekchat.databinding.FragmentSearchBinding
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.util.Util
import zikrulla.production.uzbekchat.viewmodel.UsersViewModel

class SearchFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            user = requireArguments().getSerializable(Util.ARG_USER) as User
        }
    }

    private lateinit var binding: FragmentSearchBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var viewModel: UsersViewModel
    private lateinit var usersList: ArrayList<User>
    private lateinit var user: User
    private val adapter by lazy { UserListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        loadCreate()
        click()
        change()

        return binding.root
    }

    private fun loadCreate() {
        viewModel = ViewModelProvider(this)[UsersViewModel::class.java]
        database = FirebaseDatabase.getInstance()
        reference = database.reference
        usersList = ArrayList()
        adapter.itemClick = {
            val bundle = Bundle()
            bundle.putSerializable(Util.ARG_USER, it)
            bundle.putSerializable(Util.ARG_USER_I, user)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_searchFragment_to_messageFragment, bundle)
        }
        binding.recyclerView.adapter = adapter

        readUsers()
    }

    private fun click() {
        binding.search.addTextChangedListener {
            search(usersList, it.toString())
        }

        binding.back.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun change() {
        viewModel.getUsers().observe(viewLifecycleOwner) {
            val s = binding.search.text.toString().trim()
            if (s.isNotEmpty())
                search(it, s)
            else
                adapter.submitList(it)
        }
    }

    private fun readUsers() {
        reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    usersList.clear()
                    snapshot.children.forEach {
                        val u = it.getValue(User::class.java)!!
                        if (u.uid != user.uid)
                            usersList.add(u)
                    }
                    viewModel.fetchUsers(usersList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun search(l: ArrayList<User>, query: String?) {
        if (query!!.trim().isEmpty()) {
            adapter.submitList(l)
        } else {
            val searchUser = ArrayList<User>()
            for (i in l) {
                if (i.displayName?.lowercase()!!.contains(query.trim().lowercase()))
                    searchUser.add(i)
            }
            adapter.submitList(searchUser)
        }
    }
}