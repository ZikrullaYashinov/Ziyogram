package zikrulla.production.uzbekchat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import zikrulla.production.uzbekchat.databinding.ItemMessageLeftBinding
import zikrulla.production.uzbekchat.databinding.ItemMessageRightBinding
import zikrulla.production.uzbekchat.model.Message
import zikrulla.production.uzbekchat.model.User
import java.text.SimpleDateFormat

class MessageListAdapter(val _user: User) : ListAdapter<Message, ViewHolder>(MyDiffUtil()) {

    private val MESSAGE_LEFT = 0
    private val MESSAGE_RIGHT = 1

    lateinit var itemClick: (message: Message) -> Unit

    inner class VhMessageLeft(private val itemMessageLeftBinding: ItemMessageLeftBinding) :
        ViewHolder(itemMessageLeftBinding.root) {
        fun bind(message: Message) {
            itemMessageLeftBinding.apply {
                text.text = message.text
                time.text = dateFormat(message.time ?: 0)
            }
        }
    }

    inner class VhMessageRight(private val itemMessageRightBinding: ItemMessageRightBinding) :
        ViewHolder(itemMessageRightBinding.root) {
        fun bind(message: Message) {
            itemMessageRightBinding.apply {
                text.text = message.text
                time.text = dateFormat(message.time ?: 0)
            }
        }
    }

    class MyDiffUtil: DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return newItem.fromUser == oldItem.fromUser
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateFormat(time: Long): String {
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return format.format(time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            MESSAGE_RIGHT -> VhMessageRight(
                ItemMessageRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> VhMessageLeft(
                ItemMessageLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.fromUser == _user.uid)
            MESSAGE_RIGHT
        else
            MESSAGE_LEFT
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = getItem(position)
        if (holder is MessageListAdapter.VhMessageRight) {
            holder.bind(message)
        } else if(holder is MessageListAdapter.VhMessageLeft) {
            holder.bind(message)
        }
        holder.itemView.setOnClickListener {
            itemClick.invoke(message)
        }
    }
}