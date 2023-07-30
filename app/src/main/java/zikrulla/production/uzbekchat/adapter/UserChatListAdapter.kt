package zikrulla.production.uzbekchat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import zikrulla.production.uzbekchat.databinding.ItemUserBinding
import zikrulla.production.uzbekchat.model.UserChat

class UserChatListAdapter : ListAdapter<UserChat, UserChatListAdapter.Vh>(MyDiffUtil()) {

    lateinit var itemClick: (user: UserChat) -> Unit
    lateinit var itemLongClick: (user: UserChat) -> Unit

    inner class Vh(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(user: UserChat) {
            binding.apply {
                name.text = user.user?.displayName
                email.text = user.user?.email
                newMessage.isVisible = user.newMessage
                Glide.with(root.context).load(user.user?.photoUrl).centerCrop().into(image)
                rootLayout.setOnClickListener { itemClick.invoke(user) }
                rootLayout.setOnLongClickListener {
                    itemLongClick.invoke(user)
                    return@setOnLongClickListener true
                }
            }

        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<UserChat>() {
        override fun areItemsTheSame(oldItem: UserChat, newItem: UserChat): Boolean {
            return oldItem.user?.uid == newItem.user?.uid
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: UserChat, newItem: UserChat): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))
    }

}