package zikrulla.production.uzbekchat.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import zikrulla.production.uzbekchat.model.MenuItem

class CustomDialog {

    @SuppressLint("ResourceType")
    fun showCustomDialog(context: Context, layoutId: Int, list: List<MenuItem>) {
        val builder = AlertDialog.Builder(context)
        val dialogView =
            LayoutInflater.from(context).inflate(layoutId, null, false)

        val dialog = builder.setView(dialogView).create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        list.forEach { menuItem ->
            val view = dialogView.findViewById<View>(menuItem.resource)
            view.isVisible = menuItem.isVisible
            view.setOnClickListener {
                menuItem.listener.invoke()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

}