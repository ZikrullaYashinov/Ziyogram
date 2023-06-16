package zikrulla.production.uzbekchat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import zikrulla.production.uzbekchat.databinding.ItemMessageLeftBinding
import zikrulla.production.uzbekchat.databinding.ItemMessageRightBinding
import zikrulla.production.uzbekchat.model.Message
import zikrulla.production.uzbekchat.model.User
import java.text.SimpleDateFormat

class MessageAdapter(
    private val user: User,
    private val list: List<Message>,
    private val itemClick: (message: Message) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    private val MESSAGE_LEFT = 0
    private val MESSAGE_RIGHT = 1

    @SuppressLint("SimpleDateFormat")
    private fun dateFormat(time: Long): String {
        val format = SimpleDateFormat("dd.MM.yyyy mm:ss")
        return format.format(time)
    }

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
        val message = list[position]
        return if (message.fromUser?.uid == user.uid)
            MESSAGE_RIGHT
        else
            MESSAGE_LEFT
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = list[position]
        if (holder is VhMessageRight) {
            holder.bind(message)
        } else if(holder is VhMessageLeft) {
            holder.bind(message)
        }
        holder.itemView.setOnClickListener {
            itemClick.invoke(message)
        }
    }
}