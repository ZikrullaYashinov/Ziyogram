package zikrulla.production.uzbekchat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import zikrulla.production.uzbekchat.databinding.ItemUserBinding
import zikrulla.production.uzbekchat.model.User

class UserListAdapter : ListAdapter<User, UserListAdapter.Vh>(MyDiffUtil()) {

    lateinit var itemClick: (user: User) -> Unit

    inner class Vh(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(user: User) {
            binding.apply {
                name.text = user.displayName
                email.text = user.email
                Glide.with(root.context).load(user.photoUrl).centerCrop().into(image)
                rootLayout.setOnClickListener {
                    itemClick.invoke(user)
                }
            }

        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.email == newItem.email
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))
    }

}