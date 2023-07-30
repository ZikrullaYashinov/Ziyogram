package zikrulla.production.uzbekchat.model

import java.io.Serializable

class Message : Serializable {
    var messageId: String? = null
    var fromUser: String? = null
    var toUser: String? = null
    var text: String? = null
    var time: Long? = null
    var isRead: Int? = null
    var isEdit: Boolean = false

    constructor()

    constructor(
        messageId: String?,
        fromUser: String?,
        toUser: String?,
        text: String?,
        time: Long?,
        isRead: Int?,
        isEdit: Boolean = false
    ) {
        this.messageId = messageId
        this.fromUser = fromUser
        this.toUser = toUser
        this.text = text
        this.time = time
        this.isRead = isRead
        this.isEdit = isEdit
    }

    override fun toString(): String {
        return "Message(messageId=$messageId, fromUser=$fromUser, toUser=$toUser, text=$text, time=$time, isRead=$isRead, isEdit=$isEdit)"
    }
}