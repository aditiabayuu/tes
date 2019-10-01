package id.co.inti.pandawa

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v4.content.ContextCompat
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class  fragmentdetildevice : Fragment(), OnMapReadyCallback
{
    private lateinit var dialog: Dialog
    private var rootView: View? = null
    private lateinit var  img_add : ImageButton
    private lateinit var  add : Button
    private lateinit var data : String
    private lateinit var lats : String
    private lateinit var  long : String
    private lateinit var name : TextView
    private lateinit var temperature : TextView
    private lateinit var humidity: TextView
    private lateinit var lux : TextView
    private lateinit var magnet : TextView
    private lateinit var batt : TextView
    private lateinit var  vibration : TextView
    private lateinit var  door : TextView
    private lateinit var dlg: ProgressDialog
    private lateinit var  cv_temp : CardView
    private lateinit var  cv_hum : CardView
    private lateinit var  cv_light : CardView
    private lateinit var  cv_vibration : CardView
    private lateinit var  cv_door : CardView
    private lateinit var  cv_bat : CardView
    private lateinit var  cv_maps : CardView
    private lateinit var  cv_magnet : CardView
    private lateinit var  maps : ImageView
    private lateinit var mMap: GoogleMap
    var mapFragment : SupportMapFragment?=null
    internal var context: Context? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.detil_devices, container, false)

        temperature= rootView!!.findViewById(R.id.txt_temperature)
        humidity=rootView!!.findViewById(R.id.txt_humidity)
        lux=rootView!!.findViewById(R.id.txt_lux)
        magnet=rootView!!.findViewById(R.id.txt_magnet)
        batt=rootView!!.findViewById(R.id.txt_battery)
        cv_temp= rootView!!.findViewById(R.id.cv_temp);
        cv_hum= rootView!!.findViewById(R.id.cv_hum);
        cv_light= rootView!!.findViewById(R.id.cv_light);
        cv_vibration= rootView!!.findViewById(R.id.cv_vibration);
        cv_door=rootView!!.findViewById(R.id.cv_door)
        cv_bat= rootView!!.findViewById(R.id.cv_bat);
        cv_magnet= rootView!!.findViewById(R.id.cv_magnet);
        cv_maps=rootView!!.findViewById(R.id.cv_maps);
        context= activity
        mapFragment =  childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment

        dialog= Dialog(context)
        vibration=rootView!!.findViewById(R.id.txt_vibration)
        door= rootView!!.findViewById(R.id.txt_door)
        val bundle = this.arguments
        if (bundle != null) {
            data = bundle.getString("dev")
            Log.d("tag", data)
            getData(data)

        }
        return rootView
    }
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(lats.toDouble(), long.toDouble())
        mMap.addMarker(MarkerOptions().position(sydney).title(data))
        val zoomLevel = 16.0f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,zoomLevel))
        //mMap.animateCamera( CameraUpdateFactory.zoomTo( 21.0f ) );
    }
    private fun getData(iddevice: String) {

        activity!!.runOnUiThread {
            try {

                dlg = ProgressDialog(context)
                dlg.setMessage("Sedang Mengambil Data...")
                dlg.setCancelable(false)
                dlg.show()
                val obj = JSONObject()
                obj.put("command", "getData")
                obj.put("id_device", iddevice)
                obj.put("js", "sensit.js")
                val client = OkHttpClient.Builder()
                    .connectTimeout(200, TimeUnit.SECONDS)
                    .writeTimeout(200, TimeUnit.SECONDS)
                    .readTimeout(200, TimeUnit.SECONDS).build()
                val JSON = MediaType.parse("application/json; charset=utf-8")
                val body = RequestBody.create(JSON, obj.toString())
                Log.d("tag:", obj.toString())
                val request = Request.Builder()
                    .url("http://tms.inti.co.id:7002")
                    .post(body)
                    .build()

                client.newCall(request)
                    .enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            // Error

                            activity!!.runOnUiThread {
                                dlg.dismiss()
                                dialog.setContentView(R.layout.alert_error)
                                val dialogButton = dialog.findViewById<View>(R.id.dialogButtonOK) as Button
                                val txtContent = dialog.findViewById<View>(R.id.alertcontent) as TextView
                                txtContent.text = "Periksa Kembali Koneksi Internet Anda"
                                dialog.show()
                                dialogButton.setOnClickListener { dialog.dismiss() }
                            }
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            activity!!.runOnUiThread {
                                try {
                                    val rsp = response.body()!!.string()
                                    Log.d("tag", rsp)
                                    var result: JSONObject? = null

                                    result = JSONObject(rsp)
                                    if (result.getString("responseCode") == "0") {
                                        dlg.dismiss()
                                        val arrays = result.getJSONArray("data")
                                        lats= result.getString("latitude")
                                        long= result.getString("longitude")
                                        mapFragment?.getMapAsync(this@fragmentdetildevice)
                                        //it.setCount(arrays.length());
                                        Log.d("tag", "cot :" + arrays.length())
                                        var a = 0
                                        for (i in 0 until arrays.length()) {
                                            var `val` = arrays.getJSONObject(i)
                                            if (`val`.getString("key") != "" && `val`.has("value") && `val`.getString("value") != "")
                                            {
                                                if (`val`.getString("key").equals("temperature"))
                                                {
                                                    cv_temp.setVisibility(View.VISIBLE)
                                                    temperature.setText( `val`.getString("key") + " : " + `val`.getString("value") + " "+ `val`.getString(
                                                        "unit"));
                                                }

                                                if (`val`.getString("key").equals("humidity"))
                                                {
                                                    cv_hum.setVisibility(View.VISIBLE)
                                                    humidity.setText( `val`.getString("key") + " : " + `val`.getString("value") + " "+ `val`.getString(
                                                        "unit"));
                                                }

                                                if (`val`.getString("key").equals("light"))
                                                {
                                                    cv_light.setVisibility(View.VISIBLE)
                                                    lux.setText( `val`.getString("key") + " : " + `val`.getString("value") + " "+ `val`.getString(
                                                        "unit"));
                                                }

                                                if (`val`.getString("key").equals("magnet"))
                                                {
                                                    cv_magnet.setVisibility(View.VISIBLE)
                                                    magnet.setText( `val`.getString("key") + " : " + `val`.getString("value") + " "+ `val`.getString(
                                                        "unit"));
                                                }

                                                if (`val`.getString("key").equals("vibration"))
                                                {
                                                    cv_vibration.setVisibility(View.VISIBLE)
                                                    vibration.setText( `val`.getString("key") + " : " + `val`.getString("value") + " "+ `val`.getString(
                                                        "unit") );
                                                }

                                                if (`val`.getString("key").equals("door"))
                                                {
                                                    cv_door.setVisibility(View.VISIBLE)
                                                    door.setText( `val`.getString("key") + " : " + `val`.getString("value") + " "+ `val`.getString(
                                                        "unit") );
                                                }

                                                if (`val`.getString("key").equals("battery"))
                                                {
                                                    cv_bat.setVisibility(View.VISIBLE)
                                                    batt.setText( `val`.getString("key") + " : " + `val`.getInt("value") + " "+ `val`.getString("unit") );
                                                }





                                            }
                                        }

                                            /*val temp = result.getString("temperature")
                                        if (TextUtils.isEmpty(temp))
                                        {
                                         temperature.setText("DATA TEMPERATURE TIDAK DITEMUKAN")
                                        }
                                        else {
                                            temperature.setText("TEMPERATURE : "+ temp+" C")
                                        }

                                        val hum = result.getString("humidity")
                                        if (TextUtils.isEmpty(hum))
                                        {
                                            humidity.setText("DATA HUMIDITY TIDAK DITEMUKAN")
                                        }
                                        else {
                                            humidity.setText("HUMIDITY : "+ hum+" %")
                                        }

                                        val luxs = result.getString("lux")
                                        if (TextUtils.isEmpty(luxs))
                                        {
                                            lux.setText("DATA LUX TIDAK DITEMUKAN")
                                        }
                                        else {
                                            lux.setText("LUX : "+ luxs+" %")
                                        }

                                        val doors= result.getString("door_status")
                                        if (TextUtils.isEmpty(doors))
                                        {
                                            door.setText("STATUS PINTU TIDAK DIKETAHUI")
                                        }
                                        else {
                                            door.setText("PINTU : "+ doors+" %")
                                        }

                                        val vib= result.getString("vibration_status")
                                        if (TextUtils.isEmpty(doors))
                                        {
                                            vibration.setText("STATUS VIBRATOR TIDAK DIKETAHUI")
                                        }
                                        else {
                                            vibration.setText("STATUS VIBRATOR : "+ vib+" %")
                                        }

                                        val bats= result.getString("battery")
                                        if (TextUtils.isEmpty(bats))
                                        {
                                            batt.setText("STATUS BATERAI TIDAK DIKETAHUI")
                                        }
                                        else {
                                            batt.setText("BATERAI : "+ bats+" %")
                                        }
                                        //String devs = result.getString("devList");
                                        */
                                    } else {
                                        Toast.makeText(context, result.getString("msg"), Toast.LENGTH_SHORT).show()
                                        dlg.dismiss()
                                        dialog.setContentView(R.layout.alert_error)
                                        val dialogButton = dialog.findViewById<View>(R.id.dialogButtonOK) as Button
                                        val txtContent = dialog.findViewById<View>(R.id.alertcontent) as TextView
                                        txtContent.text = "DATA DEVICE TIDAK DITEMUKAN"
                                        dialog.show()
                                        dialogButton.setOnClickListener { dialog.dismiss() }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    dlg.dismiss()
                                    dialog.setContentView(R.layout.alert_error)
                                    val dialogButton = dialog.findViewById<View>(R.id.dialogButtonOK) as Button
                                    val txtContent = dialog.findViewById<View>(R.id.alertcontent) as TextView
                                    txtContent.text = "NOT JSON FORMAT"
                                    dialog.show()
                                    dialogButton.setOnClickListener { dialog.dismiss() }
                                }
                            }

                        }
                    })
            } catch (ex: Exception) {
                ex.printStackTrace()
                dlg.dismiss()
                Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun loadMap(lats : String, longs : String): String{
        //location handling

        val mapUrlInitial = "https://maps.googleapis.com/maps/api/staticmap?"

        val mapUrlProperties = "&zoom=13&size=600x300&markers=color:red%7C"
        val mapUrlMapType = "&maptype=roadmap"
        val latLong: String = "" +lats + "," + longs
        val keys ="&key=AIzaSyBnIBn5dkaFGAG7Ylf8Jp6Xkoh_vWdkQOQ"
        Log.d("tag",mapUrlInitial + latLong + mapUrlProperties + latLong + mapUrlMapType + keys)
        return mapUrlInitial + latLong + mapUrlProperties + latLong +  mapUrlMapType+ keys

    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap =
            Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}


