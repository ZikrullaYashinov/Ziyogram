package zikrulla.production.uzbekchat.model

class Message {
    var fromUser: User? = null
    var toUser: User? = null
    var text: String? = null
    var time: Long? = null
    var isRead: Boolean? = null

    constructor()

    constructor(fromUser: User?, toUser: User?, text: String?, time: Long?, isRead: Boolean?) {
        this.fromUser = fromUser
        this.toUser = toUser
        this.text = text
        this.time = time
        this.isRead = isRead
    }

    override fun toString(): String {
        return "Message(fromUser=$fromUser, toUser=$toUser, text=$text, time=$time, isRead=$isRead)"
    }

}