package zikrulla.production.uzbekchat.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import zikrulla.production.uzbekchat.databinding.FragmentHomeBinding
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.util.Util
import zikrulla.production.uzbekchat.viewmodel.UsersViewModel
import java.util.Date

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var viewModel: UsersViewModel
    private val adapter by lazy { UserListAdapter() }
    private lateinit var userList: ArrayList<User>
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        loadCreate()
        loadStart()
        click()
        changeUsers()

        return binding.root
    }

    private fun loadCreate() {
        sharedPreference = activity?.getSharedPreferences(Util.SHP_LOGIN, Context.MODE_PRIVATE)!!
        database = FirebaseDatabase.getInstance()
        reference = database.reference
        userList = ArrayList()
        viewModel = ViewModelProvider(this)[UsersViewModel::class.java]
        adapter.itemClick = {

        }
        binding.recyclerView.adapter = adapter
    }

    private fun loadStart() {
        val name = sharedPreference.getString(Util.SHP_NAME, "")
        val email = sharedPreference.getString(Util.SHP_EMAIL, "")
        val url = sharedPreference.getString(Util.SHP_IMAGE_URL, "")
        val uid = sharedPreference.getString(Util.SHP_UID, "")
        val time = Date().time
        user = User(name, uid, email, url, time)

        readUsers()
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

    @SuppressLint("NotifyDataSetChanged")
    private fun changeUsers() {
        viewModel.getUsers().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onStart() {
        super.onStart()
        readShP()
    }

    private fun readUsers() {
        reference.child("users").child(user?.uid!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (user in snapshot.children) {
                        val value = user.getValue(User::class.java)
                        if (value != null) {
                            userList.add(value)
                        }
                    }
                    viewModel.fetchUsers(userList)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
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

}