package zikrulla.production.uzbekchat.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import zikrulla.production.uzbekchat.R
import zikrulla.production.uzbekchat.databinding.FragmentSplashBinding
import zikrulla.production.uzbekchat.viewmodel.SplashViewModel

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)

        viewModel.getIsRun().observe(viewLifecycleOwner){
            if (it)
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_splashFragment_to_homeFragment)
        }
        return binding.root
    }
}