package zikrulla.production.uzbekchat.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.adapter.UserListAdapter
import zikrulla.production.uzbekchat.databinding.FragmentSearchBinding
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.model.UserNotLastOnline
import zikrulla.production.uzbekchat.util.Util
import zikrulla.production.uzbekchat.util.Util.TAG
import zikrulla.production.uzbekchat.viewmodel.Resource
import zikrulla.production.uzbekchat.viewmodel.SearchViewModel
import zikrulla.production.uzbekchat.viewmodel.UsersViewModel
import kotlin.coroutines.CoroutineContext

class SearchFragment : Fragment(), CoroutineScope {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            user = requireArguments().getSerializable(Util.ARG_USER) as User
        }
    }

    private lateinit var binding: FragmentSearchBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private val viewModel: SearchViewModel by viewModels()
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

//        readUsers()
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
//                        Log.d(TAG, "changeUsers: Success ${it.data}")
                        usersList = it.data as ArrayList<User>
                        adapter.submitList(it.data)
                    }
                }
            }
        }
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

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}