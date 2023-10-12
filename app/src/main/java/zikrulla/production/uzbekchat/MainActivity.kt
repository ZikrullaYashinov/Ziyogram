package zikrulla.production.uzbekchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import zikrulla.production.uzbekchat.model.User

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val id = intent?.getIntExtra("id", -1)
        val fromUser = intent?.getSerializableExtra("fromUser") as User
        val toUser = intent.getSerializableExtra("toUser") as User

        val bundle = Bundle()
        bundle.putSerializable("fromUser", fromUser)
        bundle.putSerializable("toUser", toUser)

        if (id != -1){
            val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
            navController.navigate(id!!, bundle)
        }

    }

}