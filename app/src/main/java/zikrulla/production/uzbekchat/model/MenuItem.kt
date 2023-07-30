package zikrulla.production.uzbekchat.model

data class MenuItem(
    val resource: Int,
    val isVisible: Boolean,
    val listener: () -> Unit
)