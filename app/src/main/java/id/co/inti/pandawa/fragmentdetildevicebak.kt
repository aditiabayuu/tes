package id.co.inti.pandawa

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import com.ekn.gruzer.gaugelibrary.ArcGauge
import com.ekn.gruzer.gaugelibrary.Range
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import id.co.inti.pandawa.dbhelper.DBHelper
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.ColumnChartView
import lecho.lib.hellocharts.view.LineChartView
import java.lang.Exception
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class  fragmentdetildevicebak : Fragment(), OnMapReadyCallback
{
    private lateinit var dialog: Dialog
    private var rootView: View? = null
    private lateinit var  img_add : ImageButton
    private lateinit var dlg: ProgressDialog
    private lateinit var mMap: GoogleMap
    private lateinit var map: GoogleMap
    internal lateinit var context: Context
    private lateinit var dbHelper: DBHelper
    private lateinit var lats : String
    private lateinit var  long : String
    private val tags ="DEBUG"
    private lateinit var ll : LinearLayout
    private lateinit var data : String
    private lateinit var adds : TextView
    private lateinit var line : ArrayList<LatLng>
    var places=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.detil_devices_bak, container, false)
        (activity as AppCompatActivity).supportActionBar!!.show()
        ll=rootView!!.findViewById(R.id.lytss)
        context= this!!.activity!!
        line=ArrayList<LatLng>()
        dialog= Dialog(context)
        dbHelper = DBHelper(context)
        dbHelper.openDB()
        val bundle = this.arguments
        if (bundle != null) {
            data = bundle.getString("dev")
            Log.d("tag", data)
            getData(data)
        }
        (activity as AppCompatActivity).getSupportActionBar()!!.setTitle(Html.fromHtml("<font color='#ffffff'>Widget</font>"));
        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap?) {
            mMap = googleMap!!
            mMap.clear()
            // Add a marker in Sydney and move the camera
            val sydney = LatLng(lats.toDouble(), long.toDouble())
            mMap.addMarker(MarkerOptions().position(sydney).title(data))
            val zoomLevel = 16.0f
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.drawer_view, menu);

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_first_fragment -> {
                val frg = fragment_add_widget()
                val ft = fragmentManager!!.beginTransaction()
                ft.replace(R.id.main_container, frg, "get")
                val bundle = Bundle()
                bundle.putString("dev", data)
                frg.setArguments(bundle);
                ft.addToBackStack(null)
                dbHelper.closeDB()
                ft.commit()
                // Not implemented here
                return true
            }
            R.id.nav_second_fragment -> {
                val frg = fragment_alert()
                val ft = fragmentManager!!.beginTransaction()
                ft.replace(R.id.main_container, frg, "get")
                val bundle = Bundle()
                bundle.putString("dev", data)
                frg.setArguments(bundle);
                ft.addToBackStack(null)
                dbHelper.closeDB()
                ft.commit()
                // Not implemented here
                return true

            // Do Fragment menu item stuff here
            return true
            }

            else -> {
            }
        }

        return false
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
                                        Log.d("tag", "cot :" + arrays.length())
                                        val a = arrays.length()
                                        val keys_temp = arrayOfNulls<String>(a)
                                        val value_temp = arrayOfNulls<String>(a)
                                        var j=0;
                                        for (i in 0 until arrays.length()) {
                                            var `val` = arrays.getJSONObject(i)
                                            if (`val`.getString("key") != "" && `val`.has("value") && `val`.getString("value") != "")
                                            {
                                                keys_temp[j]=`val`.getString("key")
                                                value_temp[j]=`val`.getString("value")
                                                j++
                                            }
                                        }
                                        val keys = arrayOfNulls<String>(j)
                                        val value= arrayOfNulls<String>(j)
                                        System.arraycopy(keys_temp, 0, keys, 0, j)
                                        System.arraycopy(value_temp, 0, value, 0, j)
                                        val name = Preferences.getRegisteredUser(activity)
                                        val i = dbHelper.getCountWidget(name)
                                        val units= dbHelper.getunitWidget(name)
                                        Log.d(tags, " Count Widget : "+ i)
                                        val typess= dbHelper.gettypeWidget(name)
                                        val key = dbHelper.getDataWidget(name);
                                        val names= dbHelper.getNameWidget(name);
                                        for (i in 0 until key.size) {
                                            for (j in 0 until arrays.length())
                                            {
                                                var `val` = arrays.getJSONObject(j)
                                                Log.d(tag,"TYPE: "+typess[i]);
                                                if (`val`.getString("key").equals(key[i]))
                                                {
                                                    val cv =CardView(context)
                                                    val lyts= LinearLayout(context)
                                                    val remove = Button (context)
                                                    lyts.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                    lyts.setOrientation(LinearLayout.VERTICAL);
                                                    val textView = TextView(context)
                                                    textView.setText(names[i])
                                                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                                                    textView.setTextColor(getResources().getColor(R.color.black))
                                                    val params = LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                                    )
                                                    val paramss= LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        200
                                                    )
                                                    val param_map= LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        350
                                                    )
                                                    val lp = RelativeLayout.LayoutParams(
                                                        36,
                                                        36);
                                                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                                    remove.setLayoutParams(lp)
                                                    remove.setBackground(getResources().getDrawable(R.drawable.delete))
                                                    paramss.setMargins(10,5,10,0)
                                                    params.setMargins(10, 5, 0, 0)
                                                    cv.setRadius(9F);
                                                    cv.setContentPadding(15, 15, 15, 15);
                                                    cv.setCardBackgroundColor(getResources().getColor(R.color.gray));
                                                    cv.setMaxCardElevation(15f)
                                                    cv.setCardElevation(9f)
                                                    cv.setLayoutParams(params)
                                                    textView.setGravity(Gravity.CENTER)
                                                    textView.setLayoutParams(params)
                                                    lyts.addView(remove)
                                                    lyts.addView(textView)
                                                    remove.setOnClickListener {
                                                        dbHelper.deleteWidget(name,names[i])
                                                        val bundle = Bundle()
                                                        bundle.putString("dev", data)
                                                        bundle.putString("name", name)
                                                        val frg = fragmentdetildevicebak()
                                                        val ft = fragmentManager!!.beginTransaction()
                                                        frg.setArguments(bundle)
                                                        ft.replace(R.id.main_container, frg, "get")
                                                        ft.addToBackStack(null)
                                                        dbHelper.closeDB()
                                                        ft.commit()

                                                    }
                                                    if (typess[i].equals("Gauge")) {
                                                        val vv = `val`.getString("value").toDouble()
                                                        val gauge = ArcGauge(context)
                                                        val range = Range()
                                                        range.color = Color.parseColor("#ce0000")
                                                        range.from = 0.0
                                                        range.to = 100.0
                                                        gauge.setLayoutParams(paramss)
                                                        gauge.minValue = 0.0
                                                        gauge.maxValue = 100.0
                                                        gauge.value = vv
                                                        gauge.addRange(range)
                                                        lyts.addView(gauge)
                                                        val textValue= TextView(context)
                                                        textValue.setTextColor(getResources().getColor(R.color.black))
                                                        textValue.setLayoutParams(params)
                                                        textValue.setGravity(Gravity.CENTER)
                                                        textValue.setText(key[i] + " : " + `val`.getString("value")+ units[i])
                                                        lyts.addView(textValue)
                                                    }

                                                    else if (typess[i].equals("Text")) {
                                                        val textValue= TextView(context)
                                                        textValue.setTextColor(getResources().getColor(R.color.black))
                                                        textValue.setLayoutParams(params)
                                                        textValue.setGravity(Gravity.CENTER)
                                                        textValue.setText(key[i] + " : " + `val`.getString("value")+ units[i])
                                                        lyts.addView(textValue)
                                                    }

                                                    else if (typess[i].equals("Maps"))
                                                    {
                                                        places=true
                                                        val  googleMapOptions = GoogleMapOptions()
                                                        googleMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                                                            .compassEnabled(false).rotateGesturesEnabled(true)
                                                            .tiltGesturesEnabled(true);
                                                        val  mapView =  MapView(context, googleMapOptions)
                                                        mapView.setLayoutParams(param_map)
                                                        mapView.onCreate(Bundle())
                                                        mapView.onResume()
                                                        lyts.addView(mapView)
                                                        mapView.getMapAsync(this@fragmentdetildevicebak);
                                                    }

                                                    else if (typess[i].equals("Line Graph")) {
                                                        getLine(
                                                            iddevice,
                                                            `val`.getString("key"),
                                                            names[i],
                                                            cv
                                                        )
                                                    }

                                                    else if (typess[i].equals("Bar Graph")) {
                                                    getBar(
                                                        iddevice,
                                                        `val`.getString("key"),
                                                        names[i],
                                                        cv
                                                        )
                                                     }
                                                    else if (typess[i].equals("Tracking"))
                                                    {
                                                       places=false
                                                       getLoc(iddevice,cv)

                                                    }

                                                    cv.addView(lyts)
                                                    ll.addView(cv)
                                                    break


                                                }

                                                 else if (typess[i].equals("Message Counter")) {
                                                    val cv =CardView(context)
                                                    val lyts= LinearLayout(context)
                                                    val remove = Button (context)
                                                    lyts.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                    lyts.setOrientation(LinearLayout.VERTICAL);
                                                    val textView = TextView(context)
                                                    textView.setText(names[i])
                                                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                                                    textView.setTextColor(getResources().getColor(R.color.black))
                                                    val params = LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                                    )
                                                    val paramss= LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        200
                                                    )
                                                    val param_map= LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        350
                                                    )
                                                    val lp = RelativeLayout.LayoutParams(
                                                        36,
                                                        36);
                                                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                                    remove.setLayoutParams(lp)
                                                    remove.setBackground(getResources().getDrawable(R.drawable.delete))
                                                    paramss.setMargins(10,5,10,0)
                                                    params.setMargins(10, 5, 0, 0)
                                                    cv.setRadius(9F);
                                                    cv.setContentPadding(15, 15, 15, 15);
                                                    cv.setCardBackgroundColor(getResources().getColor(R.color.gray));
                                                    cv.setMaxCardElevation(15f)
                                                    cv.setCardElevation(9f)
                                                    cv.setLayoutParams(params)
                                                    textView.setGravity(Gravity.CENTER)
                                                    textView.setLayoutParams(params)
                                                    lyts.addView(remove)
                                                    lyts.addView(textView)
                                                    remove.setOnClickListener {
                                                        dbHelper.deleteWidget(name,names[i])
                                                        val bundle = Bundle()
                                                        bundle.putString("dev", data)
                                                        bundle.putString("name", name)
                                                        val frg = fragmentdetildevicebak()
                                                        val ft = fragmentManager!!.beginTransaction()
                                                        frg.setArguments(bundle)
                                                        ft.replace(R.id.main_container, frg, "get")
                                                        ft.addToBackStack(null)
                                                        dbHelper.closeDB()
                                                        ft.commit()

                                                    }
                                                    getCounter(
                                                        iddevice,
                                                        key[i],
                                                        names[i],
                                                        cv
                                                    )
                                                    cv.addView(lyts)
                                                    ll.addView(cv)
                                                     break
                                                }
                                            }
                                        }
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

    public inline fun String.toDouble(): Double = java.lang.Double.parseDouble(this)

  override fun onResume()
  {
      super.onResume()
  }
    private fun convertDouble(string: Array<String?>): FloatArray {
        val number = FloatArray(string.size)

        for (i in string.indices) {
            number[i] = java.lang.Float.parseFloat(string[i]) // error here
        }
        return number
    }

    private fun convert(string: FloatArray): IntArray {
        val number = IntArray(string.size)

        for (i in string.indices) {
            number[i] = string[i].toInt() // error here
        }
        return number
    }
    private fun getLine(iddevice: String, keys: String, title: String, cv: CardView) {
        activity!!.runOnUiThread {
            try {
                dlg = ProgressDialog(context)
                dlg.setMessage("Sedang Mengambil Data...")
                dlg.setCancelable(false)
                dlg.show()
                val obj = JSONObject()
                val token = Preferences.getRegisteredPass(activity)
                val idd = Preferences.getRegisteredUser(activity)
                val js = dbHelper.getparser(idd, iddevice)
                obj.put("command", "getSomeData")
                obj.put("id_device", iddevice)
                obj.put("js", js)
                obj.put("token_key", token)
                obj.put("key", keys)
                obj.put("length", "50")
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
                            activity!!.runOnUiThread {
                                dlg.dismiss()
                                dialog.setContentView(R.layout.alert_error)
                                val dialogButton =
                                    dialog.findViewById(R.id.dialogButtonOK) as Button
                                val txtContent = dialog.findViewById(R.id.alertcontent) as TextView
                                txtContent.text = "Periksa Kembali Koneksi Internet Anda"
                                dialog.show()
                                dialogButton.setOnClickListener { dialog.dismiss() }
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            activity!!.runOnUiThread {
                                try {
                                    val rsp = response.body()!!.string()
                                    Log.d("tag", rsp)
                                    var result: JSONObject? = null
                                    result = JSONObject(rsp)
                                    if (result.getString("responseCode") == "0") {
                                        dlg.dismiss()
                                        val lyts = LinearLayout(context)
                                        lyts.layoutParams =
                                            LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                            )
                                        lyts.orientation = LinearLayout.VERTICAL
                                        val arrays = result.getJSONArray("data")
                                        Log.d("tag", "cot :" + arrays.length())
                                        val a = arrays.length()
                                        val keys_temp = arrayOfNulls<String>(a)
                                        val value_temp = arrayOfNulls<String>(a)
                                        var q = 0
                                        for (i in 0 until arrays.length()) {
                                            val `val` = arrays.getJSONObject(i)
                                            keys_temp[q] = `val`.getString("time")
                                            value_temp[q] = `val`.getString("value")
                                            q++
                                        }
                                        val lineChartView = LineChartView(context)
                                        val tes = convertDouble(value_temp)
                                        val values = convert(tes)
                                        val yAxisValues = ArrayList<PointValue>()
                                        val axisValues = ArrayList<AxisValue>()
                                        val line = Line(yAxisValues).setColor(Color.parseColor("#311b92"))
                                        line.setHasLabels(true);
                                        line.setHasLabelsOnlyForSelected(true);
                                        for (i in keys_temp.indices) {
                                            axisValues.add(
                                                i,
                                                AxisValue(i.toFloat()).setLabel(keys_temp[i])
                                            )
                                        }
                                        for (i in value_temp.indices) {
                                            yAxisValues.add(
                                                PointValue(
                                                    i.toFloat(),
                                                    values[i].toFloat()
                                                )
                                            )
                                        }
                                        val param_map = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            350
                                        )
                                        val params = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        param_map.setMargins(10,10,10,10)
                                        params.setMargins(10,10,10,10)
                                        val lines = ArrayList<Line>()
                                        lines.add(line)
                                        val data = LineChartData()
                                        data.lines = lines
                                        val axis = Axis()
                                        axis.values = axisValues
                                        axis.textSize = 10
                                        axis.setHasSeparationLine(true)
                                        axis.textColor = Color.parseColor("#03A9F4")
                                        data.axisXBottom = axis
                                        axis.setMaxLabelChars(5)


                                        val yAxis = Axis()

                                        yAxis.name = keys
                                        yAxis.textColor = Color.parseColor("#03A9F4")
                                        yAxis.textSize = 16
                                        yAxis.setHasSeparationLine(true)
                                        data.axisYLeft = yAxis
                                        lineChartView.lineChartData = data
                                        lineChartView.setInteractive(true );
                                        lineChartView.setZoomType(ZoomType.HORIZONTAL);
                                        lineChartView.setPadding(5,5,5,5)
                                        lineChartView.setZoomLevel(100f,0f,500f)
                                        val v = Viewport(0f, 100f, 10f, 0f);


                                        lineChartView.maximumViewport = v

                                        lineChartView.currentViewport = v
                                        cv.layoutParams = params

                                        lineChartView.layoutParams = param_map

                                        cv.setContentPadding(15, 15, 15, 15)
                                        cv.setCardBackgroundColor(resources.getColor(R.color.gray))
                                        cv.maxCardElevation = 15f
                                        cv.cardElevation = 9f

                                        lyts.addView(lineChartView)
                                        cv.addView(lyts)

                                    } else {
                                        Toast.makeText(
                                            context,
                                            result.getString("msg"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        dlg.dismiss()
                                        dialog.setContentView(R.layout.alert_error)
                                        val dialogButton =
                                            dialog.findViewById(R.id.dialogButtonOK) as Button
                                        val txtContent =
                                            dialog.findViewById(R.id.alertcontent) as TextView
                                        txtContent.text = "DEVICE TIDAK DITEMUKAN"
                                        dialog.show()
                                        dialogButton.setOnClickListener { dialog.dismiss() }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    dlg.dismiss()
                                    dialog.setContentView(R.layout.alert_error)
                                    val dialogButton =
                                        dialog.findViewById(R.id.dialogButtonOK) as Button
                                    val txtContent =
                                        dialog.findViewById(R.id.alertcontent) as TextView
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

    private fun getBar(iddevice: String, keys: String, title: String, cv: CardView) {
        activity!!.runOnUiThread {
            try {
               /* dlg = ProgressDialog(context)
                dlg.setMessage("Sedang Mengambil Data...")
                dlg.setCancelable(true)
                dlg.show()*/
                val obj = JSONObject()
                val token = Preferences.getRegisteredPass(activity)
                val idd = Preferences.getRegisteredUser(activity)
                val js = dbHelper.getparser(idd, iddevice)
                obj.put("command", "getSomeData")
                obj.put("id_device", iddevice)
                obj.put("js", js)
                obj.put("token_key", token)
                obj.put("key", keys)
                obj.put("length", "50")
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
                            activity!!.runOnUiThread {
                               // dlg.dismiss()
                                dialog.setContentView(R.layout.alert_error)
                                val dialogButton =
                                    dialog.findViewById(R.id.dialogButtonOK) as Button
                                val txtContent = dialog.findViewById(R.id.alertcontent) as TextView
                                txtContent.text = "Periksa Kembali Koneksi Internet Anda"
                                dialog.show()
                                dialogButton.setOnClickListener { dialog.dismiss() }
                            }
                        }

                       override fun onResponse(call: Call, response: Response) {
                            activity!!.runOnUiThread {
                                try {
                                    val rsp = response.body()!!.string()
                                    Log.d("tag", rsp)
                                    var result: JSONObject? = null
                                    result = JSONObject(rsp)
                                    if (result.getString("responseCode") == "0") {
                                       // dlg.dismiss()
                                        val lyts = LinearLayout(context)
                                        lyts.layoutParams =
                                            LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                            )
                                        lyts.orientation = LinearLayout.VERTICAL
                                        val param_map = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            450
                                        )
                                        val arrays = result.getJSONArray("data")
                                        Log.d("tag", "cot :" + arrays.length())
                                        val a = arrays.length()
                                        val keys_temp = arrayOfNulls<String>(a)
                                        val value_temp = arrayOfNulls<String>(a)
                                        var q = 0
                                        for (i in 0 until arrays.length()) {
                                            val `val` = arrays.getJSONObject(i)
                                            keys_temp[q] = `val`.getString("time")
                                            value_temp[q] = `val`.getString("value")
                                            q++
                                        }
                                        val lineChartView = ColumnChartView(context)
                                        val tes = convertDouble(value_temp)
                                        val values = convert(tes)
                                        val columns = ArrayList<Column>()
                                        var valss= ArrayList<SubcolumnValue>()
                                        var valsss= ArrayList<AxisValue>()
                                        val numSubcolumns = 1
                                        val axisValues = ArrayList<AxisValue>()
                                       for ( i in 0  until q ) {

                                                valss= ArrayList<SubcolumnValue>()
                                                for (j in 0 until numSubcolumns) {
                                                    valss.add(SubcolumnValue(value_temp[i]!!.toFloat(), ChartUtils.pickColor()))
                                                }
                                           axisValues.add(
                                               i,
                                               AxisValue(i.toFloat()).setLabel(keys_temp[i])
                                           )
                                                val column = Column(valss);
                                                column.setHasLabels(true);
                                                column.setHasLabelsOnlyForSelected(false);
                                                columns.add(column);
                                        }

                                        var data = ColumnChartData(columns)
                                        val xAxis = Axis()

                                        val params = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        val yAxis = Axis().setHasLines(true)
                                        xAxis.setName(keys);
                                        xAxis.setValues(axisValues)
                                        data.setAxisXBottom(xAxis);
                                        data.setAxisYLeft(yAxis);
                                        lineChartView .setColumnChartData(data);
                                        lineChartView.layoutParams = param_map
                                        lineChartView.setInteractive(true );
                                        lineChartView.setZoomType(ZoomType.HORIZONTAL);
                                        lineChartView.setPadding(5,5,5,5)
                                        val v = Viewport(1f, 100f, 6f, 0f);
                                        lineChartView.maximumViewport = v
                                        param_map.setMargins(10,10,10,10)
                                        params.setMargins(10,10,10,10)
                                        lineChartView.currentViewport = v
                                        cv.setContentPadding(15, 15, 15, 15)
                                        cv.setCardBackgroundColor(resources.getColor(R.color.gray))
                                        cv.maxCardElevation = 15f
                                        cv.cardElevation = 9f
                                        cv.layoutParams = params
                                        lyts.addView(lineChartView)
                                        cv.addView(lyts)

                                    } else {
                                        Toast.makeText(
                                            context,
                                            result.getString("msg"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                       // dlg.dismiss()
                                        dialog.setContentView(R.layout.alert_error)
                                        val dialogButton =
                                            dialog.findViewById(R.id.dialogButtonOK) as Button
                                        val txtContent =
                                            dialog.findViewById(R.id.alertcontent) as TextView
                                        txtContent.text = "DEVICE TIDAK DITEMUKAN"
                                        dialog.show()
                                        dialogButton.setOnClickListener { dialog.dismiss() }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    //dlg.dismiss()
                                    dialog.setContentView(R.layout.alert_error)
                                    val dialogButton =
                                        dialog.findViewById(R.id.dialogButtonOK) as Button
                                    val txtContent =
                                        dialog.findViewById(R.id.alertcontent) as TextView
                                    txtContent.text = "NOT JSON FORMAT"
                                    dialog.show()
                                    dialogButton.setOnClickListener { dialog.dismiss() }
                                }
                            }

                        }
                    })
            } catch (ex: Exception) {
                ex.printStackTrace()
               // dlg.dismiss()
                Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCounter(iddevice: String, keys: String, title: String, cv: CardView) {
        activity!!.runOnUiThread {
            try {
                /*dlg = ProgressDialog(context)
                dlg.setMessage("Sedang Mengambil Data...")
                dlg.setCancelable(false)
                dlg.show()*/
                val obj = JSONObject()
                val token = Preferences.getRegisteredPass(activity)
                val idd = Preferences.getRegisteredUser(activity)
                val js = dbHelper.getparser(idd, iddevice)
                obj.put("command", "getCount")
                obj.put("id_device", iddevice)
                obj.put("token_key", token)
                obj.put("type", keys)
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
                            activity!!.runOnUiThread {
                               // dlg.dismiss()
                                dialog.setContentView(R.layout.alert_error)
                                val dialogButton =
                                    dialog.findViewById(R.id.dialogButtonOK) as Button
                                val txtContent = dialog.findViewById(R.id.alertcontent) as TextView
                                txtContent.text = "Periksa Kembali Koneksi Internet Anda"
                                dialog.show()
                                dialogButton.setOnClickListener { dialog.dismiss() }
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            activity!!.runOnUiThread {
                                try {
                                    val rsp = response.body()!!.string()
                                    Log.d("tag", rsp)
                                    var result: JSONObject? = null
                                    result = JSONObject(rsp)
                                    if (result.getString("responseCode") == "0") {
                                       // dlg.dismiss()
                                        val lyts = LinearLayout(context)
                                        lyts.layoutParams =
                                            LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                            )
                                        lyts.orientation = LinearLayout.VERTICAL
                                        val param_map = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            450
                                        )
                                        val arrays = result.getJSONArray("data")
                                        Log.d("tag", "cot :" + arrays.length())
                                        val a = arrays.length()
                                        val keys_temp = arrayOfNulls<String>(a)
                                        val value_temp = arrayOfNulls<String>(a)
                                        var q = 0
                                        for (i in 0 until arrays.length()) {
                                            val `val` = arrays.getJSONObject(i)
                                            keys_temp[q] = `val`.getString("time")
                                            value_temp[q] = `val`.getString("count")
                                            q++
                                        }
                                        val lineChartView = ColumnChartView(context)
                                        val tes = convertDouble(value_temp)
                                        val values = convert(tes)
                                        val columns = ArrayList<Column>()
                                        var valss= ArrayList<SubcolumnValue>()
                                        var valsss= ArrayList<AxisValue>()
                                        val numSubcolumns = 1
                                        val axisValues = ArrayList<AxisValue>()
                                        for ( i in 0  until q ) {

                                            valss= ArrayList<SubcolumnValue>()
                                            for (j in 0 until numSubcolumns) {
                                                valss.add(SubcolumnValue(value_temp[i]!!.toFloat(), ChartUtils.pickColor()))
                                            }

                                            val column = Column(valss);
                                            column.setHasLabels(true);
                                            column.setHasLabelsOnlyForSelected(false);
                                            columns.add(column);
                                        }

                                        var data = ColumnChartData(columns)
                                        val xAxis = Axis()
                                        for (i in keys_temp.indices) {
                                            axisValues.add(
                                                i,
                                                AxisValue(i.toFloat()).setLabel(keys_temp[i])
                                            )
                                        }

                                        val yAxis = Axis().setHasLines(true)
                                        xAxis.setName(keys);
                                        xAxis.setValues(axisValues)
                                        data.setAxisXBottom(xAxis);
                                        data.setAxisYLeft(yAxis);
                                        lineChartView .setColumnChartData(data);
                                        lineChartView.layoutParams = param_map
                                        lineChartView.setInteractive(true );
                                        lineChartView.setZoomType(ZoomType.HORIZONTAL);
                                        lineChartView.setPadding(5,5,5,5)
                                        val v = Viewport(0f, 100f, 6f, 0f);
                                        lineChartView.maximumViewport = v
                                        lineChartView.currentViewport = v
                                        cv.setContentPadding(15, 15, 15, 15)
                                        cv.setCardBackgroundColor(resources.getColor(R.color.gray))
                                        cv.maxCardElevation = 15f
                                        cv.cardElevation = 9f

                                        lyts.addView(lineChartView)
                                        cv.addView(lyts)

                                    } else {
                                        Toast.makeText(
                                            context,
                                            result.getString("msg"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                       // dlg.dismiss()
                                        dialog.setContentView(R.layout.alert_error)
                                        val dialogButton =
                                            dialog.findViewById(R.id.dialogButtonOK) as Button
                                        val txtContent =
                                            dialog.findViewById(R.id.alertcontent) as TextView
                                        txtContent.text = "DEVICE TIDAK DITEMUKAN"
                                        dialog.show()
                                        dialogButton.setOnClickListener { dialog.dismiss() }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                   // dlg.dismiss()
                                    dialog.setContentView(R.layout.alert_error)
                                    val dialogButton =
                                        dialog.findViewById(R.id.dialogButtonOK) as Button
                                    val txtContent =
                                        dialog.findViewById(R.id.alertcontent) as TextView
                                    txtContent.text = "NOT JSON FORMAT"
                                    dialog.show()
                                    dialogButton.setOnClickListener { dialog.dismiss() }
                                }
                            }

                        }
                    })
            } catch (ex: Exception) {
                ex.printStackTrace()
                //dlg.dismiss()
                Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getLoc(iddevice: String, cv: CardView) {
        activity!!.runOnUiThread {
            try {
                /*dlg = ProgressDialog(context)
                dlg.setMessage("Sedang Mengambil Data...")
                dlg.setCancelable(false)
                dlg.show()*/
                val obj = JSONObject()
                val token = Preferences.getRegisteredPass(activity)
                val idd = Preferences.getRegisteredUser(activity)
                val js = dbHelper.getparser(idd, iddevice)
                obj.put("command", "getTracking")
                obj.put("id_device", iddevice)
               // obj.put("js", js)
                obj.put("token_key", token)
                //obj.put("key", keys)
                //obj.put("length", "50")
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
                            activity!!.runOnUiThread {
                               // dlg.dismiss()
                                dialog.setContentView(R.layout.alert_error)
                                val dialogButton =
                                    dialog.findViewById(R.id.dialogButtonOK) as Button
                                val txtContent = dialog.findViewById(R.id.alertcontent) as TextView
                                txtContent.text = "Periksa Kembali Koneksi Internet Anda"
                                dialog.show()
                                dialogButton.setOnClickListener { dialog.dismiss() }
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            activity!!.runOnUiThread {
                                try {
                                    val rsp = response.body()!!.string()
                                    Log.d("tag", rsp)
                                    var result: JSONObject? = null
                                    result = JSONObject(rsp)
                                    if (result.getString("responseCode") == "0") {
                                       // dlg.dismiss()
                                        val lyts = LinearLayout(context)
                                        lyts.layoutParams =
                                            LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                            )
                                        lyts.orientation = LinearLayout.VERTICAL
                                        val arrays = result.getJSONArray("data")
                                        Log.d("tag", "cot :" + arrays.length())
                                        val a = arrays.length()
                                        val lat_temp = arrayOfNulls<String>(a)
                                        val lng_temp = arrayOfNulls<String>(a)
                                        val time_temp = arrayOfNulls<String>(a)
                                        var q = 0
                                        var polyline: LatLng
                                        for (i in 0 until arrays.length()) {
                                            val `val` = arrays.getJSONObject(i)
                                            //lat_temp[q] = `val`.getString("lat")
                                            //lng_temp[q] = `val`.getString("longitude")
                                            time_temp[q]= `val`.getString("time")
                                            polyline = LatLng(
                                                java.lang.Double.valueOf(`val`.getString("lat")),
                                                java.lang.Double.valueOf(`val`.getString("longitude"))
                                            )

                                            line.add(polyline)
                                            q++
                                        }




                                        val param_map = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            350
                                        )
                                        val params = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            500
                                        )



                                        val  googleMapOptions = GoogleMapOptions()
                                        googleMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                                            .compassEnabled(true).rotateGesturesEnabled(true)
                                            .tiltGesturesEnabled(true);
                                        val  mapView =  MapView(context, googleMapOptions)
                                        mapView.setLayoutParams(param_map)
                                        mapView.onCreate(Bundle())
                                        mapView.onResume()
                                        param_map.setMargins(10,100,10,10)
                                        params.setMargins(10,10,10,10)
                                        cv.layoutParams = params
                                        cv.setContentPadding(15, 15, 15, 15)
                                        cv.setCardBackgroundColor(resources.getColor(R.color.gray))
                                        cv.maxCardElevation = 15f
                                        cv.cardElevation = 9f
                                        lyts.addView(mapView)
                                        cv.addView(lyts)
                                        mapView.getMapAsync(OnMapReadyCallback() {
                                               fun onMapReady( map: GoogleMap) {
                                                  setupMap(map)
                                               }
                                         });

                                    } else {
                                        Toast.makeText(
                                            context,
                                            result.getString("msg"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                       // dlg.dismiss()
                                        dialog.setContentView(R.layout.alert_error)
                                        val dialogButton =
                                            dialog.findViewById(R.id.dialogButtonOK) as Button
                                        val txtContent =
                                            dialog.findViewById(R.id.alertcontent) as TextView
                                        txtContent.text = "DEVICE TIDAK DITEMUKAN"
                                        dialog.show()
                                        dialogButton.setOnClickListener { dialog.dismiss() }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                   // dlg.dismiss()
                                    dialog.setContentView(R.layout.alert_error)
                                    val dialogButton =
                                        dialog.findViewById(R.id.dialogButtonOK) as Button
                                    val txtContent =
                                        dialog.findViewById(R.id.alertcontent) as TextView
                                    txtContent.text = "NOT JSON FORMAT"
                                    dialog.show()
                                    dialogButton.setOnClickListener { dialog.dismiss() }
                                }
                            }

                        }
                    })
            } catch (ex: Exception) {
                ex.printStackTrace()
               // dlg.dismiss()
                Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun setupMap(map : GoogleMap?)
    {
        Log.d(tag,"PEEPEPE")
        val latitude = 0;   // set this
        val longitude = 0;  // set this too
        val  zoom = 17f;      // or whatever, between 2 and 21 (approx)
        map!!.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map!!.addPolyline(PolylineOptions().addAll(line).width(2.0f).color(Color.RED))
        for (i in 0 until line.size) {
            map.addMarker(MarkerOptions().position(line[i]).title(data))
        }


    }






}

