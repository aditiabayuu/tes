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
import android.widget.*;
import id.co.inti.pandawa.dbhelper.DBHelper;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class fragment_add_widget extends Fragment {
    private View rootView;
    private String data;
    private EditText name,desc,unit;
    private Spinner sp_dev,sp_key,sp_type;
    String[] txt_sp;
    private Button save;
    ArrayAdapter<String> adap_dev,adap_key,adap_type;
    private String [] types = {"Gauge","Line Graph","Bar Graph","Text","Maps","Tracking","Message Counter","Table"};
    private String [] times = {"hour","day","week","month"};
    DBHelper dbHelper;
    private Dialog dialog;
    private ProgressDialog dlg;
    Context context;
    private  String devs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.widget, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        name= rootView.findViewById(R.id.txt_name);
        desc=rootView.findViewById(R.id.txt_description);
        unit = rootView.findViewById(R.id.txt_unit);
        sp_dev=rootView.findViewById(R.id.sp_device);
        sp_type=rootView.findViewById(R.id.sp_type);
        sp_key= rootView.findViewById(R.id.sp_key);
        save= rootView.findViewById(R.id.btn_save);
        context= getActivity();
        Bundle args = getArguments();
        if(args!=null)
        {
            devs = getArguments().getString("dev");
            Log.d("tags","TES"+devs);

        }
        dialog= new Dialog(context);
        dbHelper = new DBHelper(context);
        dbHelper.openDB();
        adap_type = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, types);
        adap_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_type.setAdapter(adap_type);
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                if (sp_type.getSelectedItem().toString().equals("Maps"))
                {
                    sp_key.setEnabled(false);
                    unit.setEnabled(false);
                }
                else if (sp_type.getSelectedItem().toString().equals("Message Counter"))
                {
                    sp_key.setEnabled(true);
                    unit.setEnabled(true);
                    adap_key = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, times);
                    adap_key.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_key.setAdapter(adap_key);

                }
                else
                {
                    sp_key.setEnabled(true);
                    unit.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });



        //getList();
        String[] values= new String[1];
        values[0]= devs;

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

        sp_key.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item

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
                unit.setError(null);
                if (sp_type.getSelectedItem().toString().equals("Maps")) {
                    if (TextUtils.isEmpty(name.getText().toString())) {
                        name.setError("Wajib Diisi");
                        focusView = name;
                        Toast.makeText(getActivity(), "Silahkan Isi Nama Widget", Toast.LENGTH_SHORT).show();
                        cancel = true;
                    }
                    else
                    {
                        String usr= Preferences.getRegisteredUser(getActivity());
                        String dsc= desc.getText().toString();
                        if (TextUtils.isEmpty(dsc))
                            dsc="";
                        String parser = dbHelper.getparser(usr,sp_dev.getSelectedItem().toString());
                        dbHelper.insertWidget(usr,name.getText().toString(),dsc,sp_type.getSelectedItem().toString(),sp_dev.getSelectedItem().toString(),sp_key.getSelectedItem().toString(),unit.getText().toString(),parser);
                        Fragment frg = new fragmentListDevices();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.main_container, frg, "get");
                        ft.addToBackStack(null);
                        dbHelper.closeDB();
                        ft.commit();

                    }
                }

                else if (sp_type.getSelectedItem().toString().equals("Tracking")) {
                    if (TextUtils.isEmpty(name.getText().toString())) {
                        name.setError("Wajib Diisi");
                        focusView = name;
                        Toast.makeText(getActivity(), "Silahkan Isi Nama Widget", Toast.LENGTH_SHORT).show();
                        cancel = true;
                    }
                    else
                    {
                        String usr= Preferences.getRegisteredUser(getActivity());
                        String dsc= desc.getText().toString();
                        if (TextUtils.isEmpty(dsc))
                            dsc="";
                        String parser = dbHelper.getparser(usr,sp_dev.getSelectedItem().toString());
                        dbHelper.insertWidget(usr,name.getText().toString(),dsc,sp_type.getSelectedItem().toString(),sp_dev.getSelectedItem().toString(),sp_key.getSelectedItem().toString(),unit.getText().toString(),parser);
                        Fragment frg = new fragmentListDevices();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.main_container, frg, "get");
                        ft.addToBackStack(null);
                        dbHelper.closeDB();
                        ft.commit();

                    }
                }
                else {
                    if (TextUtils.isEmpty(name.getText().toString())) {
                        name.setError("Wajib Diisi");
                        focusView = name;
                        Toast.makeText(getActivity(), "Silahkan Isi Nama Widget", Toast.LENGTH_SHORT).show();
                        cancel = true;
                    }
                /* else if (TextUtils.isEmpty(unit.getText().toString())) {
                        unit.setError("Wajib Diisi");
                        focusView = unit;
                        Toast.makeText(getActivity(), "Silahkan Isi unit Widget", Toast.LENGTH_SHORT).show();
                        cancel = true;
                    }*/ else {
                        String usr = Preferences.getRegisteredUser(getActivity());
                        String dsc = desc.getText().toString();
                        if (TextUtils.isEmpty(dsc))
                            dsc = "";
                        String parser = dbHelper.getparser(usr, sp_dev.getSelectedItem().toString());
                        dbHelper.insertWidget(usr, name.getText().toString(), dsc, sp_type.getSelectedItem().toString(), sp_dev.getSelectedItem().toString(), sp_key.getSelectedItem().toString(), unit.getText().toString(), parser);
                        Fragment frg = new fragmentListDevices();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.main_container, frg, "get");
                        ft.addToBackStack(null);
                        dbHelper.closeDB();
                        ft.commit();
                    }
                }


            }
        });
        return  rootView;
    }

    private void getList()
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
                    obj.put("command", "getDevice");
                    obj.put("token_key",token);
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(200, TimeUnit.SECONDS)
                            .writeTimeout(200, TimeUnit.SECONDS)
                            .readTimeout(200, TimeUnit.SECONDS).build();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
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
                                                    String devs = result.getString("devList");
                                                    String results = devs.replaceAll("\\[", "").replaceAll("\\]", "");
                                                    String newStr = results.replace("\"", "");
                                                    if (TextUtils.isEmpty(newStr))
                                                    {
                                                        dialog.setContentView(R.layout.alert_error);
                                                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                        TextView txtContent = (TextView) dialog.findViewById(R.id.alertcontent);
                                                        txtContent.setText("DEVICE TIDAK DITEMUKAN");
                                                        dialog.show();
                                                        dialogButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                dialog.dismiss();
                                                                Fragment frg = new fragmentListDevices();
                                                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                                ft.replace(R.id.main_container, frg, "get");
                                                                ft.addToBackStack(null);
                                                                dbHelper.closeDB();
                                                                ft.commit();
                                                            }
                                                        });
                                                    }
                                                    else {
                                                        String[] values = newStr.split(",");

                                                        /*adap_dev = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, values);
                                                        adap_dev.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                        sp_dev.setAdapter(adap_dev);*/

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
