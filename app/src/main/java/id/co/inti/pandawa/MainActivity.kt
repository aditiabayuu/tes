package id.co.inti.pandawa

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import id.co.inti.pandawa.dbhelper.DBHelper

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var data: String
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val cursor = dbHelper.getDevice(data)
                val i = cursor.count
                Log.d("tag", "curosor " + i)
                if (cursor != null && cursor.getCount() > 0) {


                    val textFragment = fragmentListDevices()
                    val manager = supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.main_container, textFragment)
                    val args = Bundle()
                    args.putString("name", data);
                    textFragment.setArguments(args);
                    transaction.commit()

                } else {
                    val textFragment = fragmentNodevice()
                    val manager = supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.main_container, textFragment)
                    val args = Bundle()
                    args.putString("name", data);
                    textFragment.setArguments(args);
                    transaction.commit()
                    //manager.popBackStack();
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                val textFragment = fragmentRegisterDev()
                val manager = supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.replace(R.id.main_container, textFragment)
                val args = Bundle()
                args.putString("name", data);
                textFragment.setArguments(args);
                transaction.commit()
               // manager.popBackStack();
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                val textFragment = profil_activity()
                val manager = supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.replace(R.id.main_container, textFragment)
                val args = Bundle()
                args.putString("name", data);
                textFragment.setArguments(args);
                transaction.commit()
               // manager.popBackStack();
                return@OnNavigationItemSelectedListener true
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //supportActionBar?.hide()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        data= Preferences.getLoggedInUser(this@MainActivity)

        dbHelper = DBHelper(this)
        dbHelper.openDB()
        try {
            val intent = intent
            if (intent.hasExtra("name")) {
                data = intent.getStringExtra("name")

            }

        } catch (ex: Exception) {
            ex.printStackTrace()

        }
        val cursor = dbHelper.getDevice(data)
        val i = cursor.count
        Log.d("tag", "curosor " + i)
        if (cursor != null && cursor.getCount() > 0) {


            val textFragment = fragmentListDevices()
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.replace(R.id.main_container, textFragment)
            val args = Bundle()
            args.putString("name", data);
            textFragment.setArguments(args);
            transaction.commit()

        } else {
            val textFragment = fragmentNodevice()
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.replace(R.id.main_container, textFragment)
            val args = Bundle()
            args.putString("name", data);
            textFragment.setArguments(args);
            transaction.commit()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.closeDB()
    }

    override fun onBackPressed() {
       // super.onBackPressed()


        val fm = fragmentManager
        if (fm.backStackEntryCount > 0) {
            Log.i("MainActivity", "popping backstack")
            fm.popBackStack()
        } else {
            /*var intent: Intent? = null
            intent = Intent()
            intent.setClass(
                applicationContext,
                MainActivity::class.java!!
            )
            startActivity(intent)
            */
            super.onBackPressed()
        }
    }
}
