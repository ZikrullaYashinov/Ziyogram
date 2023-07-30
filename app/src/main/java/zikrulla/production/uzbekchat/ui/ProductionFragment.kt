package zikrulla.production.uzbekchat.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zikrulla.production.uzbekchat.databinding.FragmentProductionBinding
import zikrulla.production.uzbekchat.model.appinfo.AppInfo
import zikrulla.production.uzbekchat.util.Util.APP_VERSION
import zikrulla.production.uzbekchat.viewmodel.ProductionViewModel
import zikrulla.production.uzbekchat.viewmodel.Resource
import kotlin.coroutines.CoroutineContext

class ProductionFragment : Fragment(), CoroutineScope {

    private lateinit var binding: FragmentProductionBinding
    private lateinit var viewModel: ProductionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductionBinding.inflate(inflater, container, false)

        load()
        click()
        change()

        return binding.root
    }

    private fun load() {
        viewModel = ViewModelProvider(this)[ProductionViewModel::class.java]
    }

    private fun click() {
        binding.back.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

    private fun change() {
        launch {
            viewModel.getData().collect {
                when (it) {
                    is Resource.Error -> {
                        Log.d("@@@@", "changeUsers: Error")
                    }

                    is Resource.Loading -> {
                        Log.d("@@@@", "changeUsers: Loading")
                    }

                    is Resource.Success -> {
                        Log.d("@@@@", "changeUsers: Success\n${it.data.toString()}")
                        showAppInfo(it.data)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAppInfo(appInfo: AppInfo) {
        binding.apply {
            mainLayout2.isVisible = true
            share.isVisible = true

            if (APP_VERSION.toFloat() < appInfo.programmer?.imageUrl?.text!!.toFloat()) {
                Glide.with(root.context).load(appInfo.programmer?.imageUrl?.url).centerCrop()
                    .into(zpImage)
            }
            if (APP_VERSION.toFloat() < appInfo.designer?.imageUrl?.text!!.toFloat()) {
                Glide.with(root.context).load(appInfo.designer?.imageUrl?.url).centerCrop().into(dImage)
            }
            zpName1.text = appInfo.programmer?.name1
            zpName2.text = appInfo.programmer?.name2
            dName1.text = appInfo.designer?.name1
            dName2.text = appInfo.designer?.name2
            info.text = appInfo.info?.body

            val newV = appInfo.version?.text!!
            if (APP_VERSION.toFloat() < newV.toFloat())
                newVersion.text = "Yangi $newV versiyani o'rnatish"
            else
                newVersion.isVisible = false

            share.setOnClickListener {
                share("${appInfo.info?.title!!}\n\n${appInfo.info?.body!!}\n\n${appInfo.info?.url!!}")
            }
            zikrullaPruduction.setOnClickListener { goto(appInfo.programmer?.url!!) }
            duraznoGroup.setOnClickListener { goto(appInfo.designer?.url!!) }
            contact.setOnClickListener { goto(appInfo.contact?.url!!) }
            versionCard.setOnClickListener { goto(appInfo.version?.url!!) }
        }
    }

    private fun goto(s: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(s)))

    private fun share(text: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, null))
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main


}