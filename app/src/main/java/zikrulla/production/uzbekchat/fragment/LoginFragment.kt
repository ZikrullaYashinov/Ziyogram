package zikrulla.production.uzbekchat.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.databinding.FragmentLoginBinding
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.util.Util
import java.util.Date


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var gso: GoogleSignInOptions
    private lateinit var googleSignIn: GoogleSignInClient
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var sharedPreferenceEditor: Editor
    private val TAG = "@@@@"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        load()

        return binding.root
    }

    private fun load() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.clientId))
            .requestEmail()
            .build()
        googleSignIn = GoogleSignIn.getClient(requireContext(), gso)
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")
        sharedPreference = activity?.getSharedPreferences(Util.SHP_LOGIN, Context.MODE_PRIVATE)!!
        sharedPreferenceEditor = sharedPreference.edit()

        binding.signInGoogle.setOnClickListener {
            val intent = googleSignIn.signInIntent
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val account: GoogleSignInAccount? = task?.getResult(ApiException::class.java)
            val user = User(
                account?.displayName,
                account?.id,
                account?.email,
                account?.photoUrl.toString(),
                Date().time
            )

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    var isHas = false
                    for (it in children) {
                        val value = it.getValue(User::class.java)
                        if (value?.uid == user.uid) {
                            isHas = true
                            break
                        }
                    }
                    if (isHas) {
                        writeShP(user, true)
                        val bundle = Bundle()
                        bundle.putString("id", user.uid)
                        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                        val navController = navHostFragment.navController
                        navController.navigate(R.id.action_loginFragment_to_homeFragment, bundle)
                    } else {
                        setNewUser(user)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        } catch (e: Exception) {
            Log.w(TAG, "handleSignInResult: failed", e)
        }
    }

    private fun setNewUser(user: User) {
        reference.child(user.uid ?: "").setValue(user).addOnSuccessListener {
            writeShP(user, true)
            val bundle = Bundle()
            bundle.putString("id", user.uid)
            val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.action_loginFragment_to_homeFragment, bundle)
        }
    }

    fun writeShP(user: User, isLogin: Boolean) {
        sharedPreferenceEditor.putBoolean(Util.SHP_IS_SIGIN, isLogin).commit()
        sharedPreferenceEditor.putString(Util.SHP_UID, user.uid).commit()
        sharedPreferenceEditor.putString(Util.SHP_NAME, user.displayName).commit()
        sharedPreferenceEditor.putString(Util.SHP_EMAIL, user.email).commit()
        sharedPreferenceEditor.putString(Util.SHP_IMAGE_URL, user.photoUrl).commit()
        sharedPreferenceEditor.putLong(Util.SHP_TIME, user.resentTime!!).commit()
    }

}