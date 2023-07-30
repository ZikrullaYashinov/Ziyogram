package zikrulla.production.uzbekchat.model.appinfo

class AppInfo {
    var programmer: Campaign? = null
    var designer: Campaign? = null
    var version: Item? = null
    var info: Info? = null
    var contact: Item? = null

    override fun toString(): String {
        return "AppInfo(programmer=$programmer, designer=$designer, version=$version, info=$info, contact=$contact)"
    }

}