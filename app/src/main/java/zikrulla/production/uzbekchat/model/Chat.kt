package zikrulla.production.uzbekchat.model

class Chat : User{
    var lastTime: Long? = null

    constructor()
    constructor(
        user: User,
        lastTime: Long?
    ) : super(user.displayName, user.uid, user.email, user.photoUrl, user.resentTime) {
        this.lastTime = lastTime
    }

    override fun toString(): String {
        return "Chat(lastTime=$lastTime)"
    }

}