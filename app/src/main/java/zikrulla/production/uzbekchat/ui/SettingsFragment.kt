package zikrulla.production.uzbekchat.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.databinding.FragmentSettingsBinding
import zikrulla.production.uzbekchat.model.MenuItem
import zikrulla.production.uzbekchat.model.User
import zikrulla.production.uzbekchat.util.PopupMenuService
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
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var user: User
    private var isEditUser: Boolean = false
    private var popupMenuService: PopupMenuService? = null

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
        database = FirebaseDatabase.getInstance()
        reference = database.reference

        binding.apply {
            name.text = user.displayName
            email.text = user.email
            Glide.with(requireContext())
                .load(user.photoUrl)
                .centerCrop()
                .into(photo as ImageView)

        }

        if (!isEditUser) hide()
        binding.setPhoto.visibility = View.GONE
    }

    private fun click() {
        binding.logout.setOnClickListener {
            showPopupMenu(it)
        }
        binding.back.setOnClickListener {
            finish()
        }
        binding.setName.setOnClickListener {
            setName()
        }
        binding.setPhoto.setOnClickListener {
            setPhoto()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            popupMenuService?.dismiss()
            finish()
        }
    }

    private fun finish() {
        Navigation.findNavController(binding.root).popBackStack()
    }

    private fun showPopupMenu(view: View) {
        popupMenuService = PopupMenuService()
        popupMenuService?.showPopupMenu(requireContext(), view, R.layout.menu_settings, listOf(
            MenuItem(R.id.app_info, true) {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_settingsFragment_to_productionFragment)
            }, MenuItem(R.id.exit, true) {
                logOut()
            }
        ))
    }

    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    private fun setName() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_set_name, null, false)

        val name = dialogView.findViewById<EditText>(R.id.name)
        val save = dialogView.findViewById<Button>(R.id.save)
        val cancel = dialogView.findViewById<Button>(R.id.cancel)

        val dialog = builder.setView(dialogView).create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(R.color.transparent))
        dialog.setCancelable(true)

        name.setText(user.displayName)

        cancel.setOnClickListener {
            dialog.dismiss()
        }
        save.setOnClickListener {
            val newName = name.text.toString().trim()
            if (newName.isNotEmpty()) {
                reference.child("users").child(user.uid!!).child("displayName").setValue(newName)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show()
                        user.displayName = newName
                        binding.name.text = newName
                        writeShP(user, true)
                        dialog.dismiss()
                    }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.name_is_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
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

    fun writeShP(user: User, isLogin: Boolean) {
        sharedPreferenceEditor.putBoolean(Util.SHP_IS_SIGIN, isLogin).commit()
        sharedPreferenceEditor.putString(Util.SHP_UID, user.uid).commit()
        sharedPreferenceEditor.putString(Util.SHP_NAME, user.displayName).commit()
        sharedPreferenceEditor.putString(Util.SHP_EMAIL, user.email).commit()
        sharedPreferenceEditor.putString(Util.SHP_IMAGE_URL, user.photoUrl).commit()
        sharedPreferenceEditor.putLong(Util.SHP_TIME, user.resentTime!!).commit()
    }

}