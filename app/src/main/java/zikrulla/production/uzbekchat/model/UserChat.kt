package zikrulla.production.uzbekchat.model

class UserChat {

    var user: User? = null
    var newMessage = false

    constructor()

    constructor(user: User?, isNewMessage: Boolean) {
        this.user = user
        this.newMessage = isNewMessage
    }

    override fun toString(): String {
        return "UserChat(user=$user, isNewMessage=$newMessage)"
    }

}