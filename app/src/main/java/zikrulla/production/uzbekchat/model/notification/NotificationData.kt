package zikrulla.production.uzbekchat.model.notification

import zikrulla.production.uzbekchat.model.Message
import zikrulla.production.uzbekchat.model.User
import java.io.Serializable

class NotificationData : Serializable {

    var message: Message? = null
    var fromUser: User? = null
    var toUser: User? = null

    constructor(message: Message?, fromUser: User?, toUser: User?) {
        this.message = message
        this.fromUser = fromUser
        this.toUser = toUser
    }

    constructor()

    override fun toString(): String {
        return "NotificationData(message=$message, fromUser=$fromUser, toUser=$toUser)"
    }


}
