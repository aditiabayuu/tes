package id.co.inti.pandawa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import id.co.inti.pandawa.adapter.addAdapter;
import id.co.inti.pandawa.adapter.listDevAdapter;
import id.co.inti.pandawa.dbhelper.DBHelper;
import id.co.inti.pandawa.item.RecyclerViewClickListener;
import id.co.inti.pandawa.item.item;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class fragmentListDevices  extends Fragment {

    private View rootView;
    private String data, name;
    private ImageButton add;
    private EditText nama;
    private TextView date;
    private TextView txt_name;
    private Dialog dialog;
    private ProgressDialog dlg;
    Context context;
    RecyclerView rv;
    private String [] dt;
    LinearLayoutManager layoutManager;
    listDevAdapter adapters;
    DBHelper dbHelper;
    private ArrayList<item> items = new ArrayList<item>();

    private String[] dev_id;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        add= rootView.findViewById(R.id.add_dev);
        txt_name=rootView.findViewById(R.id.txt_name);
        date = rootView.findViewById(R.id.last_login);
        Date c = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        //to convert Date to String, use format method of SimpleDateFormat class.
        String strDate = dateFormat.format(c);
        date.setText("Last Activities : "+strDate);
        rv = (RecyclerView) rootView.findViewById(R.id.rcv);
        context= getActivity();
        name= Preferences.getRegisteredUser(getActivity());
        txt_name.setText(name+"'s Home");
        dialog = new Dialog(context);

        items.clear();
        Bundle args = getArguments();
        if(args!=null)
        {
            name = getArguments().getString("name");

        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                Fragment frg = new fragmentRegisterDev();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                frg.setArguments(bundle);
                ft.replace(R.id.main_container, frg, "get");
                ft.addToBackStack(null);
                dbHelper.closeDB();
                ft.commit();
                //getFragmentManager().popBackStackImmediate();
            }
        });

        dialog= new Dialog(context);
        dbHelper = new DBHelper(context);
        dbHelper.openDB();
        dt = new String[5];
        Cursor cursor = dbHelper.getImageDevice(name);
        dev_id = new String[cursor.getCount()];
        int i =0;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                item it = new item();
                String name = cursor.getString(cursor.getColumnIndex("device_name"));
                String id=cursor.getString(cursor.getColumnIndex("id_device"));
                String parser=cursor.getString(cursor.getColumnIndex("parser"));
                dev_id[i]=id;
                it.setName(name);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("command", "getData");
                    obj.put("id_device",id);
                    obj.put("js",parser);
                    String data = post(obj.toString());
                    JSONObject js = new JSONObject(data);
                    JSONArray arrays=js.getJSONArray("data");
                    Log.d("tag","cot :"+arrays.length());
                    int a=0;
                    for (int j=0;j<arrays.length();j++)
                    {
                       JSONObject val= arrays.getJSONObject(j);
                       if (!val.getString("key").equals("") && val.has("value") && !val.getString("value").equals(""))
                       {
                           dt[a]=val.getString("key")+ ":" +val.getString("value") +val.getString("unit");
                           Log.d("tag",dt[a]);
                           a++;
                           Log.d("tag","Loop : "+a);
                       }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < dt.length; j++) {
                    if (dt[j]!=null) {
                        sb.append(dt[j] + "\n");
                    }
                }
                it.setTemp(sb.toString());
                byte[] byteArray = cursor.getBlob(cursor.getColumnIndex("image"));
                Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                it.setImage(bm);
                items.add(it);
                i++;
            }
        }
        adapters = new listDevAdapter(items, context,new RecyclerViewClickListener(){
            @Override
            public void onClick(View view, int position) {
                int i= (int) view.getTag() ;
                Log.d("tag","POINTER"+i);
                String title = dev_id[i];
                Log.d("tag",title);
                Bundle bundle = new Bundle();
                bundle.putString("dev",title);
                bundle.putString("name",name);
                Fragment frg = new fragmentdetildevicebak();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                frg.setArguments(bundle);
                ft.replace(R.id.main_container, frg, "get");
                ft.addToBackStack(null);
                dbHelper.closeDB();
                ft.commit();
              //  getFragmentManager().popBackStackImmediate();
            }
        });
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.removeAllViewsInLayout();
        rv.setAdapter(adapters);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);
        return rootView;
    }
    String js = null;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    String post( String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        String data=null;
        Request request = new Request.Builder()
                .url("http://202.51.233.100:7002")
                .post(body)
                .build();
        try  {
            Response response = client.newCall(request).execute();
            data=response.body().string();
            return data;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return  data;
    }


}
