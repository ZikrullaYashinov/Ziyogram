package zikrulla.production.uzbekchat.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.databinding.FragmentSettingsBinding
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.util.Util

class SettingsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            user = requireArguments().getSerializable(Util.ARG_USER) as User
            isEditUser = requireArguments().getBoolean(Util.ARG_USER_EDIT, false)
        }
    }

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var gso: GoogleSignInOptions
    private lateinit var googleSignIn: GoogleSignInClient
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var sharedPreferenceEditor: SharedPreferences.Editor
    private lateinit var user: User
    private var isEditUser: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        load()
        click()

        return binding.root
    }

    private fun load() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.clientId))
            .requestEmail()
            .build()
        googleSignIn = GoogleSignIn.getClient(requireContext(), gso)
        sharedPreference = activity?.getSharedPreferences(Util.SHP_LOGIN, Context.MODE_PRIVATE)!!
        sharedPreferenceEditor = sharedPreference.edit()

        binding.apply {
            name.text = user.displayName
            email.text = user.email
            Glide.with(requireContext())
                .load(user.photoUrl)
                .centerCrop()
                .into(photo)
        }

        if (!isEditUser) hide()
    }

    private fun click() {
        binding.logout.setOnClickListener {
            logOut()
        }
        binding.back.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
        binding.setName.setOnClickListener {
            setName()
        }
        binding.setPhoto.setOnClickListener {
            setPhoto()
        }
    }

    private fun setName() {

    }

    private fun setPhoto() {

    }

    private fun hide() {
        binding.apply {
            setName.visibility = View.GONE
            setPhoto.visibility = View.GONE
            logout.visibility = View.GONE
            profile.text = getString(R.string.profile)
        }
    }

    private fun logOut() {
        googleSignIn.signOut()
        sharedPreferenceEditor.putBoolean(Util.SHP_IS_SIGIN, false).commit()
        Navigation.findNavController(binding.root).popBackStack()
    }

}