package id.co.inti.pandawa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import id.co.inti.pandawa.dbhelper.DBHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class fragment_alert  extends Fragment
{
    private View rootView;
    private Dialog dialog;
    private ProgressDialog dlg;
    Context context;
    ArrayAdapter<String> adap_dev,adap_key;
    private Button save;
    private Spinner sp_dev,sp_key;
    private TextView name, min, max,msg;
    private String devs;
    private final String tag="DEBUG";
    DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alert, container, false);
        context= getActivity();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        name= rootView.findViewById(R.id.txt_name);
        min= rootView.findViewById(R.id.txt_min);
        max= rootView.findViewById(R.id.txt_max);
        msg= rootView.findViewById(R.id.txt_msg);
        sp_dev=rootView.findViewById(R.id.sp_device);
        sp_key=rootView.findViewById(R.id.sp_key);
        save= rootView.findViewById(R.id.btn_save);
        Bundle args = getArguments();
        if(args!=null)
        {
            devs = getArguments().getString("dev");
            Log.d("tags","TES"+devs);

        }

        dialog= new Dialog(context);
        String[] values= new String[1];
        values[0]= devs;
        dbHelper = new DBHelper(context);
        dbHelper.openDB();
        adap_dev = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, values);
        adap_dev.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_dev.setAdapter(adap_dev);
        sp_dev.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                getdata(sp_dev.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = false;
                View focusView = null;
                name.setError(null);
                msg.setError(null);
                if (TextUtils.isEmpty(name.getText().toString()))
                {
                    name.setError("Wajib Diisi");
                    focusView = name;
                    Toast.makeText(getActivity(), "Silahkan Isi Nama Alert", Toast.LENGTH_SHORT).show();
                    cancel = true;
                }
                else if (TextUtils.isEmpty(msg.getText().toString()))
                {
                    msg.setError("Wajib Diisi");
                    focusView = msg;
                    Toast.makeText(getActivity(), "Silahkan Isi Custom Message", Toast.LENGTH_SHORT).show();
                    cancel = true;
                }
                else if (TextUtils.isEmpty(min.getText().toString())&& TextUtils.isEmpty(max.getText().toString()))
                {
                    Toast.makeText(getActivity(), "Silahkan Isi Value", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    insert();
                }

            }
        });
        return  rootView;
    }

    private void insert(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    dlg = new ProgressDialog(context);
                    dlg.setMessage("Sedang Mengambil Data...");
                    dlg.setCancelable(false);
                    dlg.show();
                    JSONObject obj = new JSONObject();
                    String name =Preferences.getRegisteredUser(getActivity());
                    String token= Preferences.getRegisteredPass(getActivity());
                    obj.put("command", "insertAlert");
                    obj.put("id_device", devs);
                    obj.put("id_connector","1");
                    obj.put("data_key",sp_key.getSelectedItem().toString());
                    obj.put("min_value",min.getText().toString());
                    obj.put("max_value",max.getText().toString());
                    obj.put("message",msg.getText().toString());
                    obj.put("token_key",token);
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
                                                    Fragment frg = new fragmentListDevices();
                                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                    ft.replace(R.id.main_container, frg, "get");
                                                    ft.addToBackStack(null);
                                                    dbHelper.closeDB();
                                                    ft.commit();

                                                }
                                                else
                                                {
                                                    Toast.makeText(context, result.getString("msg"), Toast.LENGTH_SHORT).show();
                                                    dlg.dismiss();
                                                    dialog.setContentView(R.layout.alert_error);
                                                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                    TextView txtContent = (TextView) dialog.findViewById(R.id.alertcontent);
                                                    txtContent.setText("GAGAL INSERT ALERT");
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

    private void getdata(final String ids)
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
                    String name =Preferences.getRegisteredUser(getActivity());
                    String parser = dbHelper.getparser(name,sp_dev.getSelectedItem().toString());
                    Log.d("tags","Parser :"+ parser);
                    String token= Preferences.getRegisteredPass(getActivity());
                    obj.put("command", "getData");
                    obj.put("id_device", ids);
                    obj.put("js",parser);
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
                                                    int a= arrays.length();

                                                    String [] devs= new String[a] ;
                                                    int j=0;
                                                    for (int i =0; i<a;i++)
                                                    {
                                                        JSONObject val = arrays.getJSONObject(i);
                                                        if (val.getString("key") != "" && val.has("value") && val.getString("value") != "")
                                                        {
                                                            devs[j]=val.getString("key");
                                                            j++;
                                                        }
                                                    }
                                                    Log.d("tag","Length Array "+j);
                                                    String [] keys= new String[j] ;
                                                    System.arraycopy(devs,0,keys,0,j);
                                                    adap_key = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, keys);
                                                    adap_key.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    sp_key.setAdapter(adap_key);


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
