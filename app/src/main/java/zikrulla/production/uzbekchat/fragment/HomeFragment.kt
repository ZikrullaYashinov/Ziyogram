package zikrulla.production.uzbekchat.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding :FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        showCustomToast("Success Toast", R.drawable.ic_launcher_foreground, R.color.teal_200)

        return binding.root
    }

    fun showCustomToast(message: String, imageId: Int, color: Int) {
        val toast = Toast(requireContext())
        toast.apply {
            val layout: View = RelativeLayout.inflate(requireContext(), R.layout.toast_success, null)
            val textView = layout.findViewById<TextView>(R.id.text)
            textView.text = message
            val imageView = layout.findViewById<ImageView>(R.id.image)
            imageView.setImageResource(imageId)
            val root = layout.findViewById<View>(R.id.toast)
            root.setBackgroundColor(color)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

}