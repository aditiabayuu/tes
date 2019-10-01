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
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity() {
    private lateinit var login: Button
    private lateinit var usrname: EditText
    private lateinit var pwd: EditText
    private lateinit var context: Context
    private lateinit var dlg: ProgressDialog
    private lateinit var dialog: Dialog
    private val tag = "DEBUG"
    private val debug = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)
        supportActionBar!!.hide()
        context = this
        val SDK_INT = android.os.Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //your codes here
        }
        dialog = Dialog(context)
        login = findViewById<Button>(R.id.btn_login)
        usrname = findViewById(R.id.txt_nama)
        pwd = findViewById(R.id.txt_password)
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
            }

        else {
               cekRegister()
                //hashkey()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent: Intent? = null
        val mainIntent = Intent(context, LoginActivity::class.java)
        context.startActivity(mainIntent)
        finish()

    }

    fun cekRegister() {
        try {
            dlg = ProgressDialog(context)
            dlg.setMessage("Sedang Mengambil Data...")
            dlg.setCancelable(false)
            dlg.show()
            val url = "http://114.57.160.52/piranti/user/create"
            val bodydata = "username="+usrname.text.toString()+"&password="+pwd.text.toString();
            /*bodydata.put("email", usrname.text.toString())
            bodydata.put("password", pwd.text.toString())
            bodydata.put("command", "login")*/
            val JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")
            val client = OkHttpClient.Builder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS).build()
            val body = RequestBody.create(JSON, bodydata.toString())
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
                            val result = JSONObject(resp)
                            Log.d("tag", resp)
                            if (result.getString("responseCode").equals("0")) {
                                dlg.dismiss()
                                val mainIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                this@RegisterActivity.startActivity(mainIntent)
                                finish()

                            } else {
                                dlg.dismiss()
                                dialog.setContentView(R.layout.alert_error)
                                val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
                                val txtContent = dialog.findViewById(R.id.alertcontent) as TextView
                                val erro = result.getString("responseDesc")
                                /* dialog.setCancelable(false);
                                            dialog.setCanceledOnTouchOutside(false);*/
                                txtContent.text = erro
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
}

