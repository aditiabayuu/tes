package id.co.inti.pandawa

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.ProgressBar

public class  splash : AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        supportActionBar?.hide()
        Handler().postDelayed(Runnable {
            try {
                val mainIntent = Intent(this@splash, LoginActivity::class.java)
                this@splash.startActivity(mainIntent)
                finish()
                return@Runnable
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }, 3000)
    }


    override fun onDestroy() {
        super.onDestroy()
        //db.close();

    }
}
