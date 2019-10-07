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

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class fragmentRegisterDev extends Fragment {

    private View rootView;
    private String data, name;
    private EditText devname;
    private Spinner sp,sp_parser;
    String[] txt_sp;
    private Button save;
    ArrayAdapter<String> adap_sp,adap_parser;
    DBHelper dbHelper;
    private Dialog dialog;
    private ProgressDialog dlg;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register_dev, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        devname= rootView.findViewById(R.id.txt_username);
        sp= rootView.findViewById(R.id.sp_prop);
        sp_parser= rootView.findViewById(R.id.sp_parser);
        save= rootView.findViewById(R.id.btn_login);
        context= getActivity();
        Bundle args = getArguments();
        if(args!=null)
        {
            name = getArguments().getString("name");
            data= getArguments().getString("dev");
        }
        dialog= new Dialog(context);
        getList();
        dbHelper = new DBHelper(context);
        dbHelper.openDB();
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        sp_parser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                devname.setError(null);
                boolean cancel = false;
                View focusView = null;
                if(TextUtils.isEmpty(devname.getText().toString()))
                {
                    devname.setError("Wajib Diisi");
                    focusView = devname;
                    Toast.makeText(getActivity(), "Silahkan Isi Nama Device", Toast.LENGTH_SHORT).show();
                    cancel = true;
                }

                else
                {
                    int a= dbHelper.getCountDevice(name,sp.getSelectedItem().toString());
                    if (a==0)
                    {
                        dbHelper.insertDevice(sp.getSelectedItem().toString(),sp_parser.getSelectedItem().toString(),devname.getText().toString(),name);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        Fragment frg = new fragmentListDevices();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        frg.setArguments(bundle);
                        ft.replace(R.id.main_container, frg, "get");
                        ft.addToBackStack(null);
                        dbHelper.closeDB();
                        ft.commit();
                    }
                    else
                    {
                        Toast.makeText(context, "DEVICE ID SUDAH TERDAFTAR", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        return rootView;
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

                                                            adap_sp = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, values);
                                                            adap_sp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                            sp.setAdapter(adap_sp);
                                                            getParser();
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

    private void getParser()
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
                    obj.put("command", "getParser");
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
                                                    String devs = result.getString("parserList");
                                                    String results = devs.replaceAll("\\[", "").replaceAll("\\]", "");
                                                    String newStr = results.replace("\"", "");
                                                    if (TextUtils.isEmpty(newStr))
                                                    {
                                                        dialog.setContentView(R.layout.alert_error);
                                                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                        TextView txtContent = (TextView) dialog.findViewById(R.id.alertcontent);
                                                        txtContent.setText("PARSER TIDAK DITEMUKAN");
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

                                                        adap_parser = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, values);
                                                        adap_parser.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                        sp_parser.setAdapter(adap_parser);
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
}
