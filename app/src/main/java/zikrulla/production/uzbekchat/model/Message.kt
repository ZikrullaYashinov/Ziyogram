package zikrulla.production.uzbekchat.model

class Message {
    var messageId: String? = null
    var fromUser: String? = null
    var toUser: String? = null
    var text: String? = null
    var time: Long? = null
    var isRead: Boolean? = null

    constructor()

    constructor(
        messageId: String?,
        fromUser: String?,
        toUser: String?,
        text: String?,
        time: Long?,
        isRead: Boolean?
    ) {
        this.messageId = messageId
        this.fromUser = fromUser
        this.toUser = toUser
        this.text = text
        this.time = time
        this.isRead = isRead
    }

    override fun toString(): String {
        return "Message(messageId=$messageId, fromUser=$fromUser, toUser=$toUser, text=$text, time=$time, isRead=$isRead)"
    }

}