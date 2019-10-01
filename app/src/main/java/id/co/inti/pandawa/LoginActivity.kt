package id.co.inti.pandawa

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import id.co.inti.pandawa.dbhelper.DBHelper
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.R.id.edit
import org.json.JSONArray


class LoginActivity : AppCompatActivity() {
    private lateinit var login: Button
    private lateinit var usrname: EditText
    private lateinit var pwd: EditText
    private lateinit var daftar: TextView
    private lateinit var context: Context
    private lateinit var dlg: ProgressDialog
    private lateinit var dialog: Dialog
    private val tag = "DEBUG"
    private val debug = true
    private lateinit  var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        supportActionBar!!.hide()
        context = this
        val SDK_INT = android.os.Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //your codes here
        }

        dbHelper = DBHelper(this)
        dbHelper.openDB()
        dialog = Dialog(context)
        login = findViewById<Button>(R.id.btn_login)
        usrname = findViewById(R.id.txt_username)
        pwd = findViewById(R.id.txt_pwd)
        daftar = findViewById(R.id.txt_daftar);
        login.setOnClickListener {
            usrname.setError(null)
            pwd.setError(null)
            var cancel = false
            var focusView: View? = null
            var usr = usrname.text.toString()
            var password = pwd.text.toString()
            if (TextUtils.isEmpty(usr)) {
                usrname.setError(getString(R.string.error_field))
                focusView = usrname
                cancel = true
            }
            if (TextUtils.isEmpty(password)) {
                pwd.setError(getString(R.string.error_field))
                focusView = pwd
                cancel = true
            } else {
                //hashkey()


                   ceklogin()
                    //finish()

            }
        }
        daftar.setOnClickListener {
            val mainIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
            mainIntent.putExtra("name",usrname.text.toString())
            this@LoginActivity.startActivity(mainIntent)
        }
    }



    fun ceklogin() {
        try {
            dlg = ProgressDialog(context)
            dlg.setMessage("Sedang Mengambil Data...")
            dlg.setCancelable(false)
            dlg.show()
            val url = "http://114.57.160.52/piranti-dev/auth/ajaxLogin"
            val bodydata= JSONObject()
            //val bodydata = "username="+usrname.text.toString()+"&password="+pwd.text.toString();
            bodydata.put("username", usrname.text.toString())
            bodydata.put("password", pwd.text.toString())
            bodydata.put("token_firebase", "ldsadsadsaad")
            val JSON = MediaType.parse("application/json; charset=utf-8")
            val client = OkHttpClient.Builder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS).build()
            val body = RequestBody.create(JSON, bodydata.toString())
            Log.d(tag,bodydata.toString())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        // For the example, you can show an error dialog or a toast
                        // on the main UI thread
                        dlg.dismiss()
                        dialog.setContentView(R.layout.alert_error)
                        val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
                        val txtContent = dialog.findViewById(R.id.alertcontent) as TextView
                        /* dialog.setCancelable(false);
                              dialog.setCanceledOnTouchOutside(false);*/
                        txtContent.text = "PERIKSA KEMBALI KONEKSI ANDA"
                        dialog.show()
                        dialogButton.setOnClickListener { dialog.dismiss() }
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread {
                        try {
                            val resp = response.body()!!.string()
                            Log.d("tag", resp)
                            val result = JSONObject(resp)

                            if (result.getString("responseCode").equals("0")) {
                                dlg.dismiss()
                                val js = result.getJSONObject("responseData");
                                val token= js.getString("token_key");

                                val ids= js.getString("id")
                                val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                                mainIntent.putExtra("name",usrname.text.toString())
                                Preferences.setRegisteredUser(getBaseContext(),usrname.text.toString())
                                Preferences.setRegisteredPass(getBaseContext(),token)
                                Preferences.setLoggedInUser(getBaseContext(),Preferences.getRegisteredUser(getBaseContext()));
                                Preferences.setLoggedInStatus(getBaseContext(),true);
                                Preferences.setRegisteredId(getBaseContext(),ids)


                                this@LoginActivity.startActivity(mainIntent)
                                finish()

                            } else {
                                dlg.dismiss()
                                dialog.setContentView(R.layout.alert_error)
                                val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
                                val txtContent = dialog.findViewById(R.id.alertcontent) as TextView
                                val erro = result.getString("responseDesc")
                                /* dialog.setCancelable(false);
                                        dialog.setCanceledOnTouchOutside(false);*/
                                txtContent.text =erro
                                dialog.show()
                                dialogButton.setOnClickListener { dialog.dismiss() }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            dlg.dismiss()
                            dialog.setContentView(R.layout.alert_error)
                            val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
                            val txtContent = dialog.findViewById(R.id.alertcontent) as TextView
                            /* dialog.setCancelable(false);
                                    dialog.setCanceledOnTouchOutside(false);*/
                            txtContent.text = "Response Tidak Dikenali"
                            dialog.show()
                            dialogButton.setOnClickListener { dialog.dismiss() }
                        }


                    }

                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
            //ex.printStackTrace()
            dlg.dismiss()
            dialog.setContentView(R.layout.alert_error)
            val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
            val txtContent = dialog.findViewById(R.id.alertcontent) as TextView
            /* dialog.setCancelable(false);
                                    dialog.setCanceledOnTouchOutside(false);*/
            txtContent.text = "Terjadi Kesalahan"
            dialog.show()
            dialogButton.setOnClickListener { dialog.dismiss() }

        }

    }

    override fun onStart() {
        super.onStart()
        if (Preferences.getLoggedInStatus(baseContext)) {
            startActivity(Intent(baseContext, MainActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}