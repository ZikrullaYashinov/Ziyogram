package zikrulla.production.uzbekchat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import zikrulla.production.uzbekchat.databinding.ItemUserBinding
import zikrulla.production.uzbekchat.model.User

class UsersAdapter(
    private val context: Context,
    private var list: List<User>,
    private val listener: ItemClickListener
) :
    RecyclerView.Adapter<UsersAdapter.Vh>() {

    inner class Vh(private val itemUserBinding: ItemUserBinding) :
        ViewHolder(itemUserBinding.root) {
        fun bind(user: User) {
            itemUserBinding.apply {
                name.text = user.displayName
                email.text = user.email
                Glide.with(context)
                    .load(user.photoUrl)
                    .centerCrop()
                    .into(image)
                root.setOnClickListener {
                    Log.d("123", "bind: click")
                    listener.onClick(user)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        val user = list[position]
        holder.bind(user)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun search(l: ArrayList<User>) {
        list = l
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onClick(user: User)
    }
}