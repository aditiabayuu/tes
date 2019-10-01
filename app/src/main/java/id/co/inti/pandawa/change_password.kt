package id.co.inti.pandawa

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class change_password : Fragment()
{
    private lateinit var dialog: Dialog
    private var rootView: View? = null
    private lateinit var dlg: ProgressDialog
    private lateinit var  add : Button
    private val tags ="DEBUG"
    internal lateinit var context: Context
    private lateinit var pwd : EditText
    private lateinit var conf_pwd : EditText
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_change_password, container, false)
        context= this!!.activity!!
        (activity as AppCompatActivity).supportActionBar!!.hide()
        dialog= Dialog(context)
        add= rootView!!.findViewById(R.id.btn_login)
        pwd= rootView!!.findViewById(R.id.txt_pwd)
        conf_pwd=rootView!!.findViewById(R.id.txt_conf_pwd)
        add.setOnClickListener{
            conf_pwd.setError(null)
            pwd.setError(null)
            var cancel = false
            var focusView: View? = null
            if (TextUtils.isEmpty(pwd.text.toString()))
            {
                pwd.setError(getString(R.string.error_field))
                focusView = pwd
                cancel = true
            }
           else  if (TextUtils.isEmpty(conf_pwd.text.toString()))
            {
                conf_pwd.setError(getString(R.string.error_field))
                focusView = conf_pwd
                cancel = true
            }
            else if (!pwd.text.toString().equals(conf_pwd.text.toString()))
            {
                Toast.makeText(context,"Password Tidak Sama",Toast.LENGTH_SHORT).show()
            }
            else
            {
                    ceklogin()
            }
        }
        return  rootView
    }

    fun ceklogin() {
        try {
            dlg = ProgressDialog(context)
            dlg.setMessage("Sedang Mengambil Data...")
            dlg.setCancelable(false)
            dlg.show()
            val url = "http://114.57.160.52/piranti-dev/user/ajaxUpdatePassword"
            val obj = JSONObject()
            val ids= Preferences.getRegisteredId(context)
            obj.put("command", "updatePassword")
            obj.put("userid", ids)
            obj.put("passsword", pwd.text.toString())
            //val bodydata = "username="+usrname.text.toString()+"&password="+pwd.text.toString();
            /*bodydata.put("email", usrname.text.toString())
            bodydata.put("password", pwd.text.toString())
            bodydata.put("command", "login")*/
            val JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")
            val client = OkHttpClient.Builder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS).build()

            val body = RequestBody.create(JSON, obj.toString())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    activity!!.runOnUiThread {
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
                    activity!!.runOnUiThread {
                        try {
                            val resp = response.body()!!.string()
                            val result = JSONObject(resp)
                            Log.d("tag", resp)
                            if (result.getString("responseCode").equals("0")) {
                                dlg.dismiss()
                                val intent = Intent()
                                intent.setClass(activity!!, MainActivity::class.java!!)
                                activity!!.startActivity(intent)

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
}