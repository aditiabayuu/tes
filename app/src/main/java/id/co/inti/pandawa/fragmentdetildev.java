package id.co.inti.pandawa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import id.co.inti.pandawa.dbhelper.DBHelper;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class fragmentdetildev extends Fragment implements OnMapReadyCallback{
    private View rootView;
    private ImageButton img_add ;

    private String data, name;
    private EditText devname;
    private Spinner sp,sp_parser;
    String[] txt_sp;
    private Button save;
    ArrayAdapter<String> adap_sp,adap_parser;
    DBHelper dbHelper;
    private Dialog dialog;
    private String lats, longitude;
    private ProgressDialog dlg;
    private LinearLayout ll;
    private String tag ="DEBUG";
    Context context;
    private String [] names;
    private TextView adds;
    private GoogleMap mMap;
    private int loop=0;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.detil_devices_bak, container, false);
        img_add= rootView.findViewById(R.id.add_dev);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ll=rootView.findViewById(R.id.lytss);
        context= getActivity();
        dialog= new Dialog(context);
        dbHelper = new DBHelper(context);
        dbHelper.openDB();
        Bundle bundle = getArguments();
        if (bundle != null) {
            data = bundle.getString("dev");
            Log.d(tag, data);
            getData(data);
        }
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frg = new fragment_add_widget();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_container, frg, "get");
                ft.addToBackStack(null);
                dbHelper.closeDB();
                ft.commit()  ;
            }
        });
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
                // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.parseDouble(lats), Double.parseDouble(longitude));
        mMap.addMarker(new MarkerOptions().position(sydney).title(data));
        float zoomLevel = 16.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,zoomLevel));
    }

    private void getData(final String iddevice)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    dlg = new ProgressDialog(context);
                    dlg.setMessage("Sedang Mengambil Data...");
                    dlg.setCancelable(false);
                    dlg.show();
                    JSONObject obj = new JSONObject();
                    String token= Preferences.getRegisteredPass(getActivity());
                    String idd= Preferences.getRegisteredUser(getActivity());
                    String js = dbHelper.getparser(idd,iddevice);
                    obj.put("command", "getData");
                    obj.put("id_device", iddevice);
                    obj.put("js", js);
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(200, TimeUnit.SECONDS)
                            .writeTimeout(200, TimeUnit.SECONDS)
                            .readTimeout(200, TimeUnit.SECONDS).build();
                    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, obj.toString());
                    Log.d("tag:", obj.toString());
                    Request request = new Request.Builder()
                            .url("http://tms.inti.co.id:7002")
                            .post(body)
                            .build();

                    client.newCall(request)
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(final Call call, IOException e) {
                                    // Error

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dlg.dismiss();
                                            dialog.setContentView(R.layout.alert_error);
                                            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                            TextView txtContent = (TextView) dialog.findViewById(R.id.alertcontent);
                                            txtContent.setText("Periksa Kembali Koneksi Internet Anda");
                                            dialog.show();
                                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String rsp = response.body().string();
                                                Log.d("tag", rsp);
                                                JSONObject result = null;
                                                result = new JSONObject(rsp);
                                                if (result.getString("responseCode").equals("0")) {
                                                    dlg.dismiss();
                                                    JSONArray arrays = result.getJSONArray("data");
                                                    lats= result.getString("latitude");
                                                    longitude= result.getString("longitude");
                                                    Log.d("tag", "cot :" + arrays.length());
                                                    int a = arrays.length();
                                                    String [] keys_temp =new String[a];
                                                    String[]  value_temp = new String[a];
                                                    int q=0;
                                                    for (int i =0;i<arrays.length();i++) {
                                                        JSONObject val = arrays.getJSONObject(i);
                                                        if (val.getString("key") != "" && val.has("value") && val.getString("value") != "")
                                                        {
                                                            keys_temp[q]=val.getString("key");
                                                            value_temp[q]=val.getString("value");
                                                            a++;
                                                        }
                                                    }
                                                    String [] keys = new String[q];
                                                    String [] value= new String[q];
                                                    System.arraycopy(keys_temp, 0, keys, 0, q);
                                                    System.arraycopy(value_temp, 0, value, 0, q);
                                                    name = Preferences.getRegisteredUser(getActivity());
                                                    int lgt = dbHelper.getCountWidget(name);
                                                    String[] units= dbHelper.getunitWidget(name);
                                                    String [] typess= dbHelper.gettypeWidget(name);
                                                    String [] key = dbHelper.getDataWidget(name);
                                                    names= new String[lgt];
                                                    names= dbHelper.getNameWidget(name);
                                                    for (loop = 0; loop<key.length; loop++){
                                                        for (int j = 0;j< arrays.length();j++)
                                                        {
                                                            final JSONObject val = arrays.getJSONObject(j);
                                                            if (val.getString("key").equals(key[loop])) {
                                                                CardView cv = new CardView(context);
                                                                LinearLayout lyts = new  LinearLayout(context);
                                                                Button remove = new Button(context);
                                                                lyts.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                                lyts.setOrientation(LinearLayout.VERTICAL);
                                                                TextView textView = new TextView(context);
                                                                textView.setText(names[loop]);
                                                                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                                                                textView.setTextColor(getResources().getColor(R.color.black));
                                                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                                                );
                                                                LinearLayout.LayoutParams paramss =  new LinearLayout.LayoutParams(
                                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                                        200
                                                                );
                                                                LinearLayout.LayoutParams param_map = new LinearLayout.LayoutParams(
                                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                                        350
                                                                );
                                                                RelativeLayout.LayoutParams lp = new  RelativeLayout.LayoutParams(
                                                                        36,
                                                                        36);
                                                                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                                                remove.setLayoutParams(lp);
                                                                remove.setBackground(getResources().getDrawable(R.drawable.delete));
                                                                paramss.setMargins(10, 5, 10, 0);
                                                                params.setMargins(10, 5, 0, 0);
                                                                cv.setRadius(9F);
                                                                cv.setContentPadding(15, 15, 15, 15);
                                                                cv.setCardBackgroundColor(getResources().getColor(R.color.gray));
                                                                cv.setMaxCardElevation(15f);
                                                                cv.setCardElevation(9f);
                                                                cv.setLayoutParams(params);
                                                                textView.setGravity(Gravity.CENTER);
                                                                textView.setLayoutParams(params);
                                                                lyts.addView(remove);
                                                                lyts.addView(textView);
                                                                remove.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                     Log.d(tag,names[loop-1]);
                                                                     /* dbHelper.deleteWidget(name, names[loop]);
                                                                      Bundle bundle = new  Bundle();
                                                                      bundle.putString("dev", data);
                                                                      bundle.putString("name", name);
                                                                      Fragment frg = new fragmentdetildevicebak();
                                                                      FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                                      frg.setArguments(bundle);
                                                                      ft.replace(R.id.main_container, frg, "get");
                                                                      ft.addToBackStack(null);
                                                                      dbHelper.closeDB();
                                                                      ft.commit();*/
                                                                            }
                                                                });
                                                                if (typess[loop].equals("Gauge")) {
                                                                    Double vv = Double.parseDouble(val.getString("value"));
                                                                    ArcGauge gauge = new ArcGauge(context);
                                                                    Range range =new  Range();
                                                                    range.setColor(Color.parseColor("#ce0000"));
                                                                    range.setFrom(0.0);
                                                                    range.setTo(100.0);
                                                                    gauge.setLayoutParams(paramss);
                                                                    gauge.setMinValue(0.0);
                                                                    gauge.setMaxValue (100.0);
                                                                    gauge.setValue(vv);
                                                                    gauge.addRange(range);
                                                                    lyts.addView(gauge);
                                                                    TextView textValue = new TextView(context);
                                                                    textValue.setTextColor(getResources().getColor(R.color.black));
                                                                    textValue.setLayoutParams(params);
                                                                    textValue.setGravity(Gravity.CENTER);
                                                                    textValue.setText(key[loop] + " : " + val.getString("value") + units[loop]);
                                                                    lyts.addView(textValue);
                                                                }

                                                                if (typess[loop].equals("Text")) {
                                                                    TextView textValue = new TextView(context);
                                                                    textValue.setTextColor(getResources().getColor(R.color.black));
                                                                    textValue.setLayoutParams(params);
                                                                    textValue.setGravity(Gravity.CENTER);
                                                                    textValue.setText(key[loop] + " : " + val.getString("value") + units[loop]);
                                                                    lyts.addView(textValue);
                                                                }

                                                                if (typess[loop].equals("Maps")) {
                                                                    GoogleMapOptions googleMapOptions =  new GoogleMapOptions();
                                                                    googleMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                                                                            .compassEnabled(false).rotateGesturesEnabled(true)
                                                                            .tiltGesturesEnabled(true);
                                                                    MapView mapView = new MapView(context, googleMapOptions);
                                                                    mapView.setLayoutParams(param_map);
                                                                    mapView.onCreate(new Bundle());
                                                                    mapView.onResume();
                                                                    lyts.addView(mapView);
                                                                    mapView.getMapAsync(fragmentdetildev.this);
                                                                }

                                                                if (typess[loop].equals("Line Graph")) {
                                                                    getBar(iddevice,val.getString("key"),names[loop],cv);
                                                                }
                                                                cv.addView(lyts);
                                                                ll.addView(cv);
                                                                break;

                                                            }
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    Toast.makeText(context, result.getString("msg"), Toast.LENGTH_SHORT).show();
                                                    dlg.dismiss();
                                                    dialog.setContentView(R.layout.alert_error);
                                                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                    TextView txtContent = (TextView) dialog.findViewById(R.id.alertcontent);
                                                    txtContent.setText("DEVICE TIDAK DITEMUKAN");
                                                    dialog.show();
                                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                dlg.dismiss();
                                                dialog.setContentView(R.layout.alert_error);
                                                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                TextView txtContent = (TextView) dialog.findViewById(R.id.alertcontent);
                                                txtContent.setText("NOT JSON FORMAT");
                                                dialog.show();
                                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }
                            });
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    dlg.dismiss();
                    Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private float[] convertDouble(String[] string) {
        float number[] = new float[string.length];

        for (int i = 0; i < string.length; i++) {
            number[i] = Float.parseFloat(string[i]); // error here
        }
        return number;
    }

    private int[] convert(float[] string) {
        int number[] = new int[string.length];

        for (int i = 0; i < string.length; i++) {
            number[i] = (int)(string[i]); // error here
        }
        return number;
    }
    private void getBar(final  String iddevice, final String keys, final String title, final CardView cv)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    dlg = new ProgressDialog(context);
                    dlg.setMessage("Sedang Mengambil Data...");
                    dlg.setCancelable(false);
                    dlg.show();
                    JSONObject obj = new JSONObject();
                    String token= Preferences.getRegisteredPass(getActivity());
                    String idd= Preferences.getRegisteredUser(getActivity());
                    String js = dbHelper.getparser(idd,iddevice);
                    obj.put("command", "getSomeData");
                    obj.put("id_device", iddevice);
                    obj.put("js", js);
                    obj.put("token_key",token);
                    obj.put("key",keys);
                    obj.put("length","50");
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(200, TimeUnit.SECONDS)
                            .writeTimeout(200, TimeUnit.SECONDS)
                            .readTimeout(200, TimeUnit.SECONDS).build();
                    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, obj.toString());
                    Log.d("tag:", obj.toString());
                    Request request = new Request.Builder()
                            .url("http://tms.inti.co.id:7002")
                            .post(body)
                            .build();
                    client.newCall(request)
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(final Call call, IOException e) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dlg.dismiss();
                                            dialog.setContentView(R.layout.alert_error);
                                            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                            TextView txtContent = (TextView) dialog.findViewById(R.id.alertcontent);
                                            txtContent.setText("Periksa Kembali Koneksi Internet Anda");
                                            dialog.show();
                                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                }
                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String rsp = response.body().string();
                                                Log.d("tag", rsp);
                                                JSONObject result = null;
                                                result = new JSONObject(rsp);
                                                if (result.getString("responseCode").equals("0")) {
                                                    dlg.dismiss();
                                                    LinearLayout lyts = new  LinearLayout(context);
                                                    lyts.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                    lyts.setOrientation(LinearLayout.VERTICAL);
                                                    JSONArray arrays = result.getJSONArray("data");
                                                    Log.d("tag", "cot :" + arrays.length());
                                                    int a = arrays.length();
                                                    String [] keys_temp =new String[a];
                                                    String[]  value_temp = new String[a];
                                                    int q=0;
                                                    for (int i =0;i<arrays.length();i++) {
                                                        JSONObject val = arrays.getJSONObject(i);
                                                        keys_temp[q]=val.getString("time");
                                                        value_temp[q]=val.getString("value");
                                                        q++;
                                                    }
                                                    LineChartView lineChartView = new LineChartView(context);
                                                    float []tes= convertDouble(value_temp);
                                                    int [] values=convert(tes);
                                                    List yAxisValues = new ArrayList();
                                                    List axisValues = new ArrayList();
                                                    Line line = new Line(yAxisValues).setColor(Color.parseColor("#311b92"));
                                                    for (int i = 0; i < keys_temp.length; i++) {
                                                        axisValues.add(i, new AxisValue(i).setLabel(keys_temp[i]));
                                                    }
                                                    for (int i = 0; i < value_temp.length; i++) {
                                                        yAxisValues.add(new PointValue(i, values[i]));
                                                    }
                                                    LinearLayout.LayoutParams param_map = new LinearLayout.LayoutParams(
                                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                                            350
                                                    );
                                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                                    );
                                                    List lines = new ArrayList();
                                                    lines.add(line);
                                                    LineChartData data = new LineChartData();
                                                    data.setLines(lines);
                                                    Axis axis = new Axis();
                                                    axis.setValues(axisValues);
                                                    axis.setTextSize(16);
                                                    axis.setTextColor(Color.parseColor("#03A9F4"));
                                                    data.setAxisXBottom(axis);
                                                    Axis yAxis = new Axis();
                                                    yAxis.setName(keys);
                                                    yAxis.setTextColor(Color.parseColor("#03A9F4"));
                                                    yAxis.setTextSize(16);
                                                    data.setAxisYLeft(yAxis);
                                                    lineChartView.setLineChartData(data);
                                                    Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
                                                    viewport.top = 110;
                                                    lineChartView.setMaximumViewport(viewport);
                                                    lineChartView.setCurrentViewport(viewport);
                                                    cv.setLayoutParams(params);
                                                    lineChartView.setHorizontalScrollBarEnabled(true);
                                                    lineChartView.setLayoutParams(param_map);
                                                    cv.setContentPadding(15, 15, 15, 15);
                                                    cv.setCardBackgroundColor(getResources().getColor(R.color.gray));
                                                    cv.setMaxCardElevation(15f);
                                                    cv.setCardElevation(9f);

                                                    lyts.addView(lineChartView);
                                                    cv.addView(lyts);

                                                }
                                                else
                                                {
                                                    Toast.makeText(context, result.getString("msg"), Toast.LENGTH_SHORT).show();
                                                    dlg.dismiss();
                                                    dialog.setContentView(R.layout.alert_error);
                                                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                    TextView txtContent = (TextView) dialog.findViewById(R.id.alertcontent);
                                                    txtContent.setText("DEVICE TIDAK DITEMUKAN");
                                                    dialog.show();
                                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                dlg.dismiss();
                                                dialog.setContentView(R.layout.alert_error);
                                                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                TextView txtContent = (TextView) dialog.findViewById(R.id.alertcontent);
                                                txtContent.setText("NOT JSON FORMAT");
                                                dialog.show();
                                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }
                            });
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    dlg.dismiss();
                    Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
