package zikrulla.production.uzbekchat.model

import java.io.Serializable

class UserNotLastOnline : Serializable {

    var displayName: String? = null
    var uid: String? = null
    var email: String? = null
    var photoUrl: String? = null
    var resentTime: Long? = null
    var deviceTokens: List<String>? = null

    constructor()
    constructor(
        displayName: String?,
        uid: String?,
        email: String?,
        photoUrl: String?,
        resentTime: Long?,
        deviceTokens: List<String>?
    ) {
        this.displayName = displayName
        this.uid = uid
        this.email = email
        this.photoUrl = photoUrl
        this.resentTime = resentTime
        this.deviceTokens = deviceTokens
    }


    override fun toString(): String {
        return "User(displayName=$displayName, uid=$uid, email=$email, photoUrl=$photoUrl, resentTime=$resentTime, deviceTokens=$deviceTokens)"
    }
}
