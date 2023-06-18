package zikrulla.production.uzbekchat.util

object Util {

    const val SHP_LOGIN = "sigin"
    const val SHP_IS_SIGIN = "is_sigin"
    const val SHP_UID = "user_id"
    const val SHP_NAME = "user_name"
    const val SHP_IMAGE_URL = "user_image_url"
    const val SHP_EMAIL = "user_email"
    const val SHP_TIME = "user_time"

    const val ARG_USER = "arg_user"
    const val ARG_USER_I = "arg_user_i"
    const val ARG_USER_EDIT = "arg_user_edit"

//    fun sortMessage(l: ArrayList<Message>): ArrayList<Message> {
//        val size = l.size
//        if (size < 2)
//            return l
//        var left = ArrayList<Message>()
//        var right = ArrayList<Message>()
//        val c = l[size - 1]
//        for (i in l) {
//            if (i.time!! > c.time!!) right.add(i) else left.add(i)
//        }
//        if (left.size > 1)
//            left = sortMessage(left)
//        if (right.size > 1)
//            right = sortMessage(right)
//        left.add(c)
//        left.addAll(right)
//        return left
//    }

//    fun sortUser(l: ArrayList<Chat>): ArrayList<Chat> {
//        val size = l.size
//        if (size < 2)
//            return l
//        var left = ArrayList<Chat>()
//        var right = ArrayList<Chat>()
//        val c = l[size - 1]
//        for (i in l) {
//            if (i.lastMessage?.time!! > c.lastMessage?.time!!) right.add(i) else left.add(i)
//        }
//        if (left.size > 1)
//            left = sortUser(left)
//        if (right.size > 1)
//            right = sortUser(right)
//        left.add(c)
//        left.addAll(right)
//        return left
//    }


//    fun showCustomToast(message: String, imageId: Int, color: Int) {
//        val toast = Toast(requireContext())
//        toast.apply {
//            val layout: View =
//                RelativeLayout.inflate(requireContext(), R.layout.toast_success, null)
//            val textView = layout.findViewById<TextView>(R.id.text)
//            textView.text = message
//            val imageView = layout.findViewById<ImageView>(R.id.image)
//            imageView.setImageResource(imageId)
//            val root = layout.findViewById<View>(R.id.toast)
//            root.setBackgroundColor(color)
//            duration = Toast.LENGTH_LONG
//            view = layout
//            show()
//        }
//    }
}