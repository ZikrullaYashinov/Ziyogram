package zikrulla.production.uzbekchat.model.appinfo

class Campaign {
    var url: String? = null
    var imageUrl: Item? = null
    var name1: String? = null
    var name2: String? = null

    override fun toString(): String {
        return "Campaign(url=$url, imageUrl=$imageUrl, name1=$name1, name2=$name2)"
    }

}
